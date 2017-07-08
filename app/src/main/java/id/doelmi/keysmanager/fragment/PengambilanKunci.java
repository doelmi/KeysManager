package id.doelmi.keysmanager.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.activity.AmbilKunciActivity;
import id.doelmi.keysmanager.activity.DetailKunciActivity;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PengambilanKunci extends Fragment implements AdapterView.OnItemClickListener {
    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private Cursor cursor;

    public PengambilanKunci() {
        // Required empty public constructor
    }

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_pengambilan_kunci, container, false);

        listView = (ListView) v.findViewById(R.id.pengambilan_kunci_list);
        try {
            helper = new SQLiteDBHelper(this.getActivity());
            database = helper.getReadableDatabase();

            cursor = database.query(
                    "KUNCI",
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI"},
                    "STATUS = ?",
                    new String[]{Integer.toString(0)},
                    null,
                    null,
                    null
            );
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    this.getActivity(),
                    android.R.layout.simple_expandable_list_item_2,
                    cursor,
                    new String[]{"NAMA_KUNCI", "DESKRIPSI_KUNCI"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );

            listView.setAdapter(cursorAdapter);
        } catch (SQLiteException e) {
            Toast.makeText(this.getActivity(), "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }
        listView.setOnItemClickListener(this);
        return v;
    }

    public void Pindah(int id) {
        Intent intent = new Intent(this.getActivity(), AmbilKunciActivity.class);
        intent.putExtra(AmbilKunciActivity.ID_KUNCI, id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pindah((int) id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        database.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            helper = new SQLiteDBHelper(this.getActivity());
            database = helper.getReadableDatabase();

            Cursor newcursor = database.query(
                    "KUNCI",
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI"},
                    "STATUS = ?",
                    new String[]{Integer.toString(0)},
                    null,
                    null,
                    null
            );
            CursorAdapter cursorAdapter = (CursorAdapter) listView.getAdapter();

            cursorAdapter.changeCursor(newcursor);
            cursor = newcursor;
        } catch (SQLiteException e) {
            Toast.makeText(this.getActivity(), "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
