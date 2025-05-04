package com.techmania.grocify.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.activity.AuthMainActivity
import com.techmania.grocify.databinding.AddressBookLayoutBinding
import com.techmania.grocify.databinding.FragmentProfileBinding
import com.techmania.grocify.viewmodels.UserViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel:UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        onBackButtonClicked()
        onOrdersLayoutClicked()
        setStatusBarColor()
//        onAddressBookClicked()
        onAddressLayoutClicked()
        onLogOutClicked()


        return binding.root
    }

    private fun onLogOutClicked() {
        binding.llLogOut.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog =builder.create()
                builder.setTitle("Log Out")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes"){_,_->
                viewModel.logOutUser()
                    startActivity(Intent(requireContext(), AuthMainActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No"){_,_ ->
                    alertDialog.dismiss()
                }
                    .show()
                    .setCancelable(false)
        }
    }

//    private fun onAddressBookClicked() {
//        binding.llAddress.setOnClickListener {
//            val addressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))
//            viewModel.getUserAddress {address ->
//                addressBookLayoutBinding.etAddress.setText(address.toString())
//            }
//            val alertDialog = AlertDialog.Builder(requireContext())
//                .setView(addressBookLayoutBinding.root)
//                .create()
//            alertDialog.show()
//
//            addressBookLayoutBinding.btnEdit.setOnClickListener {
//                addressBookLayoutBinding.etAddress.isEnabled = true
//
//            }
//            addressBookLayoutBinding.btnSave.setOnClickListener {
//                viewModel.saveAddress(addressBookLayoutBinding.etAddress.text.toString())
//                alertDialog.dismiss()
//                Utlis.showToast(requireContext(), "Address updated...")
//            }
//        }
//    }

    private fun onAddressLayoutClicked(){
        binding.llAddress.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addressFragment)
        }
    }

    private fun onOrdersLayoutClicked() {
        binding.llOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackButtonClicked() {
        binding.tvProfileFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.skyBlue)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}