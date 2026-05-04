package com.slpolice.trafficfineapp.ui.issuefine

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.slpolice.trafficfineapp.R
import com.slpolice.trafficfineapp.databinding.FragmentIssueFineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssueFineFragment : Fragment() {

    private var _binding: FragmentIssueFineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IssueFineViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) viewModel.fetchLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIssueFineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVehicleTypeSpinner()
        requestLocationPermission()

        binding.btnGetLocation.setOnClickListener { requestLocationPermission() }
        binding.btnSubmit.setOnClickListener { submitFine() }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
                categories.map { "${it.code} - ${it.description}" })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        viewModel.location.observe(viewLifecycleOwner) { loc ->
            binding.tvLocation.text = "Lat: ${loc.first}, Lng: ${loc.second}"
        }

        viewModel.issueState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IssueFineViewModel.IssueState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is IssueFineViewModel.IssueState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardQr.visibility = View.VISIBLE
                    binding.tvRefNumber.text = state.fine.referenceNumber
                    binding.tvFineAmount.text = "LKR ${state.fine.amount}"
                    generateQrCode(state.fine.referenceNumber)
                }
                is IssueFineViewModel.IssueState.Error -> {
                    binding.btnSubmit.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loadCategories()
    }

    private fun submitFine() {
        val categoryIndex = binding.spinnerCategory.selectedItemPosition
        val category = viewModel.categories.value?.getOrNull(categoryIndex) ?: return
        val vehicleTypes = resources.getStringArray(R.array.vehicle_types)
        val vehicleType = vehicleTypes[binding.spinnerVehicleType.selectedItemPosition]

        viewModel.issueFine(
            categoryId = category.categoryId,
            vehicleNumber = binding.etVehicleNumber.text.toString().trim(),
            vehicleType = vehicleType,
            driverNic = binding.etDriverNic.text.toString().trim(),
            driverName = binding.etDriverName.text.toString().trim(),
            driverPhone = binding.etDriverPhone.text.toString().trim().ifEmpty { null },
            location = binding.tvLocation.text.toString()
        )
    }

    private fun generateQrCode(text: String) {
        try {
            val writer = MultiFormatWriter()
            val matrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400)
            val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565)
            for (x in 0 until 400) {
                for (y in 0 until 400) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "QR generation failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupVehicleTypeSpinner() {
        val types = resources.getStringArray(R.array.vehicle_types)
        binding.spinnerVehicleType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, types).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLocation()
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
