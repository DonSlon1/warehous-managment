package com.example.smartinventory.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinventory.data.local.database.InventoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.smartinventory.R
import com.example.smartinventory.databinding.FragmentMainBinding
import com.example.smartinventory.ui.main.adapter.InventoryAdapter
import com.example.smartinventory.viewmodel.main.MainViewModel
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

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        
        // Register the MainViewModel with the InventoryRefresher
        com.example.smartinventory.utils.InventoryRefresher.registerMainViewModel(mainViewModel)
        
        inventoryAdapter = InventoryAdapter { inventoryItem ->
            // Navigate to AddItemFragment with the selected item for editing
            val action = MainFragmentDirections.actionMainFragmentToAddItemFragment(inventoryItem)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            adapter = inventoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mainViewModel.allInventoryItems.observe(viewLifecycleOwner) { items ->
            // Create a new list to force DiffUtil to detect changes
            val newList = items.toMutableList()
            inventoryAdapter.submitList(newList)
        }

        binding.fabAddItem.setOnClickListener {
            // Navigate to AddItemFragment without any item for adding a new item
            findNavController().navigate(R.id.action_mainFragment_to_addItemFragment)
        }
    }
    
    // Force a refresh every time the fragment resumes
    override fun onResume() {
        super.onResume()
        forceRefresh()
    }
    
    private fun forceRefresh() {
        // Force a refresh by requesting new data from database
        val db = InventoryDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            // Create a new DAO and query directly to bypass any caching
            val freshItems = db.inventoryDao().getAllItemsSync()
            withContext(Dispatchers.Main) {
                inventoryAdapter.submitList(freshItems)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister from InventoryRefresher to prevent memory leaks
        com.example.smartinventory.utils.InventoryRefresher.unregisterMainViewModel()
        _binding = null
    }
}
