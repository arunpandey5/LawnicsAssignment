package com.example.lawnics;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    public List<PhotoModel> photoList;

    public PhotoAdapter(List<PhotoModel> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhotoModel photoModel =photoList.get(position);
        holder.imageName.setText(photoModel.getImageName());
        holder.date.setText(photoModel.getDate());
        holder.time.setText(photoModel.getTime());
        holder.page.setText(photoModel.getPage());
        holder.imageType.setText(photoModel.getImageType());
        Picasso.get().load(photoModel.getImageUrl()).into(holder.imageUrl);

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView imageName, date, time, page, imageType;
        ImageView imageUrl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUrl = itemView.findViewById(R.id.imageUrl);
            imageName = itemView.findViewById(R.id.imageName);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            page = itemView.findViewById(R.id.page);
            imageType = itemView.findViewById(R.id.imageType);
        }
    }
}
