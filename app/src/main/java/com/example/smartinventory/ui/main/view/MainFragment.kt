package com.example.smartinventory.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinventory.R
import com.example.smartinventory.databinding.FragmentMainBinding
import com.example.smartinventory.ui.main.adapter.InventoryAdapter
import com.example.smartinventory.viewmodel.main.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inventoryAdapter = InventoryAdapter()

        binding.recyclerView.apply {
            adapter = inventoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Observe the LiveData from the ViewModel instead of using mock data
        mainViewModel.allInventoryItems.observe(viewLifecycleOwner, { items ->
            items?.let { inventoryAdapter.submitList(it) }
        })

        binding.fabAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addItemFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
