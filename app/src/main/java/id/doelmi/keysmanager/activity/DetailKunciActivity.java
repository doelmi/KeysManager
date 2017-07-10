package id.doelmi.keysmanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

public class DetailKunciActivity extends AppCompatActivity {

    public static final String ID_KUNCI = "IDKUNCI";
    public static final String DIARSIPKAN = "DIARSIPKAN";

    static String nama_kunci_public = null;
    SQLiteOpenHelper helper;
    int id_kunci, diarsipkan;

    TextView nama_kunci, aktivitas_kunci;
    TextView deskripsi_kunci;
    TextView status_kunci;
    TextView dibawa_oleh;
    TextView waktu_kunci;

    ImageView  imageView3;
CircleImageView gambar_kunci;
    TextView dibawaOleh_t;
    TextView waktu_kunci_t;

    LinearLayout ambil_kembali_layout, aktivitas_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kunci);
        nama_kunci = (TextView) findViewById(R.id.nama_kunci);
        deskripsi_kunci = (TextView) findViewById(R.id.deskripsi_kunci);
        status_kunci = (TextView) findViewById(R.id.status_kunci);
        dibawa_oleh = (TextView) findViewById(R.id.dibawa_oleh);
        waktu_kunci = (TextView) findViewById(R.id.waktu_kunci);
        gambar_kunci = (CircleImageView) findViewById(R.id.imageView);
        gambar_kunci.setImageResource(R.drawable.ic_launcher);
        dibawaOleh_t = (TextView) findViewById(R.id.textView6);
        waktu_kunci_t = (TextView) findViewById(R.id.textView2);

        ambil_kembali_layout = (LinearLayout) findViewById(R.id.ambil_kembali_layout);
        aktivitas_layout = (LinearLayout) findViewById(R.id.aktivitas_layout);
        aktivitas_kunci = (TextView) findViewById(R.id.aktivitas_kunci);
        imageView3 = (ImageView) findViewById(R.id.imageView3);

        id_kunci = (Integer) getIntent().getExtras().get(ID_KUNCI);
        diarsipkan = (Integer) getIntent().getExtras().get(DIARSIPKAN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateDetailKunci();
    }

    private void populateDetailKunci() {
        try {
            helper = new SQLiteDBHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "GAMBAR_KUNCI", "STATUS", "DIBAWA_OLEH", "WAKTU", "TANGGAL", "GAMBAR_KUNCI_URI"}, //Select Tabel
                    "_id = ?", //Where clause
                    new String[]{Integer.toString(id_kunci)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor.moveToFirst()) {
                String namaKunci = cursor.getString(1);

                nama_kunci_public = namaKunci;

                String deskripsiKunci = cursor.getString(2);
                int gambarKunci = cursor.getInt(3);
                int statusKunci = cursor.getInt(4);
                String DibawaOleh = cursor.getString(5);
                String Waktu = cursor.getString(7) + " " + cursor.getString(6);

                String uri_ = cursor.getString(8);


                if (Waktu.contains("null")) {
                    Waktu = "null";
                } else {
                    String tahun = Waktu.substring(0, 4);
                    String bulan = Waktu.substring(5, 7);
                    String tanggal = Waktu.substring(8, 10);
                    String jam = Waktu.substring(11);
                    Waktu = Integer.parseInt(tanggal) + " " + cariBulan(Integer.parseInt(bulan)) + " " + tahun + " " + jam;
                }
                waktu_kunci.setText(Waktu);
                if (uri_ != null && !uri_.contains("provider")) {
                    Uri uri = Uri.parse(uri_);
//                    Toast.makeText(this, uri + "", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    gambar_kunci.setImageBitmap(bitmap);
                } else if (gambarKunci != 0) {
                    gambar_kunci.setImageResource(gambarKunci);
                } else {
                    gambar_kunci.setImageResource(R.drawable.ic_launcher);
                }

                nama_kunci.setText(namaKunci);
                deskripsi_kunci.setText(deskripsiKunci);
                if (statusKunci == 0) {
                    status_kunci.setText("Ada");
                    status_kunci.setTextColor(Color.GREEN);
                    dibawaOleh_t.setText("Dikembalikan oleh : ");
                    dibawa_oleh.setText(DibawaOleh + "");

                    ambil_kembali_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PindahAmbil(id_kunci);
                        }
                    });
                    aktivitas_kunci.setText("Ambil Kunci");
                    imageView3.setImageResource(R.drawable.keluar_round);

                } else {
                    status_kunci.setText("Tidak Ada");
                    status_kunci.setTextColor(Color.RED);
                    dibawaOleh_t.setText("Dibawa oleh : ");
                    dibawa_oleh.setText(DibawaOleh + "");

                    ambil_kembali_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PindahKembali(id_kunci);
                        }
                    });
                    aktivitas_kunci.setText("Kembali Kunci");
                    imageView3.setImageResource(R.drawable.masuk_round);

                }

            }

            cursor.close();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Uri Error : " + e, Toast.LENGTH_SHORT).show();
        }

        if (diarsipkan != 0) {
            aktivitas_layout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Detail Kunci Diarsipkan");
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (diarsipkan == 0) {
            getMenuInflater().inflate(R.menu.menu_detail_kunci, menu);
        } else if (diarsipkan == 1) {
            getMenuInflater().inflate(R.menu.menu_detail_kunci_arsip, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void PindahAmbil(int id) {
        Intent intent = new Intent(this, AmbilKunciActivity.class);
        intent.putExtra(AmbilKunciActivity.ID_KUNCI, id);
        startActivity(intent);
    }

    public void PindahKembali(int id) {
        Intent intent = new Intent(this, KembaliKunciActivity.class);
        intent.putExtra(KembaliKunciActivity.ID_KUNCI, id);
        startActivity(intent);
    }

    private void hapus_data(int id) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues update = new ContentValues();
            update.put("DIARSIPKAN", 1);

            db.update("KUNCI", update, "_id = ?", new String[]{Integer.toString(id)});
//            db.delete("KUNCI", "_id = ?", new String[]{Integer.toString(id)});
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
        }
    }

    private void hapus_arsip_data(int id) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues update = new ContentValues();
            update.put("DIARSIPKAN", 0);

            db.update("KUNCI", update, "_id = ?", new String[]{Integer.toString(id)});
//            db.delete("KUNCI", "_id = ?", new String[]{Integer.toString(id)});
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertLog(String aktivitas) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ID"));
        final String date = dateFormat.format(new Date());
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("LOG_AKTIVITAS", null, InsertLogAktivitas("Anda " + aktivitas + " kunci " + nama_kunci_public, date, nama_kunci_public));
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailKunciActivity.this);
                builder.setMessage("Apakah Anda yakin untuk mengarsipkan kunci " + nama_kunci_public + "?")
                        .setTitle("Konfirmasi")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hapus_data(id_kunci);
                                insertLog("mengarsipkan");
//                                Intent intent = new Intent(DetailKunciActivity.this, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                ActivityCompat.finishAffinity(DetailKunciActivity.this);
                                DetailKunciActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_batal_arsip:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailKunciActivity.this);
                builder2.setMessage("Apakah Anda yakin untuk membatalkan arsip kunci " + nama_kunci_public + "?")
                        .setTitle("Konfirmasi")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hapus_arsip_data(id_kunci);
                                insertLog("membatalkan arsip");
                                DetailKunciActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(DetailKunciActivity.this, EditKunciActivity.class);
                intent.putExtra(EditKunciActivity.ID_KUNCI, id_kunci);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues InsertLogAktivitas(String AKTIVITAS, String WAKTU, String KUNCI) {
        ContentValues values = new ContentValues();
        values.put("AKTIVITAS", AKTIVITAS);
        values.put("WAKTU", WAKTU);
        values.put("KUNCI", KUNCI);
        return values;
    }
}
