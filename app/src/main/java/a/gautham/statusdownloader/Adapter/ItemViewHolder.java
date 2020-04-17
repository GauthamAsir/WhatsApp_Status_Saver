package a.gautham.statusdownloader.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import a.gautham.statusdownloader.R;

public class ItemViewHolder extends RecyclerView.ViewHolder{

    public ImageButton save;
    public ImageView imageView;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.ivThumbnail);
        save = itemView.findViewById(R.id.save);
    }
}