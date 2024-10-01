package com.example.smartinventory.ui.addwarehouseitem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.viewmodel.shared.AddWarehouseSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState

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

/*
                        val selectedItems = sharedViewModel.selectedItem.value ?: emptyList()

                        // Combine selectedItems and addedItems into one list
                        val allItems = selectedItems.map { selectedItem ->
                            NewWarehouseItem(
                                id = selectedItem.id.toInt(), // Ensure id is Int
                                name = selectedItem.name,
                                quantity = selectedItem.quantity,
                                price = selectedItem.unitPrice
                            )
                        } + addedItems
*/

                        // Example: Display a toast with total items count
                        Toast.makeText(
                            context,
                            "Submitted action '${actionDetails.actionName}' with {allItems.size} items",
                            Toast.LENGTH_LONG
                        ).show()

                        // Optionally, navigate back or reset fields
                        findNavController().navigate(R.id.action_navAddWarehouseItemFragment_to_warehouseActionFragment)
                    },
                    selectedItem = sharedViewModel.selectedItem.collectAsState(initial = null),
                    actionName = sharedViewModel.actionName.collectAsState(initial = ""),
                    actionType = sharedViewModel.actionType.collectAsState(initial = WarehouseActionType.INBOUND),
                    actionStatus = sharedViewModel.actionStatus.collectAsState(initial = WarehouseActionStatus.DRAFT),
                    addedItems = sharedViewModel.addedItems.collectAsState(initial = mutableListOf()),
                    onActionNameChange = { sharedViewModel.setActionName(it) },
                    onActionTypeChange = { sharedViewModel.setActionType(it) },
                    onActionStatusChange = { sharedViewModel.setActionStatus(it) },
                    onAddItem = { item -> sharedViewModel.addItem(item) },
                    onUpdateItem = { item -> sharedViewModel.updateItem(item) },
                    onRemoveItem = { item -> sharedViewModel.removeItem(item) }
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
    selectedItem: State<InventoryItem?>,
    actionName: State<String>,
    actionType: State<Enum<*>>,
    actionStatus: State<Enum<*>>,
    addedItems: State<MutableList<NewWarehouseItem>>,
    onActionNameChange: (String) -> Unit,
    onActionTypeChange: (WarehouseActionType) -> Unit,
    onActionStatusChange: (WarehouseActionStatus) -> Unit,
    onAddItem: (NewWarehouseItem) -> Unit,
    onUpdateItem: (NewWarehouseItem) -> Unit,
    onRemoveItem: (NewWarehouseItem) -> Unit
) {
    // **New Item Input Fields State**
    var itemName by rememberSaveable { mutableStateOf("") }
    var itemQuantity by rememberSaveable { mutableStateOf("") }
    var itemPrice by rememberSaveable { mutableStateOf("") }

    // **State list for manually added items**
    var itemIdCounter by rememberSaveable { mutableStateOf(0L) } // To assign unique IDs

    // **State for Editing Items**
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editingItem: NewWarehouseItem? by rememberSaveable { mutableStateOf(null) }
    var currentProcessingItem by remember { mutableStateOf<NewWarehouseItem?>(null) }

    // **Context for Toasts**
    val context = LocalContext.current

    // **Effect to load next selected item into input fields**
    LaunchedEffect(selectedItem) {
            selectedItem.value?.let {
                currentProcessingItem = NewWarehouseItem(
                    id = it.id, // Ensure id is Int
                    name = it.name,
                    quantity = it.quantity,
                    price = it.unitPrice
                )
                // Pre-fill input fields
                itemName = it.name
                itemQuantity = it.quantity.toString()
                itemPrice = it.unitPrice.toString()
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
                value = actionName.value,
                onValueChange = { onActionNameChange(it) },
                label = { Text("Action Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // **Warehouse Action Type Dropdown**
        item {
            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = it }
            ) {
                OutlinedTextField(
                    value = actionType.value.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Action Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    WarehouseActionType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                onActionTypeChange(type)
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
                onExpandedChange = { expandedStatus = it }
            ) {
                OutlinedTextField(
                    value = actionStatus.value.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Action Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    WarehouseActionStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = {
                                onActionStatusChange(status)
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
                    // Add logic if you're processing selected items
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
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = itemQuantity,
                onValueChange = { itemQuantity = it },
                label = { Text("Item Quantity") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

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

                    if (actionName.value.isBlank()) {
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
                        val updatedItem = editingItem!!.copy(
                            name = itemName,
                            quantity = quantity,
                            price = price
                        )
                        onUpdateItem(updatedItem)
                        isEditing = false
                        editingItem = null
                        Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                    } else {
                        // **Case 2 & 3: Adding a New Item**
                        val newItem = NewWarehouseItem(
                            id = itemIdCounter++,
                            name = itemName,
                            quantity = quantity,
                            price = price
                        )
                        onAddItem(newItem)
                        Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                    }

                    // Reset input fields
                    itemName = ""
                    itemQuantity = ""
                    itemPrice = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditing) "Update Item" else "Add Item"
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

        if (addedItems.value.isEmpty()) {
            item {
                Text("No items added.")
            }
        } else {
            items(addedItems.value, key = { it.id }) { item ->
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
                        onRemoveItem(it)
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
                    if (actionName.value.isBlank()) {
                        Toast.makeText(context, "Please enter the action name", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (addedItems.value.isEmpty()) {
                        Toast.makeText(context, "Add at least one item", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Prepare ActionDetails
                    val actionDetails = ActionDetails(
                        actionName = actionName.value,
                        actionType = actionType.value.toString(),
                        actionStatus = actionStatus.value.toString()
                    )

                    // Call onSubmitClick with ActionDetails and addedItems
                    onSubmitClick(actionDetails, addedItems.value.toList())
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
