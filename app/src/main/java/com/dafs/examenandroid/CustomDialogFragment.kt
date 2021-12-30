package com.dafs.examenandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_custom_dialog.view.*


class CustomDialogFragment(private val error:String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView:View=inflater.inflate(R.layout.fragment_custom_dialog,container,false)

        rootView.findViewById<TextView>(R.id.texErrorContent).text = error



        rootView.cancel_Button.setOnClickListener(View.OnClickListener {
            dismiss()
        })


        return rootView
    }



}