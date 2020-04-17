package a.gautham.statusdownloader.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import a.gautham.statusdownloader.Fragments.ImageFragment;
import a.gautham.statusdownloader.Models.Status;
import a.gautham.statusdownloader.R;
import a.gautham.statusdownloader.Utils.Common;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ImageAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<Status> imagesList;
    private Context context;
    private RelativeLayout container;

    public ImageAdapter(List<Status> imagesList, RelativeLayout container) {
        this.imagesList = imagesList;
        this.container = container;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        if (Common.ITEM_VIEW_FLAG == 1){
            View view = LayoutInflater.from(context).inflate(R.layout.item_status_2, parent, false);
            return new ItemViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final Status status = imagesList.get(position);
        Picasso.get().load(status.getFile()).into(holder.imageView);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.copyFile(status,context,container);

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertD = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.view_image_full_screen, null);
                alertD.setView(view);

                ImageView imageView = view.findViewById(R.id.img);
                Picasso.get().load(status.getFile()).into(imageView);

                AlertDialog alert = alertD.create();
                alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

}
