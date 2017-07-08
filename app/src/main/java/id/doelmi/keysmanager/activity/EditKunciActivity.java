package id.doelmi.keysmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

public class EditKunciActivity extends AppCompatActivity {
    public static final String ID_KUNCI = "IDKUNCI";
    private static int PICK_IMAGE_REQUEST = 1;
    Uri uri = null;
    SQLiteOpenHelper helper;

    int id_kunci;

    boolean gambar_diganti = false;

    ArrayList<String> listKunci = new ArrayList<>();
    int gambarKunci;

    boolean error_sama = false;

    String namaKunci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kunci);

        helper = new SQLiteDBHelper(this);

        Button btn_select = (Button) findViewById(R.id.btn_select);
        Button btn_2 = (Button) findViewById(R.id.button2);
        final EditText nama_kunci = (EditText) findViewById(R.id.nama_kunci);
        final EditText deskripsi_kunci = (EditText) findViewById(R.id.deskripsi_kunci);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);


        id_kunci = (Integer) getIntent().getExtras().get(ID_KUNCI);


        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "GAMBAR_KUNCI", "GAMBAR_KUNCI_URI"}, //Select Tabel
                    "_id = ?", //Where clause
                    new String[]{Integer.toString(id_kunci)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor.moveToFirst()) {
                namaKunci = cursor.getString(1);
                String deskripsiKunci = cursor.getString(2);
                gambarKunci = cursor.getInt(3);
                String uri_ = cursor.getString(4);


                if (uri_ != null) {
                    uri = Uri.parse(uri_);
                }
                if (uri_ != null && !uri_.contains("provider")) {
                    Uri uri = Uri.parse(uri_);

//                    Toast.makeText(this, uri + "", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } else if (gambarKunci != 0) {
                    imageView.setImageResource(gambarKunci);
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher);
                }
                nama_kunci.setText(namaKunci);
                deskripsi_kunci.setText(deskripsiKunci);
            }

            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Uri Error : " + e, Toast.LENGTH_SHORT).show();
        }

        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI"}, //Select Tabel
                    "NAMA_KUNCI != ?", //Where clause
                    new String[]{namaKunci}, //Where value
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String date = dateFormat.format(new Date());

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = nama_kunci.getText().toString();
                String deskripsi = deskripsi_kunci.getText().toString();

                String gambar_uri = null;

                if (uri != null) {
                    gambar_uri = uri.toString();
                    gambarKunci = 0;
                }
                if (nama.length() < 1) {
                    nama_kunci.setError("Nama kunci harus diisi!");
                    nama_kunci.setFocusable(true);
                } else if (error_sama) {
                    nama_kunci.setFocusable(true);
                } else {

                    try {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.update("KUNCI", UpdateKunci(nama, deskripsi, gambarKunci, gambar_uri), "_id = ?", new String[]{Integer.toString(id_kunci)});
                        db.insert("LOG_AKTIVITAS", null, InsertLogAktivitas("Anda memperbarui kunci " + nama, date, nama));
                        db.close();

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditKunciActivity.this);
                        builder.setMessage("Kunci Berhasil Diperbarui")
                                .setTitle("Berhasil!" + gambar_uri)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                    TambahKunciActivity.super.onBackPressed();
                                        EditKunciActivity.super.onBackPressed();
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    } catch (SQLiteException e) {
                        Toast.makeText(EditKunciActivity.this, e + "", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(8);
        getSupportActionBar().setTitle("Edit Kunci");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            gambar_diganti = true;
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

    private ContentValues UpdateKunci(String nama_kunci, String deskripsi_kunci, int gambar_kunci, String gambar_uri) {
        ContentValues values = new ContentValues();
        values.put("NAMA_KUNCI", nama_kunci);
        values.put("DESKRIPSI_KUNCI", deskripsi_kunci);
        values.put("GAMBAR_KUNCI", gambar_kunci);
        values.put("GAMBAR_KUNCI_URI", gambar_uri);
        return values;
    }

    private ContentValues InsertLogAktivitas(String AKTIVITAS, String WAKTU, String KUNCI) {
        ContentValues values = new ContentValues();
        values.put("AKTIVITAS", AKTIVITAS);
        values.put("WAKTU", WAKTU);
        values.put("KUNCI", KUNCI);
        return values;
    }
}
