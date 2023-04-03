package a.gautham.statusdownloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.concurrent.Executors;

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

    @SuppressLint("InlinedApi")
    private static final String[] NOTIFICATION_PERMISSION = {
            Manifest.permission.POST_NOTIFICATIONS
    };

    private static final int NOTIFICATION_REQUEST_PERMISSIONS = 4;

    private Context context;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;

                    Log.d("HEY: ", data.getData().toString());

                    context.getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        checkForUpdate();

        MaterialToolbar toolbar = findViewById(R.id.toolbarMainActivity);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.images)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.videos)));

///        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
///
///        }

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
                startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
                return true;
            case R.id.menu_aboutUs:
                startActivity(new Intent(getApplicationContext(), AboutUs.class));
                return true;
            case R.id.menu_checkUpdate:
                checkForUpdate();
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

    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

            // If Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionQ();
                return;
            }

            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(NOTIFICATION_PERMISSION,
                    NOTIFICATION_REQUEST_PERMISSIONS);
        }

        if (Common.APP_DIR == null || Common.APP_DIR.isEmpty()) {
            Common.APP_DIR = getExternalFilesDir("StatusDownloader").getPath();
            Log.d("App Path", Common.APP_DIR);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissionQ() {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + startDir;

        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);


        activityResultLauncher.launch(intent);
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

    private void checkForUpdate() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            mainHandler.post(() -> {
                AppUpdater appUpdater = new AppUpdater(MainActivity.this);
                appUpdater.setDisplay(Display.DIALOG);
                appUpdater.setDialogAlertStyle(R.style.dialogAlertStyle);
                appUpdater.setUpGithub("GauthamAsir", "WhatsApp_Status_Saver");
                appUpdater.start();
            });

        });
    }

}
