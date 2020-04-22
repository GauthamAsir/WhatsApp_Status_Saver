package a.gautham.statusdownloader.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a.gautham.statusdownloader.Models.Status;
import a.gautham.statusdownloader.R;
import a.gautham.statusdownloader.Utils.Common;

public class VideoAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<Status> videoList;
    private Context context;
    private RelativeLayout container;

    public VideoAdapter(List<Status> videoList, RelativeLayout container) {
        this.videoList = videoList;
        this.container = container;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final Status status = videoList.get(position);
        holder.imageView.setImageBitmap(status.getThumbnail());

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view1 = inflater.inflate(R.layout.view_video_full_screen, null);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDg = new AlertDialog.Builder(context);

                FrameLayout mediaControls = view1.findViewById(R.id.videoViewWrapper);

                if (view1.getParent() != null){
                    ((ViewGroup)view1.getParent()).removeView(view1);
                }

                alertDg.setView(view1);

                final VideoView videoView = view1.findViewById(R.id.video_full);

                final MediaController mediaController = new MediaController(context, false);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        mp.start();
                        mediaController.show(0);
                        mp.setLooping(true);
                    }
                });

                videoView.setMediaController(mediaController);
                mediaController.setMediaPlayer(videoView);
                videoView.setVideoURI(Uri.fromFile(status.getFile()));
                videoView.requestFocus();

                ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                if (mediaControls.getParent() != null){
                    mediaControls.removeView(mediaController);
                }

                mediaControls.addView(mediaController);

                final AlertDialog alert2 = alertDg.create();

                alert2.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                alert2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                alert2.show();

            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.copyFile(status,context,container);

            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

}
