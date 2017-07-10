package id.doelmi.keysmanager.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

public class DetailLogAktivitasActivity extends AppCompatActivity {
    public static final String ID_LOG = "IDLOG";

    LinearLayout kunci_layout;

    String deskripsi_kunci, gambar_kunci_uri;
    int gambar_kunci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_log_aktivitas);

        TextView nama = (TextView) findViewById(R.id.nama);
        TextView aktivitas = (TextView) findViewById(R.id.aktivitas);
        TextView kunci = (TextView) findViewById(R.id.kunci);
        TextView waktu = (TextView) findViewById(R.id.waktu);
        TextView catat = (TextView) findViewById(R.id.catat);

        kunci_layout = (LinearLayout) findViewById(R.id.kunci_layout);

        int id_log = (Integer) getIntent().getExtras().get(ID_LOG);

        String nama_ = null;
        String aktivitas_ = null;
        String _kunci = null;
        String waktu_ = null;
        String catat_ = null;

        String aktif = null;

        String kunci_aktif = null;

        try {
            SQLiteOpenHelper helper = new SQLiteDBHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "PENGAMBILAN_KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_PENGAMBIL_KUNCI", "NO_ID_PENGAMBIL", "NAMA_KUNCI_AMBIL", "TANGGAL_AMBIL", "WAKTU_AMBIL"}, //Select Tabel
                    "ID_LOG = ?", //Where clause
                    new String[]{Integer.toString(id_log)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor.moveToNext()) {
                nama_ = cursor.getString(1);
                _kunci = cursor.getString(3);
                waktu_ = cursor.getString(4) + " " + cursor.getString(5);
                aktivitas_ = "Pengambilan Kunci";

            }
            Cursor cursor2 = db.query(
                    "PENGEMBALIAN_KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_PENGEMBALI_KUNCI", "NO_ID_PENGEMBALI", "NAMA_KUNCI_KEMBALI", "TANGGAL_KEMBALI", "WAKTU_KEMBALI"}, //Select Tabel
                    "ID_LOG = ?", //Where clause
                    new String[]{Integer.toString(id_log)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor2.moveToNext()) {
                nama_ = cursor2.getString(1);
                _kunci = cursor2.getString(3);
                waktu_ = cursor2.getString(4) + " " + cursor2.getString(5);
                aktivitas_ = "Pengembalian Kunci";
            }

            Cursor cursor4 = db.query(
                    "LOG_AKTIVITAS", //Select Tabel
                    new String[]{"_id", "WAKTU", "AKTIVITAS", "KUNCI"}, //Select Tabel
                    "_id = ?", //Where clause
                    new String[]{Integer.toString(id_log)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor4.moveToNext()) {
                catat_ = cursor4.getString(1);
                aktif = cursor4.getString(2);
                kunci_aktif = cursor4.getString(3);
            }
            if (waktu_ == null) {
                waktu_ = "~";
                waktu.setTextColor(Color.RED);
            } else {
                String tahun = waktu_.substring(0, 4);
                String bulan = waktu_.substring(5, 7);
                String tanggal = waktu_.substring(8, 10);
                String jam = waktu_.substring(11);

                waktu_ = Integer.parseInt(tanggal) + " " + cariBulan(Integer.parseInt(bulan)) + " " + tahun + " " + jam;
            }
            if (nama_ == null) {
                nama_ = "Anda";
                nama.setTextColor(Color.BLUE);
            }
            if (aktivitas_ == null) {
                if (aktif.contains("menambah")) {
                    aktivitas_ = "Penambahan Kunci";
                } else if (aktif.contains("mengarsipkan")) {
                    aktivitas_ = "Pengarsipan Kunci";
                } else if (aktif.contains("membatalkan")) {
                    aktivitas_ = "Pembatalan Arsip Kunci";
                } else if (aktif.contains("memperbarui")) {
                    aktivitas_ = "Pembaruan Kunci";
                }
            }
            if (_kunci == null) {
                _kunci = kunci_aktif;
            }

            nama.setText(nama_);
            aktivitas.setText(aktivitas_);
            kunci.setText(_kunci);
            waktu.setText(waktu_);

            String tahun = catat_.substring(0, 4);
            String bulan = catat_.substring(5, 7);
            String tanggal = catat_.substring(8, 10);
            String jam = catat_.substring(11);

            catat_ = Integer.parseInt(tanggal) + " " + cariBulan(Integer.parseInt(bulan)) + " " + tahun + " " + jam;

            catat.setText(catat_);

            Cursor cursor_kunci = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "GAMBAR_KUNCI", "GAMBAR_KUNCI_URI"}, //Select Tabel
                    "NAMA_KUNCI = ?", //Where clause
                    new String[]{_kunci}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor_kunci.moveToNext()) {
                deskripsi_kunci = cursor_kunci.getString(2);
                gambar_kunci = cursor_kunci.getInt(3);
                gambar_kunci_uri = cursor_kunci.getString(4);
            }


            cursor.close();
            cursor2.close();
            cursor4.close();
            cursor_kunci.close();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }

        final String final_kunci = _kunci;
        kunci_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DetailLogAktivitasActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
//                dialog.setTitle("Informasi Kunci");
                CircleImageView circleImageView = (CircleImageView) dialog.findViewById(R.id.image_dialog);
                TextView nama_kunci = (TextView) dialog.findViewById(R.id.nama_kunci);
                TextView deskripsi_kunci_txt = (TextView) dialog.findViewById(R.id.deskripsi_kunci);

                nama_kunci.setText(final_kunci);
                deskripsi_kunci_txt.setText(deskripsi_kunci);
                if (gambar_kunci_uri != null && !gambar_kunci_uri.contains("provider")) {
                    Uri uri = Uri.parse(gambar_kunci_uri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    circleImageView.setImageBitmap(bitmap);
                } else if (gambar_kunci != 0) {
                    circleImageView.setImageResource(gambar_kunci);
                } else {
                    circleImageView.setImageResource(R.drawable.ic_launcher);
                }

                dialog.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(8);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
