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
                    onSubmitClick = { actionDetails, addedItems ->
                        // Handle submission logic here, e.g., save to database
                        // actionDetails includes selected items from sharedViewModel and addedItems from manual input

                        val selectedItems = sharedViewModel.selectedItems.value ?: emptyList()

                        // Combine selectedItems and addedItems into one list
                        val allItems = selectedItems.map { selectedItem ->
                            NewWarehouseItem(
                                id = selectedItem.id.toInt(), // Ensure id is Int
                                name = selectedItem.name,
                                quantity = selectedItem.quantity,
                                price = selectedItem.unitPrice
                            )
                        } + addedItems

                        // Example: Display a toast with total items count
                        Toast.makeText(
                            context,
                            "Submitted action '${actionDetails.actionName}' with ${allItems.size} items",
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

data class ActionDetails(
    val actionName: String,
    val actionType: String,
    val actionStatus: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWarehouseItemScreen(
    onSelectItemsClick: () -> Unit,
    onSubmitClick: (ActionDetails, List<NewWarehouseItem>) -> Unit,
    selectedItems: List<InventoryItem>
) {
    // **Warehouse Action Details State**
    var actionName by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf(WarehouseActionType.INBOUND) }
    var actionStatus by remember { mutableStateOf(WarehouseActionStatus.DRAFT) }

    // **New Item Input Fields State**
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    // **State list for manually added items**
    var itemIdCounter by remember { mutableStateOf(0) } // To assign unique IDs
    val addedItems = remember { mutableStateListOf<NewWarehouseItem>() }

    // **State for Editing Items**
    var isEditing by remember { mutableStateOf(false) }
    var editingItem: NewWarehouseItem? by remember { mutableStateOf(null) }

    // **Processing Selected Items**
    var processingSelectedItems by remember { mutableStateOf(listOf<InventoryItem>()) }
    var currentProcessingItem by remember { mutableStateOf<NewWarehouseItem?>(null) }

    // **Context for Toasts**
    val context = LocalContext.current

    // **Effect to handle selectedItems updates**
    LaunchedEffect(selectedItems) {
        if (selectedItems.isNotEmpty()) {
            // Add new selected items to the processing queue, avoiding duplicates
            processingSelectedItems =
                processingSelectedItems + selectedItems.filter { selectedItem ->
                    // Check if the selectedItem is already in the queue or already added
                    addedItems.none { it.id == selectedItem.id.toInt() } &&
                            processingSelectedItems.none { it.id == selectedItem.id }
                }
        }
    }

    // **Effect to load next selected item into input fields**
    LaunchedEffect(processingSelectedItems, currentProcessingItem) {
        if (currentProcessingItem == null && processingSelectedItems.isNotEmpty()) {
            val nextItem = processingSelectedItems.first()
            currentProcessingItem = NewWarehouseItem(
                id = nextItem.id.toInt(), // Ensure id is Int
                name = nextItem.name,
                quantity = nextItem.quantity,
                price = nextItem.unitPrice
            )
            // Pre-fill input fields
            itemName = nextItem.name
            itemQuantity = nextItem.quantity.toString()
            itemPrice = nextItem.unitPrice.toString()
        }
    }

    // **Main Scrollable Container**
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // **Warehouse Action Details Section**
        item {
            Text(text = "Add Warehouse Action", style = MaterialTheme.typography.titleLarge)
        }

        item {
            OutlinedTextField(
                value = actionName,
                onValueChange = { actionName = it },
                label = { Text("Action Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // **Warehouse Action Type Dropdown**
        item {
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
        }

        // **Warehouse Action Status Dropdown**
        item {
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

        // **Manual (or Selected) Item Addition Section**
        item {
            Text(
                text = when {
                    isEditing -> "Edit Item"
                    currentProcessingItem != null -> "Add Selected Item"
                    else -> "Add New Item"
                },
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = itemQuantity,
                onValueChange = { itemQuantity = it },
                label = { Text("Item Quantity") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                value = itemPrice,
                onValueChange = { itemPrice = it },
                label = { Text("Item Price") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // **Add or Update Item Button**
        item {
            Button(
                onClick = {
                    // Validate inputs
                    val quantity = itemQuantity.toIntOrNull()
                    val price = itemPrice.toDoubleOrNull()

                    if (actionName.isBlank()) {
                        Toast.makeText(context, "Please enter the action name", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (itemName.isBlank() || quantity == null || price == null) {
                        Toast.makeText(
                            context,
                            "Please enter valid item details",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (isEditing && editingItem != null) {
                        // **Case 1: Updating an Existing Manually Added Item**
                        editingItem!!.name = itemName
                        editingItem!!.quantity = quantity
                        editingItem!!.price = price
                        // Update the item in the list to trigger recomposition
                        val index = addedItems.indexOf(editingItem!!)
                        if (index != -1) {
                            addedItems[index] = editingItem!!
                        }
                        isEditing = false
                        editingItem = null
                        Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                    } else if (currentProcessingItem != null) {
                        // **Case 2: Adding a Selected Item**
                        val updatedItem = NewWarehouseItem(
                            id = currentProcessingItem!!.id, // Keep the existing ID
                            name = itemName,
                            quantity = quantity,
                            price = price
                        )
                        addedItems.add(updatedItem)
                        Toast.makeText(context, "Selected item added", Toast.LENGTH_SHORT).show()
                        // Remove the processed item from the queue
                        processingSelectedItems = processingSelectedItems.drop(1)
                        currentProcessingItem = null
                        // Reset input fields
                        itemName = ""
                        itemQuantity = ""
                        itemPrice = ""
                    } else {
                        // **Case 3: Adding a New Manually Entered Item**
                        val newItem = NewWarehouseItem(
                            id = itemIdCounter++,
                            name = itemName,
                            quantity = quantity,
                            price = price
                        )
                        addedItems.add(newItem)
                        Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                        // Reset input fields
                        itemName = ""
                        itemQuantity = ""
                        itemPrice = ""
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when {
                        isEditing -> "Update Item"
                        currentProcessingItem != null -> "Add Selected Item"
                        else -> "Add Item"
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // **Select Items Button**
        item {
            Button(
                onClick = onSelectItemsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Items from Inventory")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // **Added Items Section**
        item {
            Text(text = "Added Items:", style = MaterialTheme.typography.titleMedium)
        }

        if (addedItems.isEmpty()) {
            item {
                Text("No items added.")
            }
        } else {
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
                    },
                    isSelectable = true // Allow editing/deleting manually added items
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // **Submit Action Button**
        item {
            Button(
                onClick = {
                    if (actionName.isBlank()) {
                        Toast.makeText(context, "Please enter the action name", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (addedItems.isEmpty()) {
                        Toast.makeText(context, "Add at least one item", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Prepare ActionDetails
                    val actionDetails = ActionDetails(
                        actionName = actionName,
                        actionType = actionType.name,
                        actionStatus = actionStatus.name
                    )

                    // Call onSubmitClick with ActionDetails and addedItems
                    onSubmitClick(actionDetails, addedItems.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Submit Action")
            }
        }
    }
}

@Composable
fun ItemRow(
    item: NewWarehouseItem,
    onEdit: (NewWarehouseItem) -> Unit,
    onDelete: (NewWarehouseItem) -> Unit,
    isSelectable: Boolean = true
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
            // **Item Details**
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Name: ${item.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: \$${item.price}", style = MaterialTheme.typography.bodyMedium)
            }

            if (isSelectable) {
                // **Edit and Delete Buttons for Manually Added Items**
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
}
