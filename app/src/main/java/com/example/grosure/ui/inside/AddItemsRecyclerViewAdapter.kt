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
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.File

class AddItemsRecyclerViewAdapter(private var dataSet: MutableList<ItemInGetItem>):  RecyclerView.Adapter<AddItemsRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemCompany: TextView
        var itemPrice: TextView
        var itemPicture: ImageView
        var selectedIndicator: ImageView
        var selected = false
        lateinit var itemInGetItem: ItemInGetItem
        init {
            itemCompany = itemView.findViewById(R.id.productBrandCard)
            itemName = itemView.findViewById(R.id.productNameCard)
            itemPicture = itemView.findViewById(R.id.productImageCard)
            selectedIndicator = itemView.findViewById(R.id.selectedIndicator)
            itemPrice = itemView.findViewById(R.id.productPriceCard)
            selectedIndicator.visibility = View.GONE


            itemView.setOnClickListener(){
                if (selectedIndicator.visibility == View.VISIBLE){
                    selectedIndicator.visibility = View.GONE
                    selected=  false
                    itemInGetItem.seleted = false
                }
                else{
                    selectedIndicator.visibility = View.VISIBLE
                    selected = true
                    itemInGetItem.seleted = true
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item_add, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].item.itemName
        holder.itemCompany.text = dataSet[position].item.brand
        holder.itemPrice.text = String.format("$%.2f", dataSet[position].item.itemPrice)
        holder.itemInGetItem = dataSet[position]
        if (holder.itemInGetItem.item.isFile) {
            Picasso.get().load(File(dataSet[position].item.itemPicture)).into(holder.itemPicture)
        }
        else{
            Picasso.get().load(dataSet[position].item.itemPicture).into(holder.itemPicture)
        }
    }

    fun setNewList(newList: MutableList<ItemInGetItem>) {
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

    fun getSelectedItems(): MutableList<Item>{
        var temp = mutableListOf<Item>()
        for (a in dataSet){
            if (a.seleted){
                temp.add(a.item)
            }
        }
        return temp
    }
}