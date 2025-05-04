package com.techmania.grocify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.techmania.grocify.activity.UsersMainActivity
import com.techmania.grocify.databinding.FragmentSplash2Binding
import kotlinx.coroutines.launch


class Splash2Fragment : Fragment() {

    private lateinit var binding: FragmentSplash2Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplash2Binding.inflate(layoutInflater)
        setStatusBarColor()

        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {
                startActivity(Intent(requireActivity(),UsersMainActivity::class.java))
                requireActivity().finish()
            }

        },200000)
        return binding.root
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