package a.gautham.statusdownloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.Objects;

import a.gautham.library.AppUpdater;
import a.gautham.library.helper.Display;
import a.gautham.statusdownloader.Adapter.PageAdapter;
import a.gautham.statusdownloader.Utils.Common;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private long back_pressed;

    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbarMainActivity);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.images)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.videos)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.saved_files)));
        PagerAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        Toast.makeText(MainActivity.this,"by Mellow",Toast.LENGTH_LONG).show();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

            if(menu instanceof MenuBuilder){
                MenuBuilder m = (MenuBuilder) menu;
                m.setOptionalIconsVisible(true);
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_rateUs:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share:
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String app_url = "https://github.com/GauthamAsir/WhatsApp_Status_Saver/releases";
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at \n\n" + app_url);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"WhatsApp Status Saver");
                startActivity(Intent.createChooser(shareIntent, "Share via"));

                return true;
            case R.id.menu_privacyPolicy:
                Uri uri = Uri.parse("https://docs.google.com/document/d/1rSG7JRoBLVhW8asQkHj6s-eNXplWvtubqu1fMcEwB5U/edit?usp=sharing");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            case R.id.menu_aboutUs:
                startActivity(new Intent(getApplicationContext(),AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_PERMISSIONS && grantResults.length>0){
            if (arePermissionDenied()){
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
        }
    }

    private boolean arePermissionDenied(){

        for (String permissions : PERMISSIONS){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),permissions) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        Common.APP_DIR = getExternalFilesDir(null).getAbsolutePath() +
                File.separator + "Status Downloader";

        GetLatestAppVersion getLatestAppVersion = new GetLatestAppVersion();
        getLatestAppVersion.execute();

    }

    private class GetLatestAppVersion extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            AppUpdater appUpdater = new AppUpdater(MainActivity.this);
            appUpdater.setDisplay(Display.DIALOG);
            appUpdater.setUpGithub("GauthamAsir","WhatsApp_Status_Saver");
            appUpdater.start();

            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()){
            finish();
            moveTaskToBack(true);
        }else {
            Snackbar.make(viewPager, "Press Again to Exit", Snackbar.LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }
    }

}
