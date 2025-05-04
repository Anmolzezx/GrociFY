package com.techmania.grocify

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.techmania.grocify.databinding.FragmentAddressBinding
import com.techmania.grocify.roomdb.Address
import com.techmania.grocify.viewmodels.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.techmania.grocify.fragments.HomeFragment
import kotlinx.coroutines.launch
import java.util.Locale



class AddressFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentAddressBinding // Use ViewBinding for layout
    private lateinit var fusedLocationClient: FusedLocationProviderClient // Declare the FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnFetchLocation.setOnClickListener {
            checkLocationPermissionAndFetch()
        }
        onSaveAddressClicked()
        onBackButtonClicked()

        viewModel.getSavedAddress().observe(viewLifecycleOwner) { address ->
            if (address != null) {
                displayAddress(address)
            } else {
                showNoAddressUI()
            }  }

        return binding.root
    }

    private fun onSaveAddressClicked() {
        binding.btnSaveAddress.setOnClickListener {
            val actualAddress = binding.addressTextView.text.toString().trim()
            val addressLine1 = binding.editTextAddressLine1.text.toString().trim()
            val city = binding.editTextCity.text.toString().trim()
            val state = binding.editTextState.text.toString().trim()
            val postalCode = binding.editTextPostalCode.text.toString().trim()
            val name = binding.editTextName.text.toString().trim()
            val phoneNumber = binding.PhoneNumber.text.toString().trim()

            if (addressLine1.isEmpty() || city.isEmpty() || state.isEmpty() || postalCode.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                Utlis.showToast(requireContext(), "Please fill in all address fields before saving.")
                return@setOnClickListener
            }

            val address = Address(
                id = 1, // Since we are only saving one address
                name = name,
                phoneNumber = phoneNumber,
                label = "Home",
                addressLine1 = addressLine1,
                city = city,
                state = state,
                postalCode = postalCode,
                latitude = null,
                longitude = null
            )
            lifecycleScope.launch {
                viewModel.insertOrUpdateAddress(address)
                viewModel.saveUserAddress(actualAddress)
                Utlis.showToast(requireContext(), "Address Saved Successfully")
                displayAddress(address)
            }
        }
    }

    private fun checkLocationPermissionAndFetch() {
        if(ActivityCompat.checkSelfPermission(
            requireContext(),Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else{
            fetchCurrentLocation()
        }
    }

    private fun onBackButtonClicked() {
        binding.tvAddressFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_profileFragment)
        }
    }


    private fun displayAddress(address: Address) {
        binding.addressTextView.text = "${address.name}, ${address.phoneNumber}, ${address.addressLine1}"
        binding.editTextAddressLine1.setText(address.addressLine1)
        binding.editTextCity.setText(address.city)
        binding.editTextState.setText(address.state)
        binding.editTextPostalCode.setText(address.postalCode)
        binding.editTextName.setText(address.name)
        binding.PhoneNumber.setText(address.phoneNumber)

        lifecycleScope.launch {
            viewModel.saveAddressStatus()
        }

//        val input = binding.addressTextView.text
//        val bundle= Bundle()
//        bundle.putString("data", input.toString())
//        val fragment = HomeFragment()
//        fragment.arguments = bundle
//        fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView2,fragment)?.commit()
    }

    private fun showNoAddressUI() {
        binding.addressTextView.text = "No address saved"
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Permission is granted, proceed with fetching location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                reverseGeocode(location.latitude, location.longitude)
                lifecycleScope.launch {
                    viewModel.saveUserLocation(location.latitude, location.longitude)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to get location. Try again.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error fetching location: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun reverseGeocode(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses: List<android.location.Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val geocodedAddress = addresses[0]
                binding.editTextAddressLine1.setText(geocodedAddress.getAddressLine(0) ?: "")
                binding.editTextCity.setText(geocodedAddress.locality ?: "")
                binding.editTextState.setText(geocodedAddress.adminArea ?: "")
                binding.editTextPostalCode.setText(geocodedAddress.postalCode ?: "")

            } else {
                showNoAddressUI()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utlis.showToast(requireContext(), "Error fetching address")
        }
//        viewModel.saveUserLocation(latitude, longitude)

    }
}
