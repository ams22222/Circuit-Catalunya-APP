package com.example.m13_circuitdecatalunya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.m13_circuitdecatalunya.databinding.ActivityHomeBinding
import com.example.m13_circuitdecatalunya.databinding.ActivityMainBinding
import com.example.m13_circuitdecatalunya.fragments.MyViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var pagerAdapter: MyViewPagerAdapter
    private var viewPagerAdapterInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Para trabajar con ViewBindig
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // inicializar el view pager adapter solo una vez
        if (!viewPagerAdapterInitialized) {
            pagerAdapter = MyViewPagerAdapter(this)
            viewPagerAdapterInitialized = true
        }

        binding.viewPager.adapter = pagerAdapter

        // sincronizar el viewPager con el tabLayout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager!!.currentItem = tab?.position ?:0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        // hacer que si hacemos swipe en el viewPager también
        // se sincronize visualmente el tabLayout
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })

    }
}
