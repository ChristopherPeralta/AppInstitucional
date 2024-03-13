package com.example.appinstitucional.ui.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.appinstitucional.R


class Introduccion3Fragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduccion3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val point3 = view.findViewById<ImageView>(R.id.point3)
        point3.isSelected = true

        val btnSiguiente = view.findViewById<Button>(R.id.btn_siguiente)
        btnSiguiente.setOnClickListener {
            val intent = Intent(activity, BienvendidoActivity::class.java)
            startActivity(intent)
        }
    }
}