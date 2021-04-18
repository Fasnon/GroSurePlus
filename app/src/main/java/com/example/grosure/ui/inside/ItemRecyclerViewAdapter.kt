package com.example.grosure.ui.inside

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.File

class ItemRecyclerViewAdapter(private var dataSet: MutableLiveData<MutableList<Item>>):  RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemCompany: TextView
        var itemPrice: TextView
        var itemPicture: ImageView
        lateinit var item: Item

        init {
            itemCompany = itemView.findViewById(R.id.productBrandCard)
            itemName = itemView.findViewById(R.id.productNameCard)
            itemPicture = itemView.findViewById(R.id.productImageCard)
            itemPrice = itemView.findViewById(R.id.productPriceCard)

            itemView.setOnClickListener(){
                val navController = itemView.findNavController()
                var bundle = bundleOf()
                bundle.putString("name", itemName.text.toString())
                navController.navigate(R.id.action_nav_items_to_singleItemFragment, bundle)
            }

            itemView.setOnLongClickListener(){
                var builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Delete item?")
                builder.setPositiveButton("Confirm"){ dialog, id ->
                    var temp = dataSet.value
                    temp!!.remove(item)
                    dataSet.value = temp
                    notifyDataSetChanged()
                }
                builder.setNegativeButton("Cancel"){ dialog, id ->

                }
                builder.show()


                return@setOnLongClickListener false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.value!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet.value!![position].itemName
        holder.itemCompany.text = dataSet.value!![position].brand
        holder.itemPrice.text = String.format("$%.2f", dataSet.value!![position].itemPrice)
        holder.item = dataSet.value!![position]
        if (holder.item.isFile) {
            Picasso.get().load(File(dataSet.value!![position].itemPicture)).into(holder.itemPicture)
        }
        else{
            Picasso.get().load(dataSet.value!![position].itemPicture).into(holder.itemPicture)
        }
    }

    fun getLiveData(): MutableLiveData<MutableList<Item>>{

        return dataSet
    }

}