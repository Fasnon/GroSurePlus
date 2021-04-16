package com.example.grosure.model

import java.time.LocalDate


data class Trip(val text: String, val date: LocalDate, val user: User, var price: Double, var time : String, var itemList: MutableList<ItemInTrip>){
    fun refreshPrice(){
        var i = 0.0
        for (t in itemList){
            i += t.number * t.item.itemPrice
        }

        this.price = i

    }
}