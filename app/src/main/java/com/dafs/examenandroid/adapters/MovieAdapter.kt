package com.dafs.examenandroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dafs.examenandroid.R
import com.dafs.examenandroid.databinding.MovieRowBinding
import com.dafs.examenandroid.models.Movie

class MovieAdapter(var context: Context?, private val movies: List<Movie>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val productView= LayoutInflater.from(context).inflate(R.layout.movie_row,parent,false)
        return MovieViewHolder(productView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        movies.get(position).id?.let { movies.get(position).title?.let { it1 ->
            movies.get(position).poster?.let { it2 ->
                movies.get(position).release?.let { it3 ->
                    (holder as MovieViewHolder).initializeRowUIComponents(it,
                        it1, it2, it3
                    )
                }
            }
        } }

    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MovieViewHolder(pView: View):RecyclerView.ViewHolder(pView){

        var binding=MovieRowBinding.bind(pView)


        fun initializeRowUIComponents(id: String, title:String, picture:String,release:String ){

            binding.txtId.text="ID: $id"
            binding.txtTitle.text="Título: $title"
            binding.txtRelease.text="Lanzamiento: $release"

            var picUrl= "https://image.tmdb.org/t/p/w500/"
            Glide.with(itemView).load(picUrl + picture).into(binding.imgMovie)



        }

    }
}