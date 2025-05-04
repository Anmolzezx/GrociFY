package com.techmania.grocify.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.techmania.grocify.CartListener
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.adapters.AdapterCartProducts
import com.techmania.grocify.databinding.ActivityOrderPlaceBinding
import com.techmania.grocify.databinding.AddressLayoutBinding
import com.techmania.grocify.databinding.BsCartProductsBinding
import com.techmania.grocify.databinding.ItemViewCartProductsBinding
import com.techmania.grocify.databinding.ItemViewProductBinding
import com.techmania.grocify.models.Orders
import com.techmania.grocify.models.Product
import com.techmania.grocify.models.Users
import com.techmania.grocify.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var cartListener: CartListener? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient // Declare the FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@OrderPlaceActivity)

        setContentView(binding.root)
        setStatusBarColor()

        getUserAddress { address ->
            binding.userAddress.text = address?: "No address found"
        }

        getAllCartProducts()
        backToUserMainActivity()
        onEditAddressClicked()
//        onPlaceOrderClicked2()
        onPlacedOrderClicked()
        }



    private fun getUserAddress(callback: (String?) -> Unit) {
        val userId = Utlis.getCurrentUserId()
        userId?.let {
            FirebaseDatabase.getInstance().getReference("AllUsers")
                .child("Users")
                .child(it)
                .child("userAddress")
                .get()
                .addOnSuccessListener { snapshot ->
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                .addOnFailureListener {
                    callback(null)
                }
        } ?: callback(null) // Handle null user ID
    }

    private fun onEditAddressClicked(){
        binding.btnChangeAddress.setOnClickListener {
            val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

            val alertDialog = AlertDialog.Builder(this)
                .setView(addressLayoutBinding.root)
                .create()
            alertDialog.show()

            addressLayoutBinding.btnAdd.setOnClickListener {
                saveAddress(alertDialog, addressLayoutBinding)
            }
            addressLayoutBinding.btnGetLocation.setOnClickListener {
//                Utlis.showToast(this,"Check in profile section")
                checkLocationPermissionAndFetch()
            }

        }
    }

    private fun cartProductCount(){

        val productsBinding = ItemViewProductBinding.inflate(LayoutInflater.from(this))
        productsBinding.tvProductCount.text = "0"
        productsBinding.llProductCount.visibility = View.GONE
        productsBinding.tvAdd.visibility = View.VISIBLE
    }

    private fun onPlacedOrderClicked() {
        binding.btnNext.setOnClickListener{
            viewModel.getAddressStatus().observe(this){status ->
                if(status){

                    Utlis.showToast(this@OrderPlaceActivity,"Payment Done")
                    saveOrder()
                    lifecycleScope.launch {
                        viewModel.deleteCartProducts()
                        viewModel.savingCartItemCount(0)
                        cartListener?.hideCartLayout()
                        viewModel.cartProductCount()
                        Utlis.hideDialog()

//                        val findNavController = findNavController(R.id.fragmentContainerView3)
//                        findNavController.navigate(R.id.splash2Fragment)
                        startActivity(Intent(this@OrderPlaceActivity,UsersMainActivity::class.java))
                        finish()
                    }
                }
                else{
//                    val findNavController = findNavController(R.id.fragmentContainerView3)
//                    findNavController.navigate(R.id.addressFragment)

                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        if(addressLayoutBinding.etName.text.toString().isEmpty() || addressLayoutBinding.etPinCode.text.toString().isEmpty() ||
                            addressLayoutBinding.etPhoneNumber.text.toString().isEmpty() || addressLayoutBinding.etState.text.toString().isEmpty() ||
                            addressLayoutBinding.etCity.text.toString().isEmpty() || addressLayoutBinding.etAddress.text.toString().isEmpty()){
                            Utlis.showToast(this@OrderPlaceActivity,"Please fill in all address fields before saving.")
                            return@setOnClickListener
                        }else{
                            saveAddress(alertDialog, addressLayoutBinding)
                        }
                    }

                    viewModel.saveAddressStatus()
                    lifecycleScope.launch {
                        viewModel.deleteCartProducts()
//                        cartProductCount()
                    }

                }

            }

        }
    }

//        }
//    }
//    private fun checkStatus(){
//
//        viewModel.paymentStatus.collect{status ->
//            if(status){
//                Utlis.showToast(this@OrderPlaceActivity,"Payment Done")
//                saveOrder()
//                viewModel.deleteCartProducts()
//                viewModel.savingCartItemCount(0)
//                cartListener?.hideCartLayout()
//
//                Utlis.hideDialog()
//                startActivity(Intent(this@OrderPlaceActivity,UsersMainActivity::class.java))
//                finish()
//            }
//            else{
//                Utlis.showToast(this@OrderPlaceActivity,"Payment not done")
//            }
//        }
//    }

    private fun saveOrder(){
         viewModel.getAll().observe(this){cartProductsList->
             if(cartProductsList.isNotEmpty()){
                 viewModel.getUserAddress { address->
                     val order = Orders(
                         orderId = Utlis.getRandomId(),
                         orderList = cartProductsList,
                         userAddress = address,
                         orderStatus = 0,
                         orderDate = Utlis.getCurrentDate(),
                         orderingUserId = Utlis.getCurrentUserId()
                     )
                     viewModel.saveOrderedProduct(order)
                 }
                 for(products in cartProductsList){
                     val count = products.productCount
                     val stock = products.productStock?.minus(count!!)
                     if (stock != null) {
                         viewModel.saveProductAfterOrder(stock, products)
                     }
                 }
             }
         }
    }



    private fun saveAddress(alertDialog: AlertDialog?, addressLayoutBinding: AddressLayoutBinding) {
//        Utlis.showDialog(this, "Processing...")
        val userName = addressLayoutBinding.etName.text.toString()
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userCity = addressLayoutBinding.etCity.text.toString()
        val userAddress = addressLayoutBinding.etAddress.text.toString()

//        Utlis.hideDialog()
        if (userName.isEmpty() || userPinCode.isEmpty() || userPhoneNumber.isEmpty() || userState.isEmpty() || userCity.isEmpty() || userAddress.isEmpty()) {
            Utlis.showToast(this@OrderPlaceActivity, "Please fill in all address fields before saving.")
            binding.userAddress.text= "Please fill in all address fields before saving."
            return
        }
        val address = "$userName, $userPhoneNumber, $userAddress , $userCity, $userState, $userPinCode"

        binding.userAddress.text = address

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()

        }

        Utlis.showToast(this,"Saved..")
        alertDialog?.dismiss()
        Utlis.hideDialog()

        binding.userAddress.setText(address)

    }
//    private  fun deleteCartProducts(){
//      viewModel.deleteCartProducts()
//    }

    private fun backToUserMainActivity(){
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this,UsersMainActivity::class.java))
            finish()

        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0

            for (products in cartProductList){
                val price = products.productPrice?.substring(1)?.toInt()  //₹14
                val itemCount = products.productCount!!
                totalPrice += (price?.times(itemCount)!!)
            }
            binding.tvSubTotal.text = totalPrice.toString()
            binding.tvDeliveryCharge.text = "₹20"
            totalPrice += 20

            binding.tvGrandTotal.text = totalPrice.toString()

//            cartProductCount()
        }
    }

    private fun showNoAddressUI() {
        binding.userAddress.text = "No address saved"
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                Toast.makeText(this, "Failed to get location. Try again.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching location: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun reverseGeocode(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))


        try {
            val addresses: List<android.location.Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val geocodedAddress = addresses[0]
                addressLayoutBinding.etAddress.setText(geocodedAddress.getAddressLine(0) ?: "")
                addressLayoutBinding.etCity.setText(geocodedAddress.locality ?: "")
                addressLayoutBinding.etState.setText(geocodedAddress.adminArea ?: "")
                addressLayoutBinding.etPinCode.setText(geocodedAddress.postalCode ?: "")

            } else {
                showNoAddressUI()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utlis.showToast(this, "Error fetching address")
        }
//        viewModel.saveUserLocation(latitude, longitude)

    }
    private fun checkLocationPermissionAndFetch() {
        if(ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
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


    private fun setStatusBarColor(){
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.skyBlue)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
