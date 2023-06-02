package ca.xiaowei.flickr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.xiaowei.flickr.Model.Photo;

public class CustomRecycleViewAdapter extends RecyclerView.Adapter<CustomRecycleViewAdapter.ViewHolder> {

    private Context context;
    private List<Photo> listOfPhotos;
    private Dialog imageDialog;

    public CustomRecycleViewAdapter(Context context, List<Photo> listOfPhotos,Dialog imageDialog) {
        this.context = context;
        this.listOfPhotos = listOfPhotos;
        this.imageDialog = imageDialog;
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

        int cellPosition = position * 4;
        ImageView[] imageViews = {
                holder.imageViewOne,
                holder.imageViewTwo,
                holder.imageViewThree,
                holder.imageViewFour
        };

        for (int i = 0; i < imageViews.length; i++) {
            int photoPosition = cellPosition + i;

            if (photoPosition < listOfPhotos.size()) {
                Photo photo = listOfPhotos.get(photoPosition);
                String imageUrl = photo.getImageUrl();

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholderimage)
                        .into(imageViews[i]);

                int finalI = i; // Capture the value of 'i' for the lambda expression
                Dialog imageDialog = new Dialog(context);
                imageViews[i].setOnLongClickListener(v -> {
                    // Show the enlarged image in a dialog

                    imageDialog.setContentView(R.layout.dialog_large_image);
                    ImageView imageViewDialog = imageDialog.findViewById(R.id.imageViewLarge);

                    // Set the desired size for the dialog
                    Window window = imageDialog.getWindow();
                    if (window != null) {
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(window.getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                        window.setAttributes(layoutParams);
                    }
                    Picasso.get()
                            .load(imageUrl) // Load the same image as the long-clicked ImageView
                            .placeholder(R.drawable.placeholderimage)
                            .into(imageViewDialog);
                    imageViewDialog.setOnClickListener(dialogView -> {
                        // Dismiss the dialog when the image in the dialog is clicked
                        imageDialog.dismiss();
                    });
                    imageDialog.show();
                    return true;
                });
            } else {
                // If there are no more photos, clear the ImageView
                imageViews[i].setImageDrawable(null);
                imageViews[i].setOnLongClickListener(null);
            }

        }
    }

    private int getNextValidPosition(int position) {
        // Check if the position is out of bounds, if so, loop back to the beginning
        if (position >= listOfPhotos.size()) {
            return position % listOfPhotos.size();
        } else {
            return position;
        }
    }
    @Override
    public int getItemCount() {
        return listOfPhotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewOne;
        private ImageView imageViewTwo;
        private ImageView imageViewThree;
        private ImageView imageViewFour;
        private TextView cell_owner;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewOne = itemView.findViewById(R.id.cell_imageViewOne);
            imageViewTwo = itemView.findViewById(R.id.cell_imageViewTwo);
            imageViewThree = itemView.findViewById(R.id.cell_imageViewThree);
            imageViewFour = itemView.findViewById(R.id.cell_imageViewFour);
        }

    }

    public void addAll(List<Photo> newList) {
        listOfPhotos.addAll(newList);
    }

    public void clear() {
        listOfPhotos.clear();
    }

}
