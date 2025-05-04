package com.techmania.grocify.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
//import android.location.Address
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.privacysandbox.ads.adservices.adid.AdId
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.techmania.grocify.Utlis
import com.techmania.grocify.databinding.ItemViewProductBinding
import com.techmania.grocify.models.Bestseller
import com.techmania.grocify.models.Orders
import com.techmania.grocify.models.Product
import com.techmania.grocify.models.Users
import com.techmania.grocify.roomdb.AddressDao
import com.techmania.grocify.roomdb.AppDatabase
import com.techmania.grocify.roomdb.Address
import com.techmania.grocify.roomdb.CartProductDao
import com.techmania.grocify.roomdb.CartProductTable
import com.techmania.grocify.roomdb.CartProductsDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application){

    //initializations
    val sharedPreferences:SharedPreferences =application.getSharedPreferences("My_pref" , MODE_PRIVATE)
    val addressDao: AddressDao = AppDatabase.getDatabase(application).addressDao()
    val cartProductDao: CartProductDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()


    //Room db
    suspend fun insertCarProduct(products: CartProductTable){
        cartProductDao.insertCartProduct(products)
    }
    suspend fun insertOrUpdateAddress(address: Address){
        addressDao.insertOrUpdateAddress(address)
    }
    fun getSavedAddress(): LiveData<Address?>{
        return addressDao.getAddress()

    }

    fun getAll(): LiveData<List<CartProductTable>>{
        return cartProductDao.getAllCartProducts()
    }

      suspend fun deleteCartProducts(){
        cartProductDao.deleteCartProducts()
    }

    suspend fun updateCartProduct(products: CartProductTable){
        cartProductDao.updateCartProduct(products)
    }

    suspend fun  deleteCartProduct(productId: String){
        cartProductDao.deleteCartProduct(productId)
    }


    //firebase call
    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {
        val db =  FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database fetch Cancelled: ${error.message}")
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose{db.removeEventListener(eventListener)
        }

    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {

       val db =  FirebaseDatabase.getInstance().getReference("Admins").child("orders").orderByChild("orderStatus")

        val eventListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if(order?.orderingUserId == Utlis.getCurrentUserId()){
                        orderList.add(order!!)
                    }

                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun getCategoryProduct(category: String): Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose{db.removeEventListener(eventListener)
        }
    }

    fun getOrderProducts(orderId: String) : Flow<List<CartProductTable>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("orders").child(orderId)
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}

    }

    fun updateItemCount(product: Product, itemCount: Int){

        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}")
            .child("itemCount")
            .setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
            .child("itemCount")
            .setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product.productType}/${product.productRandomId}")
            .child("itemCount")
            .setValue(itemCount)
    }

    fun saveProductAfterOrder(stock: Int, product: CartProductTable){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productId}").child("productStock").setValue(stock)

    }
// 7:00:00 time stamp
    fun getUserAddress(callback: (String?) -> Unit){
        val db = FirebaseDatabase.getInstance().getReference("AllUsers")
            .child("Users")
            .child(Utlis.getCurrentUserId())
            .child("userAddress")

        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                else{
                    callback(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    fun  logOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun saveOrderedProduct(orders: Orders){
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("orders")
            .child(orders.orderId!!)
            .setValue(orders)
    }

    fun fetchProductTypes() : Flow<List<Bestseller>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins/ProductType")

        val eventListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val productTypeList = ArrayList<Bestseller>()
                for(productType in snapshot.children){
                    val productTypeName = productType.key

                    val productList = ArrayList<Product>()
                    for (products in productType.children){
                        val product = products.getValue(Product::class.java)
                        productList.add(product!!)

                    }
                    val bestseller = Bestseller(productType = productTypeName, products = productList)
                    productTypeList.add(bestseller)
                }
                trySend(productTypeList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    //sharePreferences
    fun savingCartItemCount(itemCount :Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }
    fun fetchTotalCartItemCount() : MutableLiveData<Int>{
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }
    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    fun getAddressStatus(): MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status
    }
    fun saveUserAddress(address: String){

        Utlis.getCurrentUserId()?.let {
            FirebaseDatabase.getInstance().getReference("AllUsers")
                .child("Users")
                .child(it)
                .child("userAddress")
                .setValue(address)
        }
    }

    fun saveUserLocation(latitude: Double, longitude: Double){
        Utlis.getCurrentUserId()?.let {
            FirebaseDatabase.getInstance().getReference("AllUsers")
                .child("Users")
                .child(it)
                .child("userLocation")
                .setValue(latitude, longitude)
        }
    }

     fun cartProductCount(){

        val productsBinding = ItemViewProductBinding.inflate(LayoutInflater.from(getApplication()))
        productsBinding.tvProductCount.text = "0"
        productsBinding.llProductCount.visibility = View.GONE
        productsBinding.tvAdd.visibility = View.VISIBLE
    }
}