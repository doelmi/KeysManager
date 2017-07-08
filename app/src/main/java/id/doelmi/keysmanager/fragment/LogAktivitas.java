package id.doelmi.keysmanager.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.activity.DetailKunciActivity;
import id.doelmi.keysmanager.activity.DetailLogAktivitasActivity;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;
import id.doelmi.keysmanager.javafile.CustomAdapter;
import id.doelmi.keysmanager.javafile.CustomAdapterLog;
import id.doelmi.keysmanager.javafile.CustomPOJO;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogAktivitas extends Fragment implements AdapterView.OnItemClickListener {
    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private Cursor cursor;

    RecyclerView recyclerView;
    CustomAdapterLog adapter;
    private ArrayList<CustomPOJO> listContentArr = new ArrayList<>();
    ListView listView;
    Context activity;

    public LogAktivitas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_log_aktivitas, container, false);

        activity = this.getActivity();
        recyclerView = (RecyclerView) v.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomAdapterLog(activity);

        populateRecylerViewValues();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity, recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Pindah(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return v;
    }

    public void Pindah(int id) {
        Intent intent = new Intent(this.getActivity(), DetailLogAktivitasActivity.class);
        intent.putExtra(DetailLogAktivitasActivity.ID_LOG, id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Pindah((int)id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        database.close();
    }

    private void populateRecylerViewValues() {
        try {
            helper = new SQLiteDBHelper(this.getActivity());
            database = helper.getReadableDatabase();

            cursor = database.query(
                    "LOG_AKTIVITAS",
                    new String[]{"_id", "AKTIVITAS", "WAKTU"},
                    null,
                    null,
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

                listContentArr.add(customPOJO);
            }
        } catch (SQLiteException e) {
            Toast.makeText(this.getActivity(), "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }

        adapter.setListContent(listContentArr);

        recyclerView.setAdapter(adapter);
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

    @Override
    public void onResume() {
        super.onResume();
        listContentArr.clear();
        populateRecylerViewValues();
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
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
                CustomPOJO list_items = listContentArr.get(rv.getChildAdapterPosition(child));
                clickListener.onClick(child, list_items.getId());
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
