package com.dafs.examenandroid.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dafs.examenandroid.CustomDialogFragment
import com.dafs.examenandroid.R
import com.dafs.examenandroid.adapters.ImageAdapter
import com.dafs.examenandroid.databinding.FragmentNotificationsBinding
import com.dafs.examenandroid.models.Item
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class NotificationsFragment : Fragment() {

  private lateinit var notificationsViewModel: NotificationsViewModel
private var _binding: FragmentNotificationsBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
  private lateinit var rv_Images: RecyclerView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

    _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
    val root: View = binding.root

    rv_Images= binding.rvSaveImages;
    rv_Images.layoutManager = LinearLayoutManager(context)

    CoroutineScope(Dispatchers.IO).launch {
      SearchFirebaseImages()
    }

    /*val textView: TextView = binding.textNotifications
    notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })*/
    return root
  }

  private suspend fun SearchFirebaseImages() {
    withContext(Dispatchers.IO) {

      val job1 = launch {
        val time1 = measureTimeMillis {
          getImages()

        }
      }

    }
  }

  private fun getImages() {
    val storageRef = FirebaseStorage.getInstance().reference.child("my_images")

    val imageList=ArrayList<Item>()
    val listAllTask: Task<ListResult> = storageRef.listAll()
    listAllTask.addOnCompleteListener { result ->
      val items: List<StorageReference> = result.result!!.items
      //add cycle for add image url to list
      items.forEachIndexed { index, item ->
        item.downloadUrl.addOnSuccessListener {
          Log.d("item", "$it")
          imageList.add(Item(it.toString()))
        }.addOnCompleteListener {
          rv_Images.adapter = context?.let { it1 -> ImageAdapter(imageList, it1) }


        }
      }
    }.addOnFailureListener {
      var dialog= CustomDialogFragment(it.toString())
      dialog.show(requireActivity().supportFragmentManager,"customDialog")
    }
  }

  override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}