package id.doelmi.keysmanager.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.doelmi.keysmanager.R;

/**
 * Created by abdul on 31/05/2017.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "keysmanager";
    private static final int DB_VERSION = 1;

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

    private ContentValues InsertKunci(String nama_kunci, String deskripsi_kunci, int gambar_kunci, String gambar_uri, int status, String dibawa_oleh, String waktu, String tanggal, int diarsipkan) {
        ContentValues values = new ContentValues();
        values.put("NAMA_KUNCI", nama_kunci);
        values.put("DESKRIPSI_KUNCI", deskripsi_kunci);
        values.put("GAMBAR_KUNCI", gambar_kunci);
        values.put("GAMBAR_KUNCI_URI", gambar_uri);
        values.put("STATUS", status);
        values.put("DIBAWA_OLEH", dibawa_oleh);
        values.put("WAKTU", waktu);
        values.put("TANGGAL", tanggal);
        values.put("DIARSIPKAN", diarsipkan);
        return values;
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            String SQL = "CREATE TABLE KUNCI (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAMA_KUNCI TEXT, DESKRIPSI_KUNCI TEXT, GAMBAR_KUNCI INTEGER, GAMBAR_KUNCI_URI TEXT, STATUS INTEGER, DIBAWA_OLEH TEXT, WAKTU TEXT, TANGGAL DATE, DIARSIPKAN INTEGER, PATH TEXT);";
            db.execSQL(SQL);
            SQL = "CREATE TABLE PENGAMBILAN_KUNCI (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAMA_PENGAMBIL_KUNCI TEXT, NO_ID_PENGAMBIL TEXT, NAMA_KUNCI_AMBIL TEXT, ID_KUNCI INTEGER, TANGGAL_AMBIL DATE, WAKTU_AMBIL TEXT, ID_LOG INTEGER);";
            db.execSQL(SQL);
            SQL = "CREATE TABLE PENGEMBALIAN_KUNCI (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAMA_PENGEMBALI_KUNCI TEXT, NO_ID_PENGEMBALI TEXT, NAMA_KUNCI_KEMBALI TEXT, ID_KUNCI INTEGER, TANGGAL_KEMBALI DATE, WAKTU_KEMBALI TEXT, ID_LOG INTEGER);";
            db.execSQL(SQL);
            SQL = "CREATE TABLE LOG_AKTIVITAS (_id INTEGER PRIMARY KEY AUTOINCREMENT, AKTIVITAS TEXT, WAKTU TEXT, KUNCI TEXT, ID_KUNCI INTEGER);";
            db.execSQL(SQL);

            db.insert("KUNCI", null, InsertKunci("L3R1", "Kunci untuk LabTIA", R.drawable.l3r1, null, 0, null, null, null, 0));
            db.insert("KUNCI", null, InsertKunci("L3R2", "Kunci untuk LabCC", R.drawable.l3r2, null, 0, null, null, null, 0));
            db.insert("KUNCI", null, InsertKunci("L2R1", "Kunci untuk Lab Multimedia dan SI", R.drawable.l3r1, null, 0, null, null, null, 0));
            db.insert("KUNCI", null, InsertKunci("L2R2", "Kunci untuk Lab CAI dan SISTER", R.drawable.l3r2, null, 0, null, null, null, 0));
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

}
