package com.dafs.examenandroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dafs.examenandroid.R
import com.dafs.examenandroid.databinding.LocationsRowBinding
import com.dafs.examenandroid.models.ELocations


class LocationsAdapter(var context: Context?, private val locations: ArrayList<ELocations>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val productView= LayoutInflater.from(context).inflate(R.layout.locations_row,parent,false)
        return LocationsViewHolder(productView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as LocationsViewHolder).initializeRowUIComponents(locations.get(position).date,locations.get(position).latitude,locations.get(position).longitude)


    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationsViewHolder(pView: View):RecyclerView.ViewHolder(pView){

        var binding=LocationsRowBinding.bind(pView)


        fun initializeRowUIComponents(date:String,longitude:String,latitude:String){

            binding.txtLocationDate.text="FECHA ALMACENAMIENTO: $date"
            binding.txtLatitude.text="LATITUD: $latitude"
            binding.txtLongitude.text="LONGITUD: $longitude"

        }

    }
}