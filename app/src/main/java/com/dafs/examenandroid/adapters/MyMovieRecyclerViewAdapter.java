package com.dafs.examenandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dafs.examenandroid.R;
import com.dafs.examenandroid.data.local.entity.MovieEntity;
import com.dafs.examenandroid.data.remote.ApiConstants;

import java.util.List;

public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {

    private List<MovieEntity> mValues;
    Context ctx;

    public MyMovieRecyclerViewAdapter(Context context, List<MovieEntity> items) {
        mValues = items;
        ctx = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.txtTitle.setText("Titulo: "+mValues.get(position).getTitle());
        holder.txtIdioma.setText("Idioma: "+mValues.get(position).getOriginalLanguage());
        holder.txtRelease.setText("Lanzamiento: "+mValues.get(position).getReleaseDate());

        Glide.with(ctx)
                .load(ApiConstants.IMAGE_API_PREFIX + holder.mItem.getPosterPath())
                .into(holder.imageViewCover);
    }

    public void setData(List<MovieEntity> movies) {
        this.mValues = movies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mValues != null)
            return mValues.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageViewCover;
        public final TextView txtIdioma;
        public final TextView txtTitle;
        public final TextView txtRelease;
        public MovieEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageViewCover = view.findViewById(R.id.imgPelicula);
            txtIdioma=view.findViewById(R.id.txtIdioma);
            txtTitle=view.findViewById(R.id.txtTitulo);
            txtRelease=view.findViewById(R.id.txtLanzamiento);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}