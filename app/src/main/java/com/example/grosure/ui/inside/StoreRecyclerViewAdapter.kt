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

class StoreRecyclerViewAdapter():  RecyclerView.Adapter<StoreRecyclerViewAdapter.ViewHolder>() {
    var STORE_LIST = listOf<String>(
        "Fairprice xTRa",
        "Cold Storage",
        "Hao Mart",
        "Tommy Minimart",
        "GIANT SUPER",
        "Sheng Siong"
    )
    var DISTANCES = listOf<String>("200m", "400m", "700m", "1300m", "1500m", "2100m")


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemDistance: TextView

        init {
            itemName = itemView.findViewById(R.id.storeName)
            itemDistance = itemView.findViewById(R.id.distance)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_store, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return STORE_LIST.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = STORE_LIST[position]
        holder.itemDistance.text = DISTANCES[position]
    }
}