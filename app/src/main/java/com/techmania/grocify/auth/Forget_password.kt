package com.techmania.grocify.auth

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.databinding.FragmentForgetPasswordBinding


class Forget_password : Fragment() {


    private lateinit var binding:FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)

        binding.btnForgotPasswordSubmit.setOnClickListener {
            resetPassword()
        }
        setStatusBarColor()
        onSignINClick()
        return binding.root
    }


    private fun resetPassword(){
        val email = binding.etSignUpEmail.text.toString()
        if(validateForm(email)){
            Utlis.getAuthInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    hideKeyboard()
                    binding.tilEmailForgotPassword.visibility = View.GONE
                    binding.tvSubmitMessage.visibility = View.VISIBLE
                    binding.btnForgotPasswordSubmit.visibility = View.GONE
                    Utlis.showToast(requireContext(), "Password reset email sent")
                    binding.btnSignIn.visibility = View.VISIBLE
                }
                else{
                    Utlis.showToast(requireContext(), "Password reset email not sent")
                }
            }
        }
    }


    private fun validateForm( email: String): Boolean {
        return when{

            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmailForgotPassword.error = "Enter valid email address"
                false
            }

            else -> {true}
        }

    }


   private fun onSignINClick(){
       binding.btnSignIn.setOnClickListener {
           findNavController().navigate(R.id.action_forget_password_to_loginFragment)
       }
   }

    private fun hideKeyboard(){
        view?.let { v ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
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