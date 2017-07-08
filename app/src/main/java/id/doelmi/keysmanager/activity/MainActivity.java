package id.doelmi.keysmanager.activity;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import id.doelmi.keysmanager.R;
import id.doelmi.keysmanager.fragment.DaftarKunci;
import id.doelmi.keysmanager.fragment.LogAktivitas;
import id.doelmi.keysmanager.javafile.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setElevation(0);
    }

    Fragment kunci, log;

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        kunci = new DaftarKunci();
        log = new LogAktivitas();
        adapter.addFragment(kunci, "DAFTAR KUNCI");
        adapter.addFragment(log, "LOG AKTIVITAS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_create_order:
                Intent intent = new Intent(this, TambahKunciActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:

                return true;
            case R.id.action_diarsipkan:
                Intent intent3 = new Intent(this, DaftarKunciDiarsipkan.class);
                startActivity(intent3);
                return true;
            case R.id.action_search:
                Intent intent2 = new Intent(this, SearchActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static long back_pressed;
    private static long PERIOD = 2000;

    @Override
    public void onBackPressed() {
        if (back_pressed + PERIOD > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Tekan tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
