package a.gautham.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import a.gautham.library.helper.ServiceGenerator;
import a.gautham.library.models.AssestsModel;
import a.gautham.library.models.GitRelease;
import a.gautham.library.models.Update;
import a.gautham.library.service.GithubService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdater {

    private Context context;
    private String dialogTitle, dialogMessage, dialogBtnPositiveText, dialogBtnNegativeText, gitUsername, gitRepoName;
    private boolean dialogCancelable;
    private int dialogAlertStyle, dialog_icon;
    private UpdateListener updateListener;
    private UtilsAsync utilsAsync;
    private static final String TAG = "AppUpdater: ";
    private static DownloadTask downloadTask;
    private Display display;

    public AppUpdater(Context context) {
        this.context = context;

        // DIALOG
        this.dialogTitle = context.getString(R.string.app_updater_new_update_available);
        this.dialogBtnPositiveText = context.getString(R.string.app_updater_update);
        this.dialogBtnNegativeText = context.getString(R.string.app_updater_cancel);
        this.dialogMessage = "";
        this.dialogCancelable = false;
        this.dialog_icon = R.drawable.ic_system_update;
        this.dialogAlertStyle = R.style.dialogAlertStyle;

        this.updateListener = new UpdateListener() {
            @Override
            public void onSuccess(Update update, boolean isUpdateAvailable) {
                if (isUpdateAvailable){

                    if (display == Display.DIALOG)
                        showDialog(update);

                }
            }

            @Override
            public void onFailed(String error) {

            }
        };

    }

    public interface UpdateListener{

        void onSuccess(Update update, boolean isUpdateAvailable);

        void onFailed(String error);
    }

    public AppUpdater withListener(UpdateListener updateListener){
        this.updateListener = updateListener;
        return this;
    }

    public AppUpdater setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    public AppUpdater setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
        return this;
    }

    public AppUpdater setDialogBtnPositive(String dialogBtnPositive) {
        this.dialogBtnPositiveText = dialogBtnPositive;
        return this;
    }

    public AppUpdater setDialogBtnNegative(String dialogBtnNegative) {
        this.dialogBtnNegativeText = dialogBtnNegative;
        return this;
    }

    public AppUpdater setDialogCancelable(boolean dialogCancelable) {
        this.dialogCancelable = dialogCancelable;
        return this;
    }

    public AppUpdater setDialogAlertStyle(int dialogAlertStyle) {
        this.dialogAlertStyle = dialogAlertStyle;
        return this;
    }

    public AppUpdater setDialog_icon(int dialog_icon) {
        this.dialog_icon = dialog_icon;
        return this;
    }

    public AppUpdater setUpGithub(String gitUsername, String gitRepoName){
        this.gitUsername = gitUsername;
        this.gitRepoName = gitRepoName;
        return this;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void start(){

        utilsAsync = new UtilsAsync(context, gitUsername, gitRepoName, updateListener);
        utilsAsync.execute();

    }

    static class UtilsAsync extends AsyncTask{

        private WeakReference<Context> contextRef;
        private String gitUsername, gitRepoName;
        private UpdateListener updateListener;
        private Update update1;

        UtilsAsync(Context context, String gitUsername, String gitRepoName, UpdateListener updateListener) {
            this.contextRef = new WeakReference<>(context);
            this.gitUsername = gitUsername;
            this.gitRepoName = gitRepoName;
            this.updateListener = updateListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = contextRef.get();
            if (context == null || updateListener == null){
                cancel(true);
            }else if (isNetworkAvailable(context)){
                if (!isGitHubValid(gitUsername, gitRepoName)){
                    updateListener.onFailed(context.getString(R.string.app_updater_git_empty));
                    cancel(true);
                }
            }else {
                updateListener.onFailed(context.getString(R.string.app_updater_no_internet));
                cancel(true);
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Call<GitRelease> callAsync;
                GithubService githubService = ServiceGenerator.build().create(GithubService.class);
                callAsync = githubService.getReleases(gitUsername, gitRepoName);

                callAsync.enqueue(new Callback<GitRelease>() {
                    @Override
                    public void onResponse(Call<GitRelease> call, Response<GitRelease> response) {

                        if (response.code() == 200){

                            GitRelease releases = response.body();
                            AssestsModel assestsModel = releases.getAssestsModels().get(0);

                            double size = assestsModel.getSize();
                            double fileSizeInKB = size / 1024;
                            double fileSizeInMB = fileSizeInKB / 1024;
                            double fileSizeInGb = fileSizeInMB / 1024;

                            Update update = new Update(assestsModel.getDownload_url(),
                                    assestsModel.getAssest_name(),
                                    releases.getRelease_title(),
                                    releases.getRelease_description(),
                                    releases.getRelease_tag_name(),
                                    assestsModel.getDownload_count(),
                                    assestsModel.getSize(), fileSizeInKB, fileSizeInMB, fileSizeInGb);
                            update1 = update;

                            double currentAppversion = Double.parseDouble(getAppVersion(contextRef.get()));
                            double latestAppversion = Double.parseDouble(releases.getRelease_tag_name());

                            updateListener.onSuccess(update,isUpdateAvailable(currentAppversion, latestAppversion));

                        }else {
                            updateListener.onFailed("Failed: " + response.message());
                            cancel(true);
                        }

                    }

                    @Override
                    public void onFailure(Call<GitRelease> call, Throwable t) {
                        Log.e(TAG + "Error: ",t.getMessage());
                        updateListener.onFailed("Error: "+ t.getMessage());
                        cancel(true);
                    }
                });

            }catch (Exception e){
                Log.e(TAG + "Failed: ",e.getMessage());
                updateListener.onFailed("Failed: "+ e.getMessage());
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


        }
    }

    private void showDialog(Update update1) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, dialogAlertStyle)
                .setTitle(dialogTitle)
                .setCancelable(dialogCancelable)
                .setIcon(dialog_icon);

        if (dialogMessage.isEmpty()){
            builder.setMessage(String.format("New Update %s is Available. By Downloading the latest " +
                    "update, you will get the latest features, " +
                    "improvements and bug fixes. Do you want to Update App?",update1.getLatest_version()));
        }else {
            builder.setMessage(dialogMessage);
        }

        builder.setPositiveButton(dialogBtnPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask = new DownloadTask(context, update1);
                downloadTask.execute(update1.getDownloadUrl());
            }
        });
        builder.setNegativeButton(dialogBtnNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void stop(){
        if (utilsAsync != null && utilsAsync.isCancelled()){
            utilsAsync.cancel(true);
        }
    }

    private static boolean isGitHubValid(String gitUsername, String gitRepoName) {

        if (gitUsername == null || gitRepoName == null){
            return false;
        }else
            return !gitUsername.isEmpty() && !gitRepoName.isEmpty() && gitUsername.length() != 0 && gitRepoName.length() != 0;
    }

    private static boolean isUpdateAvailable(double currentAppversion, double latestAppversion){

        return currentAppversion < latestAppversion;
    }

    static Boolean isNetworkAvailable(Context context) {
        Boolean res = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                res = networkInfo.isConnected();
            }
        }

        return res;
    }

    private static String getAppVersion(Context context){

        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-","");
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class DownloadTask extends AsyncTask<String, Integer, String> {

        private WeakReference<Context> contextRef;
        private String app_name;
        private PowerManager.WakeLock mWakeLock;
        private DownloadDialog downloadDialog;

        DownloadTask(Context context, Update update) {
            this.contextRef = new WeakReference<>(context);
            this.app_name = update.getAsset_name();
            downloadDialog = new DownloadDialog(contextRef.get());
            downloadDialog.setDownloadName(update.getAsset_name());
            downloadDialog.setDownloadSize(update.getAsses_sizeMb()+"MB");
            downloadDialog.setNegativeBtnListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancel(true);
                }
            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) contextRef.get().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            downloadDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(contextRef.get().getExternalFilesDir(null).getAbsolutePath() + "/"+app_name);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            downloadDialog.setProgressPercent(progress[0] + "%");
            downloadDialog.setProgressBaPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            mWakeLock.release();
            downloadDialog.hide();
            if (result != null){
                Toast.makeText(contextRef.get(),R.string.app_updater_download_error+result, Toast.LENGTH_LONG).show();
            }
            else{

                Toast.makeText(contextRef.get(),R.string.app_updater_update_downloaded, Toast.LENGTH_SHORT).show();

                File file = new File(contextRef.get().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + app_name);
                Uri data = FileProvider.getUriForFile(contextRef.get(), contextRef.get().getPackageName() +".provider",file);

                Intent installAPK = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                installAPK.setDataAndType(data,"application/vnd.android.package-archive");
                installAPK.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                contextRef.get().startActivity(installAPK);
            }
        }
    }

}
