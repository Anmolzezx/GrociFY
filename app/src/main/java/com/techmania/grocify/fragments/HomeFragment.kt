package com.techmania.grocify.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techmania.grocify.CartListener
import com.techmania.grocify.Constants
import com.techmania.grocify.R
import com.techmania.grocify.Utlis
import com.techmania.grocify.adapters.AdapterBestseller
import com.techmania.grocify.adapters.AdapterCategory
import com.techmania.grocify.adapters.AdapterProduct
import com.techmania.grocify.databinding.BsSeeAllBinding
import com.techmania.grocify.databinding.FragmentHomeBinding
import com.techmania.grocify.databinding.ItemViewProductBinding
import com.techmania.grocify.models.Bestseller
import com.techmania.grocify.models.Category
import com.techmania.grocify.models.Product
import com.techmania.grocify.roomdb.CartProductTable
import com.techmania.grocify.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {



    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterBestseller: AdapterBestseller
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigatingToSearchFragment()
        onProfileClicked()
//        fetchBestseller()
//        get()

        return binding.root
    }

//    private fun fetchBestseller() {
//        binding.shimmerViewContainer.visibility = View.VISIBLE
//        lifecycleScope.launch {
//            viewModel.fetchProductTypes().collect{
//                adapterBestseller = AdapterBestseller(::onSeeAllButtonClicked)
//                binding.rvBestsellers.adapter = adapterBestseller
//                adapterBestseller.differ.submitList(it)
//                binding.shimmerViewContainer.visibility = View.GONE
//
//            }
//        }
//    }


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

    private fun onProfileClicked() {
        binding.ivProfile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

//    private fun get(){
//        viewModel.getAll().observe(viewLifecycleOwner){
//            for (i in it){
//                Log.d("vvv", i.productTitle.toString())
//                Log.d("vvv", i.productCount.toString())
//            }
//        }
//    }

    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment2)
        }
    }

    private fun setAllCategories() {
       val categoryList = ArrayList<Category>()
        for (i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }


        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }

    fun onCategoryIconClicked(category: Category){
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)


    }

//    fun onSeeAllButtonClicked(productType: Bestseller){
//        val bsSeeAllBinding = BsSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
//        val bs = BottomSheetDialog(requireContext())
//            bs.setContentView(bsSeeAllBinding.root)
//
//        adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked,::onDecrementButtonClicked)
//        bsSeeAllBinding.rvProducts.adapter = adapterProduct
//        adapterProduct.differ.submitList(productType.products)
//        bs.show()
//    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE


        //step1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)


        //step2
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCount)
        }
    }


    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){

        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++


        if (product.productStock!! + 1 > itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)

            //step 2
            product.itemCount = itemCountInc

            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product,itemCountInc)

            }
        }
        else{
            Utlis.showToast(requireContext(), "Can't add more item of this")
        }

    }
    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){

        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCountDec)

        }

        if(itemCountDec>0){
            productBinding.tvProductCount.text = itemCountDec.toString()

        }
        else{
            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId!!) }
            Log.d("VV", product.productRandomId!!)
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"

        }

        cartListener?.showCartLayout(-1)

        //step2

    }

    private fun saveProductInRoomDb(product: Product) {

        val cartProduct = CartProductTable(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹" + "${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType


        )
        lifecycleScope.launch {
            viewModel.insertCarProduct(cartProduct)


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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please implement cart listener")
        }
    }


}