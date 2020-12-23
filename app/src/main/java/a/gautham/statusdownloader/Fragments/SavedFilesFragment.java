package a.gautham.statusdownloader.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a.gautham.statusdownloader.Adapter.FilesAdapter;
import a.gautham.statusdownloader.Models.Status;
import a.gautham.statusdownloader.R;
import a.gautham.statusdownloader.Utils.Common;

public class SavedFilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Status> savedFilesList = new ArrayList<>();
    private Handler handler = new Handler();
    private FilesAdapter filesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView no_files_found;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saved_files,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFiles);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutFiles);
        progressBar = view.findViewById(R.id.progressBar);
        no_files_found = view.findViewById(R.id.no_files_found);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),android.R.color.holo_orange_dark)
                ,ContextCompat.getColor(getActivity(),android.R.color.holo_green_dark),
                ContextCompat.getColor(getActivity(),R.color.colorPrimary),
                ContextCompat.getColor(getActivity(),android.R.color.holo_blue_dark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFiles();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        getFiles();

    }

    private void getFiles() {

        final File app_dir = new File(Common.APP_DIR);

        if (app_dir.exists()){

            no_files_found.setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] savedFiles = null;
                    savedFiles = app_dir.listFiles();
                    savedFilesList.clear();

                    if (savedFiles!=null && savedFiles.length>0){

                        Arrays.sort(savedFiles);
                        for (File file : savedFiles){
                            Status status = new Status(file, file.getName(), file.getAbsolutePath());

                            if (status.isVideo())
                                status.setThumbnail(getThumbnail(status));

                            savedFilesList.add(status);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                filesAdapter = new FilesAdapter(savedFilesList);
                                recyclerView.setAdapter(filesAdapter);
                                filesAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                no_files_found.setVisibility(View.VISIBLE);
//                                Toast.makeText(getActivity(), "Dir doest not exists", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            }).start();

        }else {
            no_files_found.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    private Bitmap getThumbnail(Status status) {
        return a.gautham.statusdownloader.Utils.ThumbnailUtils.createVideoThumbnail(status.getFile().getAbsolutePath(),
                3);
    }
}
