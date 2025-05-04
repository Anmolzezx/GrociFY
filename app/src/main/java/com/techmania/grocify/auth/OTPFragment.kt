package com.techmania.grocify.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.activity.UsersMainActivity
import com.techmania.grocify.databinding.FragmentOTPBinding
import com.techmania.grocify.models.Users
import com.techmania.grocify.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(layoutInflater)

        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utlis.showDialog(requireContext(), "Signing you...")
            val editTexts = arrayOf(
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
            )
            val otp = editTexts.joinToString("") { it.text.toString() }

            if (otp.length < editTexts.size) {
                Utlis.showToast(requireContext(), "Please enter right otp")
            } else {
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }

//    private fun verifyOtp(otp: String) {
//        val user = Users(uid = Utlis.getCurrentUserId(), userPhoneNumber = userNumber, userAddress =" " )
//
//        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
//
//        lifecycleScope.launch {
//            viewModel.isSignedInSuccessfully.collect {
//                if (it) {
//                    Utlis.hideDialog()
//                    Utlis.showToast(requireContext(), "Logged In...")
//                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
//                    requireActivity().finish()
//                }
//            }
//        }
//    }

//latest
//    private fun verifyOtp(otp: String) {
//        val currentUserId = Utlis.getCurrentUserId()
//        if (currentUserId != null) {
//            val user = Users(uid = currentUserId, userPhoneNumber = userNumber, userAddress = " ")
//            viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
//
//            lifecycleScope.launch {
//                viewModel.isSignedInSuccessfully.collect {
//                    if (it) {
//                        Utlis.hideDialog()
//                        Utlis.showToast(requireContext(), "Logged In...")
//                        startActivity(Intent(requireContext(), UsersMainActivity::class.java))
//                        requireActivity().finish()
//                    }
//                }
//            }
//        } else {
//            // Handle the case when the current user ID is null (user not logged in)
//            // For example, prompt the user to log in or provide an error message
//            Utlis.showToast(requireContext(), "User not logged in")
//        }
//    }

//    private fun verifyOtp(otp: String) {
//        val currentUserId = Utlis.getCurrentUserId()
//
//        if (currentUserId.isNullOrEmpty()) {
//            Utlis.showToast(requireContext(), "User not logged in. Please sign in again.")
//            Utlis.hideDialog()
//            return
//        }
//
//        val user = Users(uid = currentUserId, userPhoneNumber = userNumber, userAddress = " ")
//        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
//
//        lifecycleScope.launch {
//            viewModel.isSignedInSuccessfully.collect { isSignedIn ->
//                if (isSignedIn) {
//                    Utlis.hideDialog()
//                    Utlis.showToast(requireContext(), "Logged In Successfully!")
//                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
//                    requireActivity().finish()
//                } else {
//                    Utlis.showToast(requireContext(), "Login failed. Please try again.")
//                }
//            }
//        }
//    }


    private fun verifyOtp(otp: String) {
        val admins = Users(uid = null, userPhoneNumber = userNumber)

        viewModel.signInWithPhoneAuthCredential(otp, userNumber, admins)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect {
                if (it) {
                    Utlis.hideDialog()
                    Utlis.showToast(requireContext(), "Logged In...")
                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }

            }
        }
    }





    private fun sendOTP() {

        Utlis.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect{

                    if(it){
                        Utlis.hideDialog()
                        Utlis.showToast(requireContext(), "OTP sent ...")
                    }
                }
            }

        }


    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {

            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
        for(i in editTexts.indices){
            editTexts[i].addTextChangedListener ( object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length ==1){
                        if(i< editTexts.size -1){
                            editTexts[i+1].requestFocus()
                        }

                    }else if(s?.length == 0){
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber

    }


}