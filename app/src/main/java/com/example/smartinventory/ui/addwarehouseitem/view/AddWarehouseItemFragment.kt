package com.example.smartinventory.ui.addwarehouseitem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.compose.foundation.text.KeyboardOptions.Companion
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.viewmodel.shared.AddWarehouseSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.livedata.observeAsState
import javax.inject.Inject

@AndroidEntryPoint
class AddWarehouseItemFragment : Fragment() {

    private val sharedViewModel: AddWarehouseSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddWarehouseItemScreen(
                    onSelectItemsClick = {
                        // Navigate to FilterInventoryItems
                        findNavController().navigate(R.id.action_navAddWarehouseItemFragment_to_filterInventoryItemsFragment)
                    },
                    onSubmitClick = { actionDetails ->
                        // Handle submission logic here, e.g., save to database
                        // actionDetails includes selected items from sharedViewModel

                        val selectedItems = sharedViewModel.selectedItems.value ?: emptyList()

                        // Example: Display a toast with selected items count
                        Toast.makeText(
                            context,
                            "Submitted action '${actionDetails.actionName}' with ${selectedItems.size} items",
                            Toast.LENGTH_LONG
                        ).show()

                        // Optionally, navigate back or reset fields
                        findNavController().navigate(R.id.action_navAddWarehouseItemFragment_to_warehouseActionFragment)
                    },
                    selectedItems = sharedViewModel.selectedItems.observeAsState(initial = emptyList()).value
                )
            }
        }
    }
}

data class NewWarehouseItem(
    val id: Long, // Unique identifier
    var name: String,
    var quantity: Int,
    var price: Double
)

enum class WarehouseActionType {
    INBOUND,
    OUTBOUND
}

enum class WarehouseActionStatus {
    DRAFT,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWarehouseItemScreen(
    onSelectItemsClick: () -> Unit,
    onSubmitClick: (ActionDetails) -> Unit,
    selectedItems: List<InventoryItem>
) {
    var actionName by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf("INBOUND") }
    var actionStatus by remember { mutableStateOf("DRAFT") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Add Warehouse Action", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = actionName,
            onValueChange = { actionName = it },
            label = { Text("Action Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Action Type Dropdown
        var expandedType by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType }
        ) {
            OutlinedTextField(
                value = actionType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Action Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                listOf("INBOUND", "OUTBOUND").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            actionType = type
                            expandedType = false
                        }
                    )
                }
            }
        }

        // Action Status Dropdown
        var expandedStatus by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedStatus,
            onExpandedChange = { expandedStatus = !expandedStatus }
        ) {
            OutlinedTextField(
                value = actionStatus,
                onValueChange = {},
                readOnly = true,
                label = { Text("Action Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedStatus,
                onDismissRequest = { expandedStatus = false }
            ) {
                listOf("DRAFT", "COMPLETED").forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            actionStatus = status
                            expandedStatus = false
                        }
                    )
                }
            }
        }

        // Selected Items Section
        Text(text = "Added Items:", style = MaterialTheme.typography.titleMedium)
        if (selectedItems.isEmpty()) {
            Text("No items added.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .heightIn(min = 0.dp, max = 200.dp)
                    .fillMaxWidth()
            ) {
                items(selectedItems) { item ->
                    val newWarehouseItem = NewWarehouseItem(
                        id = item.id,
                        name = item.name,
                        quantity = item.quantity,
                        price = item.unitPrice
                    )
                    ItemRow(
                        item = newWarehouseItem,
                        onEdit = { },
                        onDelete = { }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Select Items Button
        Button(
            onClick = onSelectItemsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Items")
        }

        // Submit Action Button
        Button(
            onClick = {
                onSubmitClick(
                    ActionDetails(
                        actionName = actionName,
                        actionType = actionType,
                        actionStatus = actionStatus
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Action")
        }
    }
}

data class ActionDetails(
    val actionName: String,
    val actionType: String,
    val actionStatus: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemRow(
    item: NewWarehouseItem,
    onEdit: (NewWarehouseItem) -> Unit,
    onDelete: (NewWarehouseItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Name: ${item.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: \$${item.price}", style = MaterialTheme.typography.bodyMedium)
            }

            // Edit and Delete Buttons
            Row {
                IconButton(onClick = { onEdit(item) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item"
                    )
                }
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item"
                    )
                }
            }
        }
    }
}
