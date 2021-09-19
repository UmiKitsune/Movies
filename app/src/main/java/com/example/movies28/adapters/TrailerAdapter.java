package com.example.movies28.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies28.R;
import com.example.movies28.database.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.name.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    private List<Trailer> trailers = new ArrayList<>();

    //слушатель события на нажатию по названию(картинке) трейлера
    private OnTrailerClickListener onTrailerClickListener;
    public interface OnTrailerClickListener{
        void onTrailerClick(String videoUrl);
    }
    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewNameOfVideo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey()); //в getKey мы сохранили url
                    }
                }
            });
        }
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}
