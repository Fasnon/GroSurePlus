package com.example.grosure.model

data class ItemForCompare(val item: Item, var price: Double, var number: Int) {
    fun refreshPrice(){
        this.price = number * item.itemPrice
    }
}