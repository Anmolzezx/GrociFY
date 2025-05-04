package com.techmania.grocify.auth

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.activity.UsersMainActivity
import com.techmania.grocify.databinding.FragmentLoginBinding
import com.techmania.grocify.viewmodels.AuthViewModel

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        setStatusBarColor()
        onRegisterClick()
        onForgotPasswordClick()
        signingIn()
        onGoogleSignInClick()
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            private var doubleBackToExitPressedOnce = false

            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish() // Exit the app
                } else {
                    doubleBackToExitPressedOnce = true
                    Utlis.showToast(requireContext(), "Press back again to exit")

                    // Reset the flag after 2 seconds
                    view.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }
        })
    }

    private fun onGoogleSignInClick(){
        binding.btnSignInWithGoogle.setOnClickListener {
            signINWithGoogle()
        }
    }

    private fun signINWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account != null){
                Utlis.showDialog(requireContext(),"Signing in")
                updateUI(account)
            }

        }
        else{
            Utlis.showToast(requireContext(), "SignIn Failed, Try again later")

        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Utlis.getAuthInstance().signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                Utlis.hideDialog()
                Utlis.showToast(requireContext(), "Login successful")
                startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                requireActivity().finish()
            }
            else{
                Utlis.showToast(requireContext(), "Login Failed")
            }
        }

    }

    private fun onRegisterClick(){
        binding.tvRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun onForgotPasswordClick(){
        binding.tvForgotPassword.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_forget_password)
        }

    }

    private fun signingIn(){
        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun signInUser(){
        val email = binding.etsignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()
        if(validateForm(email,password)){
            Utlis.getAuthInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Utlis.showToast(requireContext(), "Login Successful")
                        startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                        requireActivity().finish()
                    }
                    else{
                        Utlis.showToast(requireContext(), "Login Failed")
                    }
                }
        }

    }

    private fun validateForm( email: String, password: String?): Boolean {
        return when{

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