package com.example.smartinventory.ui.additem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.databinding.FragmentAddItemBinding
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.viewmodel.additem.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val addItemViewModel: AddItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            val itemName = binding.editTextName.text.toString()
            val quantity = binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
            // Collect other fields similarly

            if (validateInput(itemName /*, other fields */)) {
                val newItem = InventoryItem(
                    id = UUID.randomUUID().toString(),
                    name = itemName,
                    sku = "SKU",
                    category = "Category",
                    quantity = quantity,
                    supplier = "Supplier",
                    reorderLevel = 0,
                    unitPrice = 0.0,
                    Date(),
                    imageUrl = "https://via.placeholder.com/150"
                )

                addItemViewModel.insert(newItem)
                findNavController().navigate(R.id.action_addItemFragment_to_mainFragment)
            } else {
                // Handle validation error
            }
        }
    }

    private fun validateInput(name: String /*, other fields */): Boolean {
        // Implement your validation logic
        return name.isNotEmpty() // Simple example
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
