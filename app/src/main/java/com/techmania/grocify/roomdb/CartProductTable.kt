package com.techmania.grocify.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "CartProductTable")
data class CartProductTable(
    @PrimaryKey
    var productId: String = "random", //cant apply nullability check here

    var productTitle: String? = null,
    var productQuantity: String? = null,
    var productPrice: String? = null,
    var productCount : Int? = null,
    var productStock: Int? = null,
    var productImage: String? = null,
    var productCategory: String? = null,
    var adminUid: String? = null,
    var productType: String? = null,

)


