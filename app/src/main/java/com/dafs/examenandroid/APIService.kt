package com.dafs.examenandroid

import com.dafs.examenandroid.models.MovieResponse
import retrofit2.Call

import retrofit2.http.GET



interface APIService {
    @GET("/3/movie/popular?api_key=b0dcf116259ca64959b47e9c1227ff57")
        fun getMovieList(): Call<MovieResponse>
}