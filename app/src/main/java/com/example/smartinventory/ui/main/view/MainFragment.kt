package com.example.smartinventory.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinventory.R
import com.example.smartinventory.databinding.FragmentMainBinding
import com.example.smartinventory.ui.main.adapter.InventoryAdapter
import com.example.smartinventory.viewmodel.main.MainViewModel
import com.example.smartinventory.data.model.InventoryItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
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

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        inventoryAdapter = InventoryAdapter { inventoryItem ->
            // Navigate to AddItemFragment with the selected item for editing
            val action = MainFragmentDirections.actionMainFragmentToAddItemFragment(inventoryItem)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            adapter = inventoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mainViewModel.allInventoryItems.observe(viewLifecycleOwner, { items ->
            inventoryAdapter.submitList(items)
        })

        binding.fabAddItem.setOnClickListener {
            // Navigate to AddItemFragment without any item for adding a new item
            findNavController().navigate(R.id.action_mainFragment_to_addItemFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
