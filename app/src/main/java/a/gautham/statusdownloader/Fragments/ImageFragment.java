package a.gautham.statusdownloader.Fragments;

import android.content.UriPermission;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import a.gautham.statusdownloader.Adapter.ImageAdapter;
import a.gautham.statusdownloader.Models.Status;
import a.gautham.statusdownloader.R;
import a.gautham.statusdownloader.Utils.Common;

public class ImageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> imagesList = new ArrayList<>();

    private ImageAdapter imageAdapter;
    private RelativeLayout container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewImage);
        progressBar = view.findViewById(R.id.prgressBarImage);
        container = view.findViewById(R.id.image_container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageTextView = view.findViewById(R.id.messageTextImage);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark)
                , ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark));

        swipeRefreshLayout.setOnRefreshListener(this::getStatus);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Common.GRID_COUNT));

        getStatus();

    }

    private void getStatus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            executeNew();

        } else if (Common.STATUS_DIRECTORY.exists()) {

            executeOld();

        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.cant_find_whatsapp_dir);
            Toast.makeText(getActivity(), getString(R.string.cant_find_whatsapp_dir), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void executeOld() {

        Executors.newSingleThreadExecutor().execute(() -> {

            Handler mainHandler = new Handler(Looper.getMainLooper());

            File[] statusFiles;
            statusFiles = Common.STATUS_DIRECTORY.listFiles();
            imagesList.clear();

            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                for (File file : statusFiles) {

                    if (file.getName().contains(".nomedia"))
                        continue;

                    Status status = new Status(file, file.getName(), file.getAbsolutePath());

                    if (!status.isVideo() && status.getTitle().endsWith(".jpg")) {
                        imagesList.add(status);
                    }

                }

                mainHandler.post(() -> {

                    if (imagesList.size() <= 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                        messageTextView.setText(R.string.no_files_found);
                    } else {
                        messageTextView.setVisibility(View.GONE);
                        messageTextView.setText("");
                    }

                    imageAdapter = new ImageAdapter(imagesList, container);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyItemRangeChanged(0, imagesList.size());
                    progressBar.setVisibility(View.GONE);
                });

            } else {

                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                });

            }
            swipeRefreshLayout.setRefreshing(false);

        });
    }

    private void executeNew() {

        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

            DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());

            imagesList.clear();

            if (file == null) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            DocumentFile[] statusFiles = file.listFiles();

            if (statusFiles.length <= 0) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            for (DocumentFile documentFile : statusFiles) {

                if (Objects.requireNonNull(documentFile.getName()).contains(".nomedia"))
                    continue;

                Status status = new Status(documentFile);

                if (!status.isVideo()) {
                    imagesList.add(status);
                }
            }

            Log.e("HEY: ", String.valueOf(imagesList.size()));

            mainHandler.post(() -> {

                if (imagesList.size() <= 0) {
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                } else {
                    messageTextView.setVisibility(View.GONE);
                    messageTextView.setText("");
                }

                imageAdapter = new ImageAdapter(imagesList, container);
                recyclerView.setAdapter(imageAdapter);
                imageAdapter.notifyItemRangeChanged(0, imagesList.size());
                progressBar.setVisibility(View.GONE);
            });

        });
    }

}
