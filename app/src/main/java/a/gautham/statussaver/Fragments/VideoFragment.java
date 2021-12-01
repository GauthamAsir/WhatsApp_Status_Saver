package a.gautham.statussaver.Fragments;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a.gautham.statussaver.Adapter.ImageAdapter;
import a.gautham.statussaver.Adapter.VideoAdapter;
import a.gautham.statussaver.Models.Status;
import a.gautham.statussaver.R;
import a.gautham.statussaver.Utils.Common;

public class VideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> videoList = new ArrayList<>();
    private final Handler handler = new Handler();
    private VideoAdapter videoAdapter;
    private RelativeLayout container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recyclerViewVideo);
        progressBar = view.findViewById(R.id.prgressBarVideo);
        container = view.findViewById(R.id.videos_container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageTextView = view.findViewById(R.id.messageTextVideo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark)
                , ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.design_default_color_on_primary),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark));

        swipeRefreshLayout.setOnRefreshListener(this::getStatus);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Common.GRID_COUNT));

        getStatus();

        super.onViewCreated(view, savedInstanceState);
    }

    private void getStatus() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            getVideosApi29();

        } else if (Common.STATUS_DIRECTORY.exists()) {

            execute();

        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.cant_find_whatsapp_dir);
            Toast.makeText(getActivity(), getString(R.string.cant_find_whatsapp_dir), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void getVideosApi29() {
        new Thread(() -> {

            Uri uri = Uri.parse(Common.STATUS_DIRECTORY_NEW);

            DocumentFile doc = DocumentFile.fromTreeUri(requireActivity(), uri);

            if(doc == null) {
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(R.string.cant_find_whatsapp_dir);
                return;
            }

            DocumentFile doc2 = doc.findFile("Media");

            assert doc2 != null;
            DocumentFile doc3 = doc2.findFile(".Statuses");

            DocumentFile[] statusFiles;

            assert doc3 != null;
            statusFiles = doc3.listFiles();

            videoList.clear();

            if (statusFiles.length > 0) {

                for (DocumentFile file : statusFiles) {
                    Status status = new Status(new File(file.getUri().getPath()), file.getName(),
                            file.getUri().getPath(),
                            file.getUri());

                    if (status.isVideo() && status.getTitle().endsWith(".mp4")) {
                        videoList.add(status);
                    }
                }

                handler.post(() -> {

                    if (videoList.size() <= 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                        messageTextView.setText(R.string.no_files_found);
                    } else {
                        messageTextView.setVisibility(View.GONE);
                        messageTextView.setText("");
                    }

                    videoAdapter = new VideoAdapter(videoList, container);
                    recyclerView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });

            } else {

                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                });

            }
            swipeRefreshLayout.setRefreshing(false);
        }).start();
    }

    private void execute() {
        new Thread(() -> {
            File[] statusFiles;
            statusFiles = Common.STATUS_DIRECTORY.listFiles();
            videoList.clear();

            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                for (File file : statusFiles) {
                    Status status = new Status(file, file.getName(), file.getAbsolutePath(),null);

                    if (status.isVideo() && status.getTitle().endsWith(".mp4")) {
                        videoList.add(status);
                    }

                }

                handler.post(() -> {

                    if (videoList.size() <= 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                        messageTextView.setText(R.string.no_files_found);
                    } else {
                        messageTextView.setVisibility(View.GONE);
                        messageTextView.setText("");
                    }

                    videoAdapter = new VideoAdapter(videoList, container);
                    recyclerView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });

            } else {

                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                });

            }
            swipeRefreshLayout.setRefreshing(false);
        }).start();
    }

}
