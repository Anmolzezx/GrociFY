package com.techmania.grocify.auth

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)


        onLoginClick()
        setStatusBarColor()
        registration()

        return binding.root
    }

    private fun onLoginClick(){
        binding.tvLoginPage.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun registration(){
        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name = binding.etSignUpName.text.toString()
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text?.toString()
        if(validateForm(name,email,password)){

            Utlis.showDialog(requireContext(), "Signing Up")
            Utlis.getAuthInstance().createUserWithEmailAndPassword(email, password!!)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Utlis.hideDialog()
                        Utlis.showToast(requireContext(), "User Id created successfully")
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

                    }
                    else{
                        Utlis.hideDialog()
                        Utlis.showToast(requireContext(), "User Id not created")
                    }
                }

        }
    }

    private fun validateForm(name: String, email: String, password: String?): Boolean {
        return when{
            TextUtils.isEmpty(name) -> {
                binding.tilName.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding.tilPassword.error = "Enter password"
                false
            }
            else -> {true}
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