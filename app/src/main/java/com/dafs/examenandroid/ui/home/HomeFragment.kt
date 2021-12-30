package com.dafs.examenandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dafs.examenandroid.APIService
import com.dafs.examenandroid.CustomDialogFragment
import com.dafs.examenandroid.MovieApiService
import com.dafs.examenandroid.adapters.MovieAdapter
import com.dafs.examenandroid.databinding.FragmentHomeBinding
import com.dafs.examenandroid.models.Movie
import com.dafs.examenandroid.models.MovieResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.measureTimeMillis

class HomeFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel
  private lateinit var rv_movies:RecyclerView
    private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    //val textView: TextView = binding.textHome
      rv_movies= binding.rvMovies;
      rv_movies.layoutManager = LinearLayoutManager(context)
      //rv_movies.setHasFixedSize(true)
      homeViewModel.text.observe(viewLifecycleOwner, Observer {
      //textView.text = it
    })
      CoroutineScope(Dispatchers.IO).launch {
          apiRequest()
      }

    return root


  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNewText(input: String){
        println("debug: setNewText $input ms.")
    }
    private suspend fun setTextOnMainThread(input: String) {
        withContext (Dispatchers.Main) {
            setNewText(input)
        }
    }

    private suspend fun apiRequest() {
        withContext(Dispatchers.IO) {

            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread("Got $result1")
                }
                println("debug: compeleted job1 in $time1 ms.")
            }


        }
    }

    private suspend fun getResult1FromApi(): String {
        getMovieData { movies : List<Movie> ->
            rv_movies.adapter = MovieAdapter(context,movies)
        }
        return "Result #1"
    }


    private fun getMovieData(callback: (List<Movie>) -> Unit){
        val apiService = MovieApiService.getInstance().create(APIService::class.java)
        apiService.getMovieList().enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                println("debug: FAIL")
                var dialog=CustomDialogFragment("No tienes conexión a internet")
                dialog.show(activity!!.supportFragmentManager,"customDialog")

            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                return callback(response.body()!!.movies)
            }

        })
    }

}