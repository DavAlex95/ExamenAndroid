package com.dafs.examenandroid.adapters

import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.dafs.examenandroid.R
import com.dafs.examenandroid.databinding.ImagesRowBinding
import com.dafs.examenandroid.models.Item
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class ImageAdapter (private var items:List<Item>, private val context: Context):
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.images_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Picasso.get().load(item.imageUrl).into(holder.imageView)

        holder.imageView.setOnClickListener {
            setupDialog(item)
        }
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    private fun setupDialog(item: Item) {
        val dialog = Dialog(context, R.style.DialogStyle)
        dialog.setContentView(R.layout.dialog_wallpaper)
        val dialogImageView = dialog.findViewById<ImageView>(R.id.dialogImageView)
        val dialogProgressBar = dialog.findViewById<ProgressBar>(R.id.dialogProgressBar)

        dialogProgressBar.visibility = View.VISIBLE


        //load image into Picasso
        Picasso.get().load(item.imageUrl).into(dialogImageView, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                dialogProgressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.d("errorLoad", e.toString())
            }

        })



        dialog.show()

    }

}

