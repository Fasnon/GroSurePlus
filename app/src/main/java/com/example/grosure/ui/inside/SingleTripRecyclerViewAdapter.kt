package com.example.grosure.ui.inside

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.Image
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

class SingleTripRecyclerViewAdapter(private var trip: Trip, private var dataSet: MutableList<ItemInTrip>, private var ChangeDetector: MutableLiveData<Boolean>):  RecyclerView.Adapter<SingleTripRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemCompany: TextView
        var itemPrice: TextView
        var itemTotalPrice: TextView
        var itemPicture: ImageView
        var itemQuantity: NumberPicker
        lateinit var itemInTrip: ItemInTrip

        init {
            itemCompany = itemView.findViewById(R.id.productBrandCard)
            itemName = itemView.findViewById(R.id.productNameCard)
            itemPicture = itemView.findViewById(R.id.productImageCard)
            itemPrice = itemView.findViewById(R.id.productPriceCard)
            itemTotalPrice = itemView.findViewById(R.id.totalPriceCard)
            itemQuantity = itemView.findViewById(R.id.number_picker)
            itemQuantity.setOnValueChangedListener() { itemQuantity, oldInt, newInt ->
                itemTotalPrice.text = String.format("Total Price: $%.2f", (itemPrice.text.toString().substring(1).toDouble() * newInt))
                itemInTrip.number = newInt
                ChangeDetector.value = !ChangeDetector.value!!
                trip.refreshPrice()
            }

            itemView.setOnLongClickListener() {
                var builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Delete item?")
                builder.setPositiveButton("Confirm") { dialog, id ->
                    trip.itemList.remove(itemInTrip)
                    trip.refreshPrice()
                    ChangeDetector.value = !ChangeDetector.value!!
                    notifyDataSetChanged()
                }
                builder.setNegativeButton("Cancel") { dialog, id ->

                }
                builder.show()
                return@setOnLongClickListener false
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_card, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].item.itemName
        holder.itemCompany.text = dataSet[position].item.brand
        holder.itemPrice.text = String.format("$%.2f", dataSet[position].item.itemPrice)
        Picasso.get().load(dataSet[position].item.itemPicture.toString()).into(holder.itemPicture)
        holder.itemTotalPrice.text = String.format("Total Price: $%.2f", dataSet[position].item.itemPrice * dataSet[position].number)
        holder.itemInTrip = dataSet[position]
        holder.itemQuantity.value = dataSet[position].number

    }

    fun setNewList(newList: MutableList<ItemInTrip>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    fun getItemsList(): MutableList<String>{
        var temp = mutableListOf<String>()
        for (t in dataSet!!) {
            temp.add(t.item.itemName)
        }
        return  temp
    }

}