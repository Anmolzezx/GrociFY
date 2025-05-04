package com.techmania.grocify.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techmania.grocify.CartListener
import com.techmania.grocify.R
import com.techmania.grocify.adapters.AdapterCartProducts
import com.techmania.grocify.databinding.ActivityUsersMainBinding
import com.techmania.grocify.databinding.BsCartProductsBinding
import com.techmania.grocify.roomdb.CartProductTable
import com.techmania.grocify.viewmodels.UserViewModel

class UsersMainActivity : AppCompatActivity() , CartListener{
    private lateinit var binding:ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProductTable>
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        getTotalItemCountInCart()

        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this,OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){

            cartProductList = it

        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {
            val bsCartProductBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))
            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            bsCartProductBinding.tvNumberOfProductCount.text= binding.tvNumberOfProductCount.text
            bsCartProductBinding.btnNext.setOnClickListener {
                startActivity(Intent(this,OrderPlaceActivity::class.java))
            }
            adapterCartProducts = AdapterCartProducts()
            bsCartProductBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()

        }
    }

    private fun getTotalItemCountInCart() {

        viewModel.fetchTotalCartItemCount().observe(this){
            if(it > 0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()

            }
            else
            {
                binding.llCart.visibility = View.GONE
            }
        }
    }


    override fun showCartLayout(itemCount: Int) {
        val previousCount =binding.tvNumberOfProductCount.text.toString().toInt()
        val updateCount = previousCount+itemCount

        if (updateCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updateCount.toString()

        }

        else{
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)
        }

    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text = "0"

    }


}