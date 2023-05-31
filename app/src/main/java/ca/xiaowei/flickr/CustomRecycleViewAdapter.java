package ca.xiaowei.flickr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CustomRecycleViewAdapter extends RecyclerView.Adapter<CustomRecycleViewAdapter.ViewHolder> {

    private Context context;
    private List<Photo> listOfPhotos;

    public CustomRecycleViewAdapter(Context context, List<Photo> listOfPhotos) {
        this.context = context;
        this.listOfPhotos = listOfPhotos;
    }


    @NonNull
    @Override
    public CustomRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_custom_cell, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecycleViewAdapter.ViewHolder holder, int position) {
        Photo photos = listOfPhotos.get(position);
        String url = photos.getImageUrl();
        System.out.println(url);
        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.placeholderimage)
                .resize(80,80)
                .into(holder.imageView);

        holder.cell_owner.setText(photos.getOwner());

    }

    @Override
    public int getItemCount() {
        return listOfPhotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView cell_owner;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cell_imageView);
            cell_owner = itemView.findViewById(R.id.cell_owner);
        }

    }

    public void addAll(List<Photo> newList) {
        listOfPhotos.addAll(newList);
    }

    public void clear() {
        listOfPhotos.clear();
    }
}
