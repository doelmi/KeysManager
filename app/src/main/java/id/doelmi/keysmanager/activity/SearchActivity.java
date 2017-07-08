package id.doelmi.keysmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;
import id.doelmi.keysmanager.javafile.CustomAdapter;
import id.doelmi.keysmanager.javafile.CustomAdapterLog;
import id.doelmi.keysmanager.javafile.CustomPOJO;

public class SearchActivity extends AppCompatActivity {
    SQLiteOpenHelper helper;
    SQLiteDatabase db;

    private Cursor cursor;

    RecyclerView recyclerView, recyclerView2;
    CustomAdapter adapter;
    CustomAdapterLog adapterLog;
    private ArrayList<CustomPOJO> listContentArr = new ArrayList<>();
    private ArrayList<CustomPOJO> listContentArrLog = new ArrayList<>();
    ListView listView;
    Context activity;
    EditText search;

    TextView error, labelKunci, labelLog;

    CoordinatorLayout coor1, coor2;

    private void gone_all() {
        error.setVisibility(View.GONE);
        labelKunci.setVisibility(View.GONE);
        labelLog.setVisibility(View.GONE);
        coor1.setVisibility(View.GONE);
        coor2.setVisibility(View.GONE);
    }

    boolean hasSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        error = (TextView) findViewById(R.id.error);
        labelKunci = (TextView) findViewById(R.id.labelKunci);
        labelLog = (TextView) findViewById(R.id.labelLog);

        coor1 = (CoordinatorLayout) findViewById(R.id.coor1);
        coor2 = (CoordinatorLayout) findViewById(R.id.coor2);

        gone_all();

        try {
            helper = new SQLiteDBHelper(this);
            db = helper.getReadableDatabase();
        } catch (SQLiteException e) {
            Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
        }


        //RecyclerView
        activity = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomAdapter(activity);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomPOJO list_items = listContentArr.get(position);
                int id = list_items.getId();
                PindahDetail(id);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(activity, "Long Press on position : " + position, Toast.LENGTH_SHORT).show();
            }
        }));

        recyclerView2 = (RecyclerView) findViewById(R.id.recycleView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        adapterLog = new CustomAdapterLog(activity);

        recyclerView2.addOnItemTouchListener(new RecyclerTouchListener(activity, recyclerView2, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomPOJO list_items = listContentArrLog.get(position);
                int id = list_items.getId();
                PindahLog(id);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(activity, "Long Press on position : " + position, Toast.LENGTH_SHORT).show();
            }
        }));

        //RecyclerView Tutup
        ActionBar actionBar = getSupportActionBar();

        actionBar.setCustomView(R.layout.actionbar_view);

        search = (EditText) actionBar.getCustomView().findViewById(R.id.searchField);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String cari = search.getText().toString();
                listContentArr.clear();
                listContentArrLog.clear();
                populateRecylerViewValues(cari);
                populateRecylerViewValuesLog(cari);
                kondisi_tampil();
                hasSearch = true;
                return false;
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

        getSupportActionBar().setElevation(8);
    }

    private void kondisi_tampil() {
        if (!listContentArr.isEmpty()) {
            labelKunci.setVisibility(View.VISIBLE);
            coor1.setVisibility(View.VISIBLE);
        } else if (listContentArr.isEmpty()) {
            labelKunci.setVisibility(View.GONE);
            coor1.setVisibility(View.GONE);
        }
        if (!listContentArrLog.isEmpty()) {
            labelLog.setVisibility(View.VISIBLE);
            coor2.setVisibility(View.VISIBLE);
        } else if (listContentArrLog.isEmpty()) {
            labelLog.setVisibility(View.GONE);
            coor2.setVisibility(View.GONE);
        }

        if (listContentArr.isEmpty() && listContentArrLog.isEmpty()) {
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }


    private void populateRecylerViewValues(String cari) {
        try {
            helper = new SQLiteDBHelper(this);
            db = helper.getReadableDatabase();

            cursor = db.query(
                    "KUNCI",
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "STATUS", "GAMBAR_KUNCI", "GAMBAR_KUNCI_URI"},
                    "DIARSIPKAN = ? AND ( NAMA_KUNCI LIKE ? OR DESKRIPSI_KUNCI LIKE ? )",
                    new String[]{Integer.toString(0), "%" + cari + "%", "%" + cari + "%"},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                CustomPOJO customPOJO = new CustomPOJO();
                int id = cursor.getInt(0);
                String nama = cursor.getString(1);
                String kunci = cursor.getString(2);
                String status = cursor.getString(3);
                String gambar = cursor.getString(4);
                if (gambar.equals("0")) {
                    gambar = cursor.getString(5);
                }
                if (status.equals("0")) {
                    status = "Ada";
                } else {
                    status = "Tidak Ada";
                }
                customPOJO.setName(nama);
                customPOJO.setContent(kunci);
                customPOJO.setTime(status);
                customPOJO.setId(id);
                customPOJO.setGambar(gambar);

                listContentArr.add(customPOJO);
            }

        } catch (SQLiteException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }

        adapter.setListContent(listContentArr);

        recyclerView.setAdapter(adapter);
    }

    private String cari_tanggal(String cari) {
        String cari_split[] = cari.split(" ");
        String cari2 = "";
        if (cari_split.length == 1) {
            if (cari_split[0].length() == 1) {
                cari2 = "-0" + cari_split[0];
            } else if (cari_split[0].length() == 2) {
                cari2 = "-" + cari_split[0];
            } else if (cari_split[0].length() == 4 && cari_split[0].contains("2")) {
                cari2 = cari_split[0] + "-";
            } else {
                cari2 = "-" + cariBulantoInt(cari_split[0]) + "-";
            }
        } else if (cari_split.length == 2) {
            if (cari_split[0].length() == 1) {
                cari_split[0] = "-0" + cari_split[0];
                cari2 = "-" + cariBulantoInt(cari_split[1]) + cari_split[0];
            } else if (cari_split[0].length() == 2) {
                cari_split[0] = "-" + cari_split[0];
                cari2 = "-" + cariBulantoInt(cari_split[1]) + cari_split[0];
            } else if (cari_split[1].length() == 4 && cari_split[1].contains("2")) {
                cari2 = cari_split[1] + "-" + cariBulantoInt(cari_split[0]) + "-";
            } else cari2 = "data nggak ada";
        } else if (cari_split.length == 3) {
            if (cari_split[0].length() == 1) {
                cari2 = cari_split[2] + "-" + cariBulantoInt(cari_split[1]) + "-0" + cari_split[0];
            } else if (cari_split[0].length() == 2) {
                cari2 = cari_split[2] + "-" + cariBulantoInt(cari_split[1]) + "-" + cari_split[0];
            } else cari2 = "data nggak ada";
        }
        return cari2;
    }

    private void populateRecylerViewValuesLog(String cari) {
        String cari2 = cari_tanggal(cari);
        try {
            helper = new SQLiteDBHelper(this);
            db = helper.getReadableDatabase();

            cursor = db.query(
                    "LOG_AKTIVITAS",
                    new String[]{"_id", "AKTIVITAS", "WAKTU"},
                    "AKTIVITAS LIKE ? OR WAKTU LIKE ?",
                    new String[]{"%" + cari + "%", "%" + cari2 + "%"},
                    null,
                    null,
                    "WAKTU DESC"
            );
            while (cursor.moveToNext()) {
                CustomPOJO customPOJO = new CustomPOJO();
                int id = cursor.getInt(0);
                String aktivitas = cursor.getString(1);
                String waktu = cursor.getString(2);

                String tahun = waktu.substring(0, 4);
                String bulan = waktu.substring(5, 7);
                String tanggal = waktu.substring(8, 10);
                String jam = waktu.substring(11);

                waktu = Integer.parseInt(tanggal) + " " + cariBulan(Integer.parseInt(bulan)) + " " + tahun + " " + jam;

                customPOJO.setName(aktivitas);
                customPOJO.setContent(waktu);
                customPOJO.setId(id);

                listContentArrLog.add(customPOJO);
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }

        adapterLog.setListContent(listContentArrLog);

        recyclerView2.setAdapter(adapterLog);
    }

    private String cariBulan(int bulan) {
        switch (bulan) {
            case 1:
                return "Januari";
            case 2:
                return "Februari";
            case 3:
                return "Maret";
            case 4:
                return "April";
            case 5:
                return "Mei";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "Agustus";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "November";
            case 12:
                return "Desember";
            default:
                return null;
        }
    }

    private String cariBulantoInt(String bulan) {
        switch (bulan) {
            case "januari":
                return "01";
            case "februari":
                return "02";
            case "maret":
                return "03";
            case "april":
                return "04";
            case "mei":
                return "05";
            case "juni":
                return "06";
            case "juli":
                return "07";
            case "agustus":
                return "08";
            case "september":
                return "09";
            case "oktober":
                return "10";
            case "november":
                return "11";
            case "desember":
                return "12";
            default:
                return bulan;
        }
    }

    public void PindahDetail(int id) {
        Intent intent = new Intent(this, DetailKunciActivity.class);
        intent.putExtra(DetailKunciActivity.ID_KUNCI, id);
        intent.putExtra(DetailKunciActivity.DIARSIPKAN, 0);
        startActivity(intent);
    }

    public void PindahLog(int id) {
        Intent intent = new Intent(this, DetailLogAktivitasActivity.class);
        intent.putExtra(DetailLogAktivitasActivity.ID_LOG, id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasSearch) {
            gone_all();
            listContentArrLog.clear();
            listContentArr.clear();
            populateRecylerViewValues(search.getText().toString());
            populateRecylerViewValuesLog(search.getText().toString());
            kondisi_tampil();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
        db.close();
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
