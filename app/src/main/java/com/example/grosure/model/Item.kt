package com.example.grosure.model


data class Item(var itemName: String, var brand: String, var itemPrice: Double, var itemPicture: String, var isFile: Boolean, val user: User) {


}