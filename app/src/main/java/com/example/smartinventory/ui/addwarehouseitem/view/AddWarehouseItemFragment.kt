package com.example.smartinventory.ui.addwarehouseitem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.smartinventory.viewmodel.shared.AddWarehouseSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.model.WarehouseActionItem
import com.example.smartinventory.data.model.WarehouseActionStatus
import com.example.smartinventory.data.model.WarehouseActionType
import com.example.smartinventory.viewmodel.addwarehouseaction.AddWarehouseActionViewModel

@AndroidEntryPoint
class AddWarehouseItemFragment : Fragment() {

    private val sharedViewModel: AddWarehouseSharedViewModel by activityViewModels()
    private val viewModel: AddWarehouseActionViewModel by activityViewModels()
    
    private var warehouseActionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get warehouseActionId from arguments
        arguments?.let {
            warehouseActionId = it.getLong("warehouseActionId", -1L)
            if (warehouseActionId > 0) {
                // We're in edit mode, load the warehouse action
                viewModel.loadWarehouseAction(warehouseActionId)
            } else {
                // We're in creation mode, clear any previous data
                clearInputData()
            }
        } ?: run {
            // No arguments means creation mode
            clearInputData()
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // When returning from the filter screen, if we're not in edit mode,
        // make sure we clear any previous state except the selected items
        if (warehouseActionId <= 0 && !viewModel.isEditMode) {
            // Only restore the added items (don't clear them)
            val currentItems = ArrayList(sharedViewModel.addedItems.value)
            sharedViewModel.resetAllData()
            
            // Re-add the previously added items
            currentItems.forEach { item ->
                sharedViewModel.addItem(item)
            }
        }
    }
    
    private fun clearInputData() {
        // Reset all data in the shared ViewModel
        sharedViewModel.resetAllData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                androidx.compose.material3.MaterialTheme {
                    AddWarehouseItemScreen(
                        onSelectItemsClick = {
                            // Navigate to FilterInventoryItems
                            findNavController().navigate(R.id.action_navAddWarehouseItemFragment_to_filterInventoryItemsFragment)
                        },
                        onSubmitClick = { actionDetails, addedItems ->
                        val warehouseAction = if (viewModel.isEditMode) {
                            // If editing, use the existing ID
                            WarehouseAction(
                                id = viewModel.editingWarehouseActionId,
                                name = actionDetails.actionName,
                                type = actionDetails.actionType,
                                status = actionDetails.actionStatus
                            )
                        } else {
                            // If adding new, use default ID 0 (will be autoincremented)
                            WarehouseAction(
                                name = actionDetails.actionName,
                                type = actionDetails.actionType,
                                status = actionDetails.actionStatus
                            )
                        }

                        // Map UI items to WarehouseActionItem entities
                        val actionItems = addedItems.map { item ->
                            WarehouseActionItem(
                                warehouseActionId = warehouseAction.id,
                                inventoryItemId = item.id,
                                quantity = item.quantity,
                                price = item.price
                            )
                        }

                        try {
                            // The viewModel will handle whether to insert or update
                            viewModel.insertWarehouseActionWithItems(warehouseAction, actionItems)
                            
                            Toast.makeText(
                                context,
                                if (viewModel.isEditMode) "Updated warehouse action" else "Created warehouse action",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            return@AddWarehouseItemScreen
                        }

                        // Clear the view model data to prevent stale data
                        sharedViewModel.resetAllData()
                        
                        // Navigate back to warehouse actions list
                        findNavController().navigate(R.id.action_navAddWarehouseItemFragment_to_warehouseActionFragment)
                    },
                    sharedViewModel = sharedViewModel,
                    isEditMode = viewModel.isEditMode,
                    viewModel = viewModel
                )
                }
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

data class ActionDetails(
    val actionName: String,
    val actionType: WarehouseActionType,
    val actionStatus: WarehouseActionStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWarehouseItemScreen(
    onSelectItemsClick: () -> Unit,
    onSubmitClick: (ActionDetails, List<NewWarehouseItem>) -> Unit,
    sharedViewModel: AddWarehouseSharedViewModel,
    isEditMode: Boolean = false,
    viewModel: AddWarehouseActionViewModel
) {
    // **New Item Input Fields State**
    var itemName by rememberSaveable { mutableStateOf("") }
    var itemQuantity by rememberSaveable { mutableStateOf("") }
    var itemPrice by rememberSaveable { mutableStateOf("") }

    // **State list for manually added items**
    var itemIdCounter by rememberSaveable { mutableLongStateOf(0L) } // To assign unique IDs

    // **State for Editing Items**
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editingItem: NewWarehouseItem? by rememberSaveable { mutableStateOf(null) }
    var currentProcessingItem by remember { mutableStateOf<NewWarehouseItem?>(null) }

    // **Context for Toasts**
    val context = LocalContext.current
    val selectedItem = sharedViewModel.selectedItem
    val actionName = sharedViewModel.actionName.collectAsState(initial = "")
    val actionType = sharedViewModel.actionType.collectAsState(initial = WarehouseActionType.INBOUND)
    val actionStatus = sharedViewModel.actionStatus.collectAsState(initial = WarehouseActionStatus.DRAFT)
    val addedItems = sharedViewModel.addedItems.collectAsState(initial = mutableListOf())
    
    // Observe the currentWarehouseAction for edit mode
    val currentWarehouseAction by viewModel.currentWarehouseAction.observeAsState()
    
    // Effect to handle loading data or resetting fields
    LaunchedEffect(currentWarehouseAction, isEditMode) {
        if (isEditMode && currentWarehouseAction != null) {
            // Edit mode with data - load the existing action data
            currentWarehouseAction?.let { action ->
                // Set the action details in the shared view model
                sharedViewModel.setActionName(action.name)
                sharedViewModel.setActionType(action.type)
                sharedViewModel.setActionStatus(action.status)
                
                // Load the action items from the database and add them to the shared view model
                viewModel.repository.getWarehouseActionWithItems(action.id)?.let { actionPair ->
                    // Clear existing items first
                    addedItems.value.forEach { item ->
                        sharedViewModel.removeItem(item)
                    }
                    
                    // Add the items from the database
                    val items = actionPair.second
                    items.forEach { actionItem ->
                        val inventoryItem = viewModel.inventoryRepository.getItem(actionItem.inventoryItemId)
                        inventoryItem?.let { item ->
                            val newItem = NewWarehouseItem(
                                id = item.id,
                                name = item.name,
                                quantity = actionItem.quantity,
                                price = actionItem.price
                            )
                            sharedViewModel.addItem(newItem)
                        }
                    }
                }
            }
        } else {
            // Creation mode - clear input fields
            itemName = ""
            itemQuantity = ""
            itemPrice = ""
            currentProcessingItem = null
            isEditing = false
        }
    }

    // **Effect to load next selected item into input fields**
    LaunchedEffect(selectedItem) {
        selectedItem.value?.let {
            val existId = it.id
            val existingItem = addedItems.value.find { it.id == existId }
            if (existingItem != null) {
                Toast.makeText(context, "Item already added", Toast.LENGTH_SHORT).show()
                isEditing = true
                currentProcessingItem = existingItem
            } else {
                currentProcessingItem = NewWarehouseItem(
                    id = it.id, // Ensure id is Int
                    name = it.name,
                    quantity = it.quantity,
                    price = it.unitPrice
                )
                // Pre-fill input fields
            }

            itemName = currentProcessingItem!!.name
            itemQuantity = currentProcessingItem!!.quantity.toString()
            itemPrice = currentProcessingItem!!.price.toString()
        }
    }

    // Get the Material3 color scheme
    val colorScheme = MaterialTheme.colorScheme
    
    // **Main Scrollable Container**
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // **Warehouse Action Details Section**
        item {
            Text(
                text = if (isEditMode) "Edit Warehouse Action" else "Add Warehouse Action", 
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            OutlinedTextField(
                value = actionName.value,
                onValueChange = { sharedViewModel.setActionName(it) },
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
                                sharedViewModel.setActionType(type)
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
                                sharedViewModel.setActionStatus(status)
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


        if (currentProcessingItem != null) {
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
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (currentProcessingItem == null) {
                            Toast.makeText(context, "No item selected", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Validate inputs
                        val quantity = itemQuantity.toIntOrNull()
                        val price = itemPrice.toDoubleOrNull()


                        if (quantity == null || price == null) {
                            Toast.makeText(
                                context,
                                "Please enter valid item details",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val existingItem = sharedViewModel.addedItems.value.find { it.id == currentProcessingItem!!.id }
                        if (existingItem != null) {
                            // **Case 1: Updating an Existing Manually Added Item**
                            val updatedItem = existingItem.copy(
                                quantity = quantity,
                                price = price
                            )
                            sharedViewModel.updateItem(updatedItem)
                            isEditing = true
                            Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                        } else {
                            // **Case 2 & 3: Adding a New Item**
                            val newItem = NewWarehouseItem(
                                id = currentProcessingItem?.id ?: itemIdCounter++,
                                name = currentProcessingItem!!.name,
                                quantity = quantity,
                                price = price
                            )
                            isEditing = false
                            sharedViewModel.addItem(newItem)

                            Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                        }

                        // Reset input fields
                        selectedItem.value = null
                        currentProcessingItem = null
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
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            Spacer(modifier = Modifier.height(8.dp))
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
                        //selectedItem.value = null
                        currentProcessingItem = it
                        // Populate input fields with existing item data
                        itemName = it.name
                        itemQuantity = it.quantity.toString()
                        itemPrice = it.price.toString()
                    },
                    onDelete = {
                        isEditing = false
                        currentProcessingItem = null
                        itemName = ""
                        itemQuantity = ""
                        itemQuantity = ""
                        sharedViewModel.removeItem(it)
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
                        actionType = actionType.value,
                        actionStatus = actionStatus.value
                    )

                    // Call onSubmitClick with ActionDetails and addedItems
                    onSubmitClick(actionDetails, addedItems.value.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Action" else "Submit Action")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
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

data class InventoryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)

// Preview functionality removed
