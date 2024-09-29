package com.example.smartinventory.ui.items

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.viewmodel.addwarehouseaction.AddWarehouseActionViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class FilterInventoryItems @Inject constructor(
): Fragment() {

    private lateinit var viewModel: AddWarehouseActionViewModel
    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val TAG = "AddWarehouseItemFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                viewModel = hiltViewModel()
                var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                    if (bitmap != null) {
                        Log.d(TAG, "Captured image: Width=${bitmap.width}, Height=${bitmap.height}")
                        capturedBitmap = bitmap
                        val image = InputImage.fromBitmap(bitmap, 0)
                        scanBarcodes(image) { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val barcodeResult = barcodes.first().rawValue.orEmpty()
                                Log.d(TAG, "Barcode detected: $barcodeResult")
                                viewModel.filterItemsByBarcode(barcodeResult)
                            } else {
                                Log.d(TAG, "No barcodes detected")
                                Toast.makeText(context, "No barcodes detected", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d(TAG, "Bitmap is null")
                    }
                }

                AddWarehouseItemScreenWithViewModel(
                    viewModel = hiltViewModel(),
                    onItemClicked = { item ->
                        Log.d(TAG, "Item clicked: ${item.name}")
                    },
                    onCaptureButtonClick = {
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launcher.launch(null)
                        } else {
                            ActivityCompat.requestPermissions(activity as Activity, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                        }
                    }
                )
            }
        }
    }

    private fun scanBarcodes(image: InputImage, onSuccess: (List<Barcode>) -> Unit) {
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_UNKNOWN
        ).build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                Log.d(TAG, "Barcode scan succeeded with ${barcodes.size} barcodes")
                onSuccess(barcodes)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Barcode scanning failed", e)
            }
    }
}


@Composable
fun AddWarehouseItemScreen(
    filterText: String,
    onFilterTextChange: (String) -> Unit,
    onScanBarcodeClick: () -> Unit,
    items: List<InventoryItem>,
    onItemClicked: (InventoryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = filterText,
            onValueChange = onFilterTextChange,
            label = { Text("Search Items") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan Barcode Button
        Button(
            onClick = onScanBarcodeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan Barcode")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display List of Items
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                WarehouseItemRow(item = item, onItemClicked = onItemClicked)
            }
        }
    }
}

@Composable
fun WarehouseItemRow(item: InventoryItem, onItemClicked: (InventoryItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(item) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Type: ${item.category}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Barcode: ${item.ean}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AddWarehouseItemScreenWithViewModel(
    viewModel: AddWarehouseActionViewModel,
    onItemClicked: (InventoryItem) -> Unit = {},
    onCaptureButtonClick: () -> Unit,

    ) {
    val filterText by viewModel.filterQuery.observeAsState("")
    val items by viewModel.filteredInventoryItem.observeAsState(emptyList())

    val onFilterTextChange: (String) -> Unit = { query ->
        viewModel.filterItems(query)
    }

    AddWarehouseItemScreen(
        filterText = filterText,
        onFilterTextChange = onFilterTextChange,
        onScanBarcodeClick = onCaptureButtonClick,
        items = items,
        onItemClicked = onItemClicked
    )
}

@Preview(showBackground = true)
@Composable
fun AddWarehouseItemScreenPreview() {
    val sampleItems = listOf(
        InventoryItem(
            name = "Sample Item 1",
            category = "Sample Category A",
            quantity = 20,
            ean = "11122",
            expiryDate = Date(),
            imageUrl = null,
            sku = "SKU-001",
            supplier = "Supplier X",
            reorderLevel = 5,
            unitPrice = 99.99
        ),
        InventoryItem(
            name = "Sample Item 2",
            category = "Sample Category B",
            quantity = 30,
            ean = "55566",
            expiryDate = Date(),
            imageUrl = null,
            sku = "SKU-002",
            supplier = "Supplier Y",
            reorderLevel = 10,
            unitPrice = 149.99
        )
    )

    AddWarehouseItemScreen(
        filterText = "",
        onFilterTextChange = {},
        onScanBarcodeClick = {},
        items = sampleItems,
        onItemClicked = {}
    )
}
