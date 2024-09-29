package com.example.smartinventory.ui.additem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.smartinventory.R
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.databinding.FragmentAddItemBinding
import com.example.smartinventory.viewmodel.additem.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val addItemViewModel: AddItemViewModel by viewModels()
    private val args: AddItemFragmentArgs by navArgs()


    private var isEditMode: Boolean = false
    private var currentItem: InventoryItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = addItemViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isEditMode = args.inventoryItem != null
        currentItem = args.inventoryItem
        addItemViewModel.isEditMode = isEditMode

        if (isEditMode) {
            populateFields(currentItem)
            //binding.buttonSave.text = getString(R.string.update_item)
        }

        binding.buttonSave.setOnClickListener {
            saveItem()
        }

        binding.buttonDelete.setOnClickListener {
            addItemViewModel.delete(currentItem!!)
            Toast.makeText(requireContext(), getString(R.string.item_deleted), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addItemFragment_to_mainFragment)
        }
    }

    private fun populateFields(item: InventoryItem?) {
        item?.let {
            binding.editTextName.setText(it.name)
            binding.editTextSku.setText(it.sku)
            binding.editTextCategory.setText(it.category)
            binding.editTextEan.setText(it.ean.toString())
            //binding..setText(it.quantity.toString())
            binding.editTextSupplier.setText(it.supplier)
            binding.editTextReorderLevel.setText(it.reorderLevel.toString())
            binding.editTextUnitPrice.setText(it.unitPrice.toString())
            // Handle date and image as needed
        }
    }

    private fun saveItem() {
        val name = binding.editTextName.text.toString().trim()
        val sku = binding.editTextSku.text.toString().trim()
        val category = binding.editTextCategory.text.toString().trim()
        val supplier = binding.editTextSupplier.text.toString().trim()
        val reorderLevelStr = binding.editTextReorderLevel.text.toString().trim()
        val unitPriceStr = binding.editTextUnitPrice.text.toString().trim()
        val eanStr = binding.editTextEan.text.toString().trim()
        // Collect other fields as needed

        // Validate inputs
        if (name.isEmpty() || sku.isEmpty() || category.isEmpty()|| supplier.isEmpty() ||
            reorderLevelStr.isEmpty() || unitPriceStr.isEmpty() || eanStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val reorderLevel = reorderLevelStr.toIntOrNull() ?: 0
        val unitPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
        val ean = eanStr.toIntOrNull() ?: 0

        val newItem = InventoryItem(
            id = currentItem?.id ?: 0, // Use existing ID for editing
            name = name,
            sku = sku,
            category = category,
            quantity = currentItem?.quantity ?: 0,
            supplier = supplier,
            reorderLevel = reorderLevel,
            unitPrice = unitPrice,
            ean = ean,
            expiryDate = currentItem?.expiryDate ?: Date(),
            imageUrl = currentItem?.imageUrl ?: "https://via.placeholder.com/150"
        )

        if (isEditMode) {
            addItemViewModel.update(newItem)
            Toast.makeText(requireContext(), getString(R.string.item_updated), Toast.LENGTH_SHORT).show()
        } else {
            addItemViewModel.insert(newItem)
            Toast.makeText(requireContext(), getString(R.string.item_added), Toast.LENGTH_SHORT).show()
        }

        findNavController().navigate(R.id.action_addItemFragment_to_mainFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
