package a.gautham.statussaver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import a.gautham.statussaver.Adapter.PageAdapter;
import a.gautham.statussaver.Utils.Common;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity: ";
    private ViewPager viewPager;
    private long back_pressed;

    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 12;

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

        Toast.makeText(MainActivity.this, "by Mellow", Toast.LENGTH_LONG).show();

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

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_rateUs:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String app_url = "https://github.com/GauthamAsir/WhatsApp_Status_Saver/releases";
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at \n\n" + app_url);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "WhatsApp Status Saver");
                startActivity(Intent.createChooser(shareIntent, "Share via"));

                return true;
            case R.id.menu_privacyPolicy:
//                startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
                return true;
            case R.id.menu_aboutUs:
//                startActivity(new Intent(getApplicationContext(), AboutUs.class));
                return true;
            case R.id.menu_checkUpdate:
//                GetLatestAppVersion getLatestAppVersion = new GetLatestAppVersion();
//                getLatestAppVersion.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE) {
            System.out.println("HEY");

            if (data == null) return; // TODO: Show error
            Uri uri = data.getData();
            if (uri == null) return;

            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        }

    }

    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions) == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Common.APP_DIR = getExternalMediaDirs()[0].getAbsolutePath();

        List<UriPermission> permissions = getContentResolver().getPersistedUriPermissions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (permissions.get(0).getUri().toString().contains("com.whatsapp")) {
                System.out.println("Has WhatsApp Permission");
                return;
            }

            if(permissions.size() > 1) {
                getContentResolver().getPersistedUriPermissions().clear();
            }

            getPermissionQAbove();
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);

        }



    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            moveTaskToBack(true);
        } else {
            Snackbar.make(viewPager, "Press Again to Exit", Snackbar.LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri getUri() {
        StorageManager sm = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        //String startDir = "Android";
        //String startDir = "Download"; // Not choosable on an Android 11 device
        //String startDir = "DCIM";
        //String startDir = "DCIM/Camera";  // replace "/", "%2F"
        //String startDir = "DCIM%2FCamera";
        // String startDir = "Documents";
        String startDir = "Android/media/com.whatsapp/WhatsApp";

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();

        Log.d(TAG, "INITIAL_URI scheme: " + scheme);

        scheme = scheme.replace("/root/", "/document/");

        startDir = startDir.replace("/", "%2F");

        scheme += "%3A" + startDir;

        return Uri.parse(scheme);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getPermissionQAbove() {

        StorageManager sm = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();

        Uri uri = getUri();

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Log.d(TAG, "uri: " + uri.toString());

        ((Activity) MainActivity.this).startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE);
    }

}