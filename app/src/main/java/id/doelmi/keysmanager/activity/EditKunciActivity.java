package id.doelmi.keysmanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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

    ImageView imageView;

    int finalHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kunci);

        helper = new SQLiteDBHelper(this);

        Button btn_select = (Button) findViewById(R.id.btn_select);
        FloatingActionButton btn_2 = (FloatingActionButton) findViewById(R.id.fab);
        final EditText nama_kunci = (EditText) findViewById(R.id.nama_kunci);
        final EditText deskripsi_kunci = (EditText) findViewById(R.id.deskripsi_kunci);
        imageView = (ImageView) findViewById(R.id.imageView2);


        id_kunci = (Integer) getIntent().getExtras().get(ID_KUNCI);


        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI", "DESKRIPSI_KUNCI", "GAMBAR_KUNCI", "GAMBAR_KUNCI_URI", "PATH"}, //Select Tabel
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
                String path = cursor.getString(5);

                if (uri_ != null && uri_.contains(".jpg")) {
                    try {
                        File f = new File(path, uri_);
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                        imageView.setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ID"));
        final String date = dateFormat.format(new Date());

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = nama_kunci.getText().toString().toUpperCase();
                String deskripsi = deskripsi_kunci.getText().toString();

                if (uri != null) {
                    gambarKunci = 0;
                }
                if (nama.length() < 1) {
                    nama_kunci.setError("Nama kunci harus diisi!");
                    nama_kunci.setFocusable(true);
                } else if (error_sama) {
                    nama_kunci.setFocusable(true);
                } else {

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File dir = cw.getDir("keysmanager", Context.MODE_PRIVATE);
                    Random random = new Random();
                    String imageName = "image_" + String.valueOf(random.nextInt(1000)) + System.currentTimeMillis() + ".jpg";
                    File myPath = new File(dir, imageName);

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(myPath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String real_path = dir.getAbsolutePath();

                    try {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.update("KUNCI", UpdateKunci(nama.toUpperCase(), deskripsi, gambarKunci, imageName, real_path), "_id = ?", new String[]{Integer.toString(id_kunci)});
                        db.insert("LOG_AKTIVITAS", null, InsertLogAktivitas("Anda memperbarui kunci " + nama, date, nama, id_kunci));
                        db.close();

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditKunciActivity.this);
                        builder.setMessage("Kunci Berhasil Diperbarui")
                                .setTitle("Berhasil!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
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

    private ContentValues UpdateKunci(String nama_kunci, String deskripsi_kunci, int gambar_kunci, String gambar_uri, String path) {
        ContentValues values = new ContentValues();
        values.put("NAMA_KUNCI", nama_kunci);
        values.put("DESKRIPSI_KUNCI", deskripsi_kunci);
        values.put("GAMBAR_KUNCI", gambar_kunci);
        values.put("GAMBAR_KUNCI_URI", gambar_uri);
        values.put("PATH", path);
        return values;
    }

    private ContentValues InsertLogAktivitas(String AKTIVITAS, String WAKTU, String KUNCI, int ID_KUNCI) {
        ContentValues values = new ContentValues();
        values.put("AKTIVITAS", AKTIVITAS);
        values.put("WAKTU", WAKTU);
        values.put("KUNCI", KUNCI);
        values.put("ID_KUNCI", ID_KUNCI);
        return values;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        finalHeight = imageView.getWidth();
        imageView.getLayoutParams().height = finalHeight;

    }
}
