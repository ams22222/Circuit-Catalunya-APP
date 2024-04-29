package com.example.m13_circuitdecatalunya.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m13_circuitdecatalunya.ComandaAdapter
import com.example.m13_circuitdecatalunya.ComandaViewModel
import com.example.m13_circuitdecatalunya.ComandaViewModelFactory
import com.example.m13_circuitdecatalunya.databinding.ComandaItemBinding
import com.example.m13_circuitdecatalunya.databinding.FragmentComandesBinding

class ComandaFragment: Fragment() {
    private val viewModel: ComandaViewModel by viewModels { ComandaViewModelFactory(requireContext()) }
    private lateinit var adapter: ComandaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentComandesBinding.inflate(inflater, container, false)

        binding.rvDades.layoutManager = LinearLayoutManager(requireContext())

        adapter = ComandaAdapter(emptyList(), requireContext())

        binding.rvDades.adapter = adapter

        viewModel.comandas.observe(viewLifecycleOwner) { poblacions ->
            adapter.comandas = poblacions
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
}