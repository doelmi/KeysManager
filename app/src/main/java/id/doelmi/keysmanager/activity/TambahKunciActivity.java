package id.doelmi.keysmanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

public class TambahKunciActivity extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    Uri uri = null;
    SQLiteOpenHelper helper;

    ArrayList<String> listKunci = new ArrayList<>();

    boolean error_sama = false;
    ImageView imageView;

    int finalHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kunci);
        Button btn_select = (Button) findViewById(R.id.btn_select);
        FloatingActionButton btn_2 = (FloatingActionButton) findViewById(R.id.fab);
        final EditText nama_kunci = (EditText) findViewById(R.id.nama_kunci);
        final EditText deskripsi_kunci = (EditText) findViewById(R.id.deskripsi_kunci);
        imageView = (ImageView) findViewById(R.id.imageView2);

        helper = new SQLiteDBHelper(this);
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI"}, //Select Tabel
                    null, //Where clause
                    null, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            while (cursor.moveToNext()) {
                String namaKunci = cursor.getString(1);
                listKunci.add(namaKunci.toLowerCase());
            }

            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }

        nama_kunci.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listKunci.contains(nama_kunci.getText().toString().toLowerCase())) {
                    nama_kunci.setError("Kunci sudah ada!");
                    error_sama = true;
                } else {
                    error_sama = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ID"));
        final String date = dateFormat.format(new Date());

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = nama_kunci.getText().toString().toUpperCase();
                String deskripsi = deskripsi_kunci.getText().toString();

                String gambar_uri = null;

                if (uri != null) {
                    gambar_uri = uri.toString();
                }
                if (nama.length() < 1) {
                    nama_kunci.setError("Nama kunci harus diisi!");
                    nama_kunci.setFocusable(true);
                } else if (error_sama) {
                    nama_kunci.setFocusable(true);
                } else {
                    try {
                        SQLiteDatabase db = helper.getWritableDatabase();

                        db.insert("KUNCI", null, InsertKunci(nama, deskripsi, 0, gambar_uri, 0, null, null, null));
                        db.insert("LOG_AKTIVITAS", null, InsertLogAktivitas("Anda menambahkan kunci " + nama, date, nama));
                        db.close();

                        AlertDialog.Builder builder = new AlertDialog.Builder(TambahKunciActivity.this);
                        builder.setMessage("Kunci Berhasil Ditambahkan")
                                .setTitle("Berhasil!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                    TambahKunciActivity.super.onBackPressed();
                                        Intent intent = new Intent(TambahKunciActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        ActivityCompat.finishAffinity(TambahKunciActivity.this);
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    } catch (SQLiteException e) {
                        Toast.makeText(TambahKunciActivity.this, e + "", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(8);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private ContentValues InsertKunci(String nama_kunci, String deskripsi_kunci, int gambar_kunci, String gambar_uri, int status, String dibawa_oleh, String waktu, String tanggal) {
        ContentValues values = new ContentValues();
        values.put("NAMA_KUNCI", nama_kunci);
        values.put("DESKRIPSI_KUNCI", deskripsi_kunci);
        values.put("GAMBAR_KUNCI", gambar_kunci);
        values.put("GAMBAR_KUNCI_URI", gambar_uri);
        values.put("STATUS", status);
        values.put("DIBAWA_OLEH", dibawa_oleh);
        values.put("WAKTU", waktu);
        values.put("TANGGAL", tanggal);
        values.put("DIARSIPKAN", 0);
        return values;
    }

    private ContentValues InsertLogAktivitas(String AKTIVITAS, String WAKTU, String KUNCI) {
        ContentValues values = new ContentValues();
        values.put("AKTIVITAS", AKTIVITAS);
        values.put("WAKTU", WAKTU);
        values.put("KUNCI", KUNCI);
        return values;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        finalHeight = imageView.getWidth();
        imageView.getLayoutParams().height = finalHeight;

    }
}
