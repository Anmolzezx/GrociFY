package com.techmania.grocify

interface CartListener {

    fun showCartLayout(itemCount: Int)

    fun savingCartItemCount(itemCount: Int)

    fun hideCartLayout()
}