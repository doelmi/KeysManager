package id.doelmi.keysmanager.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.dbhelper.SQLiteDBHelper;

public class KembaliKunciActivity extends AppCompatActivity {
    public static final String ID_KUNCI = "IDKUNCI";
    SQLiteOpenHelper helper;
    int id_log = 1;
    String namaKunci;
    ScrollView linelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kembali_kunci);

        linelayout = (ScrollView) findViewById(R.id.linelayout);

        final EditText nama_pengembali = (EditText) findViewById(R.id.nama_pengembali);
        final EditText no_id = (EditText) findViewById(R.id.no_id);
        final EditText kunci_kembali = (EditText) findViewById(R.id.kunci_kembali);

        FloatingActionButton kirim_btn = (FloatingActionButton) findViewById(R.id.fab);
        final EditText tanggal_get = (EditText) findViewById(R.id.tanggal);
        final EditText pukul = (EditText) findViewById(R.id.pukulan);

        ImageView tanggal_image = (ImageView) findViewById(R.id.imageView4);
        ImageView pukul_image = (ImageView) findViewById(R.id.imageView5);

        final int id_kunci = (Integer) getIntent().getExtras().get(ID_KUNCI);

        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("ID"));

        pukul.setText(timeFormat.format(new Date()));
        final TimePickerDialog pukulan = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (Integer.toString(hourOfDay).length() == 1) pukul.setText("0"+hourOfDay + ":" + minute);
                else pukul.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, true);

        pukul_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pukulan.show();
            }
        });


        Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("ID"));

        final DatePickerDialog tanggalan = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                tanggal_get.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        tanggal_get.setText(dateFormatter.format(new Date()));
        tanggal_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggalan.show();
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ID"));
        final String date = dateFormat.format(new Date());
        try {
            helper = new SQLiteDBHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(
                    "KUNCI", //Select Tabel
                    new String[]{"_id", "NAMA_KUNCI"}, //Select Tabel
                    "_id = ?", //Where clause
                    new String[]{Integer.toString(id_kunci)}, //Where value
                    null, //GroupBy
                    null, //Having
                    null  //OrderBy
            );

            if (cursor.moveToFirst()) {
                namaKunci = cursor.getString(1);
                kunci_kembali.setText(namaKunci);

            }

            Cursor cursor2 = db.query(
                    "LOG_AKTIVITAS", //Select Tabel
                    new String[]{"_id"}, //Select Tabel
                    null, //Where clause
                    null, //Where value
                    null, //GroupBy
                    null, //Having
                    "_id DESC"  //OrderBy
            );

            if (cursor2.moveToFirst()) {
                id_log = cursor2.getInt(0) + 1;
            }

            cursor.close();
            cursor2.close();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
        }
        kirim_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nama_pengembali.getText().toString().length() < 1) {
                    linelayout.fullScroll(View.FOCUS_UP);
                    nama_pengembali.setError("Nama Pengembali Harus Diisi");
                    nama_pengembali.setFocusable(true);
                } else {
                    try {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.insert("PENGEMBALIAN_KUNCI", null, InsertPengembalianKunci(nama_pengembali.getText().toString().trim(), no_id.getText().toString(), namaKunci, tanggal_get.getText().toString(), pukul.getText().toString(), id_log, id_kunci));
                        db.insert("LOG_AKTIVITAS", null, InsertLogAktivitas(nama_pengembali.getText().toString().trim() + " mengembalikan kunci " + kunci_kembali.getText().toString(), date));

                        ContentValues values = new ContentValues();
                        values.put("STATUS", 0);
                        values.put("DIBAWA_OLEH", nama_pengembali.getText().toString().trim());
                        values.put("WAKTU", pukul.getText().toString());
                        values.put("TANGGAL", tanggal_get.getText().toString());

                        db.update("KUNCI", values, "_id = ?", new String[]{Integer.toString(id_kunci)});

                        db.close();
                        AlertDialog.Builder builder = new AlertDialog.Builder(KembaliKunciActivity.this);
                        builder.setMessage("Kunci Berhasil Dikembalikan")
                                .setTitle("Berhasil!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        KembaliKunciActivity.super.onBackPressed();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    } catch (SQLException e) {
                        Toast.makeText(KembaliKunciActivity.this, "Database Error : " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(8);
    }


    private ContentValues InsertPengembalianKunci(String NAMA_PENGEMBALI_KUNCI, String NO_ID_PENGEMBALI, String NAMA_KUNCI_KEMBALI, String TANGGAL_KEMBALI, String WAKTU_KEMBALI, int ID_LOG, int ID_KUNCI) {
        ContentValues values = new ContentValues();
        values.put("NAMA_PENGEMBALI_KUNCI", NAMA_PENGEMBALI_KUNCI);
        values.put("NO_ID_PENGEMBALI", NO_ID_PENGEMBALI);
        values.put("NAMA_KUNCI_KEMBALI", NAMA_KUNCI_KEMBALI);
        values.put("TANGGAL_KEMBALI", TANGGAL_KEMBALI);
        values.put("WAKTU_KEMBALI", WAKTU_KEMBALI);
        values.put("ID_LOG", ID_LOG);
        values.put("ID_KUNCI", ID_KUNCI);
        return values;
    }

    private ContentValues InsertLogAktivitas(String AKTIVITAS, String WAKTU) {
        ContentValues values = new ContentValues();
        values.put("AKTIVITAS", AKTIVITAS);
        values.put("WAKTU", WAKTU);
        return values;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        helper.close();
    }
}
