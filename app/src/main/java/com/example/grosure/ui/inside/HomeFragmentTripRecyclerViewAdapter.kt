package com.example.grosure.ui.inside

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.Image
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.ItemInGetItem
import com.example.grosure.model.ItemInTrip
import com.example.grosure.model.Trip
import com.shawnlin.numberpicker.NumberPicker
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragmentTripRecyclerViewAdapter(private var dataSet: MutableList<Trip>):  RecyclerView.Adapter<HomeFragmentTripRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemDate: TextView
        var itemPrice: TextView
        lateinit var  trip: Trip

        init {
            itemDate = itemView.findViewById(R.id.tripDateHomeCard)
            itemName = itemView.findViewById(R.id.tripNameHomeCard)
            itemPrice = itemView.findViewById(R.id.tripPriceHomeCard)

            itemView.setOnClickListener() {
                var navController = itemView.findNavController()
                var b = Bundle()
                val sdf = DateTimeFormatter.ofPattern("dd/MM/yy")
                b.putString("name", itemName.text.toString())
                b.putString("date", trip.date.format(sdf))
                navController.navigate(R.id.action_nav_home_to_singleTripFragment, b)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_trip_in_home, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].text
        holder.itemPrice.text = String.format("$%.2f", dataSet[position].price)

        val sdf = DateTimeFormatter.ofPattern("dd/MM")
        holder.trip = dataSet[position]
        if (dataSet[position].time == ""){
            holder.itemDate.text = sdf.format(dataSet[position].date).toString()
        }
        else {
            holder.itemDate.text = sdf.format(dataSet[position].date).toString() + ", " + dataSet[position].time
        }

    }
//
//    fun setNewList(newList: MutableList<ItemInTrip>) {
//        dataSet = newList
//        notifyDataSetChanged()
//    }
//
//    fun getItemsList(): MutableList<String>{
//        var temp = mutableListOf<String>()
//        for (t in dataSet!!) {
//            temp.add(t.item.itemName)
//        }
//        return  temp
//    }

}