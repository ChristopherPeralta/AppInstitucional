package com.example.appinstitucional.ui.Login.adaptador
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appinstitucional.ui.Login.Introduccion1Fragment
import com.example.appinstitucional.ui.Login.Introduccion2Fragment
import com.example.appinstitucional.ui.Login.Introduccion3Fragment

class IntroductionPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3 // Número de pantallas de introducción
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Introduccion1Fragment()
            1 -> Introduccion2Fragment()
            2 -> Introduccion3Fragment()
            else -> throw IllegalArgumentException("Posición no válida")
        }
    }
}