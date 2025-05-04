package com.techmania.grocify.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.databinding.FragmentSignInBinding
import com.techmania.grocify.databinding.FragmentSplashBinding


class SignInFragment : Fragment(){

        private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)

        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()

        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener{
            val number = binding.etUserNumber.text.toString()

            if(number.isEmpty() || number.length != 10 ){
                Utlis.showToast(requireContext(), "Please enter valid phone number ")

            }
            else
            {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }
        }
    }

    private fun getUserNumber(){

        binding.etUserNumber.addTextChangedListener (
            object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                   val len = number?.length

                    if(len == 10){
                        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                    else{
                        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grayish_blue))

                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }

                })
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