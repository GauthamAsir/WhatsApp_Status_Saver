package a.gautham.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DownloadDialog extends AlertDialog {

    private int progressBaPercent;
    private DialogInterface.OnClickListener negativeBtnListener;
    private AlertDialog alertDialog;
    private TextView percent_pg, download_name, download_size;
    private ProgressBar progressBar;

    DownloadDialog(@NonNull Context context) {
        super(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogAlertStyle);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.download_layout, null);
        builder.setView(view);

        progressBar = view.findViewById(R.id.progressBar);
        download_name = view.findViewById(R.id.download_name);
        download_size = view.findViewById(R.id.download_size);
        percent_pg = view.findViewById(R.id.percent_pg);
        builder.setTitle(R.string.app_updater_downloading);
        builder.setIcon(R.drawable.ic_system_update);

        progressBar.setMax(100);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(progressBaPercent);

        builder.setNegativeButton("Cancel",negativeBtnListener);

        alertDialog = builder.create();

    }

    void setDownloadName(String downloadName) {
        download_name.setText(downloadName);
    }

    void setDownloadSize(String downloadSize) {
        download_size.setText(downloadSize);
    }

    void setProgressPercent(String progressPercent) {
        percent_pg.setText(progressPercent);
    }

    void setProgressBaPercent(int progressBaPercent) {
        progressBar.setProgress(progressBaPercent);
    }

    void setNegativeBtnListener(OnClickListener negativeBtn) {
        this.negativeBtnListener = negativeBtn;
    }

    public void show(){
        alertDialog.show();
    }

    public void hide(){
        alertDialog.dismiss();
    }
}
