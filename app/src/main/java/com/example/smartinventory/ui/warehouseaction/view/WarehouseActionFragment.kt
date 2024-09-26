package com.example.smartinventory.ui.warehouseaction.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WarehouseActionFragment: Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("WarehouseActionFragment", "onViewCreated")
        Toast.makeText(requireContext(),"WarehouseActionFragment", Toast.LENGTH_SHORT).show()
    }
}