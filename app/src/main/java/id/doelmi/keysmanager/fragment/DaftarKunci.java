package id.doelmi.keysmanager.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.activity.DetailKunciActivity;
import id.doelmi.keysmanager.activity.TambahKunciActivity;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;
import id.doelmi.keysmanager.javafile.CustomAdapter;
import id.doelmi.keysmanager.javafile.CustomPOJO;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaftarKunci extends Fragment {
    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private Cursor cursor;

    public DaftarKunci() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    CustomAdapter adapter;
    private ArrayList<CustomPOJO> listContentArr = new ArrayList<>();
    Context activity;

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_daftar_kunci, container, false);
        //Recycler View
        activity = this.getActivity();
        recyclerView = (RecyclerView) v.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setHasFixedSize(true);
        adapter = new CustomAdapter(activity);

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

        textView = (TextView) v.findViewById(R.id.tidak_ada);

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TambahKunciActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void Pindah(int id) {
        Intent intent = new Intent(this.getActivity(), DetailKunciActivity.class);
        intent.putExtra(DetailKunciActivity.ID_KUNCI, id);
        intent.putExtra(DetailKunciActivity.DIARSIPKAN, 0);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void populateRecylerViewValues() {
        try {
            helper = new SQLiteDBHelper(this.getActivity());
            database = helper.getReadableDatabase();

            cursor = database.query(
                    "KUNCI",
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "STATUS", "GAMBAR_KUNCI", "GAMBAR_KUNCI_URI", "PATH"},
                    "DIARSIPKAN = ?",
                    new String[]{Integer.toString(0)},
                    null,
                    null,
                    "NAMA_KUNCI ASC"
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
                String path = cursor.getString(6);

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
                customPOJO.setPath(path);

                listContentArr.add(customPOJO);
            }
        } catch (SQLiteException e) {
            Toast.makeText(this.getActivity(), "Database Error : " + e, Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            database.close();
            helper.close();
        }

        adapter.setListContent(listContentArr);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        listContentArr.clear();
        populateRecylerViewValues();

        if (listContentArr.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
        }
    }

    private interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        private RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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
