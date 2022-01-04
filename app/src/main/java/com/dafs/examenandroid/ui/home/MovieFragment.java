package com.dafs.examenandroid.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dafs.examenandroid.R;
import com.dafs.examenandroid.adapters.MyMovieRecyclerViewAdapter;
import com.dafs.examenandroid.data.local.entity.MovieEntity;

import java.util.List;

public class MovieFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    List<MovieEntity> movieList;
    MyMovieRecyclerViewAdapter adapter;
    MovieViewModel movieViewModel;
    private RecyclerView recyclerView;


    public MovieFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        movieViewModel =
                new ViewModelProvider(this).get(MovieViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);


            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new MyMovieRecyclerViewAdapter(
                    getActivity(),
                    movieList
            );

            recyclerView.setAdapter(adapter);

            loadMovies();

        return view;
    }

    private void loadMovies() {

        movieViewModel.getPopularMovies().observe(getActivity(), listResource -> {
            movieList = listResource.data;
            adapter.setData(movieList);
        });
    }

}