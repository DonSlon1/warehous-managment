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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddWarehouseItemFragment @Inject constructor() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddWarehouseItemScreen()
            }
        }
    }
}

data class NewWarehouseItem(
    val id: Int, // Unique identifier
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
fun AddWarehouseItemScreen() {
    // Warehouse Action Details State
    var actionName by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf(WarehouseActionType.INBOUND) }
    var actionStatus by remember { mutableStateOf(WarehouseActionStatus.DRAFT) }

    // New Item Input Fields State
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    // State list for added items
    var itemIdCounter by remember { mutableStateOf(0) } // To assign unique IDs
    val addedItems = remember { mutableStateListOf<NewWarehouseItem>() }

    // State for Editing Items
    var isEditing by remember { mutableStateOf(false) }
    var editingItem: NewWarehouseItem? by remember { mutableStateOf(null) }

    // Context for Toasts
    val context = LocalContext.current

    // Single LazyColumn to handle all scrollable content
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // **Warehouse Action Details Section**
        item {
            Text(
                text = "Warehouse Action Details",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = actionName,
                onValueChange = { actionName = it },
                label = { Text("Action Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Warehouse Action Type Dropdown
            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                OutlinedTextField(
                    value = actionType.name,
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
                    WarehouseActionType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                actionType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Warehouse Action Status Dropdown
            var expandedStatus by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = actionStatus.name,
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
                    WarehouseActionStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = {
                                actionStatus = status
                                expandedStatus = false
                            }
                        )
                    }
                }
            }
        }


        // **Add New Item Section**
        item {
            Text(
                text = if (isEditing) "Edit Item" else "Add New Item",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = itemQuantity,
                onValueChange = { itemQuantity = it },
                label = { Text("Item Quantity") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = itemPrice,
                onValueChange = { itemPrice = it },
                label = { Text("Item Price") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add or Update Item Button
            Button(
                onClick = {
                    // Validate inputs
                    val quantity = itemQuantity.toIntOrNull()
                    val price = itemPrice.toDoubleOrNull()

                    if (actionName.isBlank()) {
                        Toast.makeText(context, "Please enter the action name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (itemName.isBlank() || quantity == null || price == null) {
                        Toast.makeText(context, "Please enter valid item details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isEditing && editingItem != null) {
                        // Update existing item
                        editingItem!!.name = itemName
                        editingItem!!.quantity = quantity
                        editingItem!!.price = price
                        // Trigger recomposition
                        val index = addedItems.indexOf(editingItem!!)
                        if (index != -1) {
                            addedItems[index] = editingItem!!
                        }
                        isEditing = false
                        editingItem = null
                        Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                    } else {
                        // Add new item
                        val newItem = NewWarehouseItem(
                            id = itemIdCounter++,
                            name = itemName,
                            quantity = quantity,
                            price = price
                        )
                        addedItems.add(newItem)
                        Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                    }

                    // Reset input fields
                    itemName = ""
                    itemQuantity = ""
                    itemPrice = ""
                },
            ) {
                Text(if (isEditing) "Update Item" else "Add Item")
            }
        }


        // **Added Items List Section**
        item {
            Text(
                text = "Added Items",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (addedItems.isEmpty()) {
                Text("No items added yet.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Display Added Items
        items(addedItems, key = { it.id }) { item ->
            ItemRow(
                item = item,
                onEdit = {
                    isEditing = true
                    editingItem = it
                    // Populate input fields with existing item data
                    itemName = it.name
                    itemQuantity = it.quantity.toString()
                    itemPrice = it.price.toString()
                },
                onDelete = {
                    addedItems.remove(it)
                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // **Submit All Details Button**
        item {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (actionName.isBlank()) {
                        Toast.makeText(context, "Please enter the action name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (addedItems.isEmpty()) {
                        Toast.makeText(context, "Add at least one item", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Handle submission logic here
                    // Example: Pass the WarehouseAction and addedItems to ViewModel or another component

                    // For demonstration, show a Toast
                    Toast.makeText(
                        context,
                        "Action \"$actionName\" with ${addedItems.size} items submitted",
                        Toast.LENGTH_LONG
                    ).show()

                    // Reset all fields after submission
                    actionName = ""
                    actionType = WarehouseActionType.INBOUND
                    actionStatus = WarehouseActionStatus.DRAFT
                    addedItems.clear()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Submit All Details")
            }
        }
    }
}

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
