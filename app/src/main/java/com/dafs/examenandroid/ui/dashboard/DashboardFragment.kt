package com.dafs.examenandroid.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dafs.examenandroid.CustomDialogFragment
import com.dafs.examenandroid.MapsActivity
import com.dafs.examenandroid.adapters.LocationsAdapter
import com.dafs.examenandroid.databinding.FragmentDashboardBinding
import com.dafs.examenandroid.models.ELocations
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


class DashboardFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
private var _binding: FragmentDashboardBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    private lateinit var rv_Locations: RecyclerView
    private lateinit var fab: FloatingActionButton
    var locationList=ArrayList<ELocations>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

    _binding = FragmentDashboardBinding.inflate(inflater, container, false)
    val root: View = binding.root

      rv_Locations= binding.rvLocations;
      rv_Locations.layoutManager = LinearLayoutManager(context)

      fab =binding.fabMap

      fab.setOnClickListener {
          Log.d("LocationsSearch", "Click")
          val intent:Intent = Intent(context, MapsActivity::class.java)
          startActivity(intent)

      }

      /*val textView: TextView = binding.textDashboard
      dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        textView.text = it
      })*/
      CoroutineScope(Dispatchers.IO).launch {
          SearchFirebaseLocations()
      }

    return root
  }

    private fun getLocations(){
        val db = Firebase.firestore


        db.collection("location")
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {

                    val name:String=document.get("fecha").toString()
                    val latitud:String=document.get("latitud").toString()
                    val longitud:String=document.get("longitud").toString()
                    locationList.add(ELocations(name,latitud,longitud))

                    Log.d("LocationsSearch", "${document.id} => ${document.data}")
                    Log.d("LocationsSearch", "$name $latitud $longitud")

                }

                rv_Locations.adapter = LocationsAdapter(context,locationList)

            }
            .addOnFailureListener { exception ->
                var dialog= CustomDialogFragment(exception.toString())
                dialog.show(requireActivity().supportFragmentManager,"customDialog")
                Log.w("LocationsSearch", "Error getting documents.", exception)
            }

    }


    private suspend fun SearchFirebaseLocations() {
        withContext(Dispatchers.IO) {

            val job1 = launch {
                val time1 = measureTimeMillis {
                    getLocations()

                }
            }

        }
    }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





