package com.example.smartinventory.ui.addwarehouseitem.view

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.viewmodel.addwarehouseaction.AddWarehouseActionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWarehouseItemFragment: Fragment() {
/*
    private var _binding: FragmentAddWarehouseItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddWarehouseActionViewModel by viewModels()
    val allItems by viewModel.filteredInventoryItem.observeAsState(emptyList())

*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                AddWarehouseItemScreen()
            }
        }
    }
}

@Composable
fun AddWarehouseItemScreen(viewModel: AddWarehouseActionViewModel = hiltViewModel()) {

    val allItems by viewModel.filteredInventoryItem.observeAsState(emptyList())
    var filterText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = filterText,
            onValueChange = {
                filterText = it
                viewModel.filterItems(it)
            },
            label = { Text("Search Items") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan Barcode Button
        Button(
            onClick = {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                Log.d("MainScreen", "Launching camera intent")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan Barcode")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display List of Items
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(allItems) { item ->
                WarehouseItemRow(item)
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun WarehouseItemRow(item: InventoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                Log.d("WarehouseItemRow", "Item clicked: ${item.name}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = item.name)
            Text(text = "Type: ${item.category}")
            Text(text = "Status: ${item.quantity}")
            Text(text = "Barcode: ${item.ean}")
        }
    }
}