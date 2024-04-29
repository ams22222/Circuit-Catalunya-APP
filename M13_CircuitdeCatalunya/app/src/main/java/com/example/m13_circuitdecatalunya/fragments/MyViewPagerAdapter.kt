package com.example.m13_circuitdecatalunya.fragments

import ReservaFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity){

    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment

        when (position) {
            0-> fragment = EntradesFragment()
            1 -> fragment = ReservaFragment()
            else -> fragment = ComandaFragment()
        }

        // Verificar si el Fragment ya está en la lista
        if (!fragments.contains(fragment)) {
            fragments.add(fragment)
        }

        return fragment
    }
}
