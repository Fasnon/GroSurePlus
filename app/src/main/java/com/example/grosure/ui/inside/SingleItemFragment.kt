package com.example.grosure.ui.inside

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso

class SingleItemFragment : Fragment(){

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        val itemName = requireArguments().getString("name")
        var item : Item? = null
        for (ite in model.currentUserItems.value!!){
            if (ite.itemName == itemName){
                item = ite
            }
        }


        Picasso.get().load(item!!.itemPicture).into(requireView().findViewById<ImageView>(R.id.productImageSingle))
        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyEdit).editText!!.setText(item.brand)
        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameEdit).editText!!.setText(item.itemName)
        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceEdit).editText!!.setText(String.format("%.2f",item.itemPrice))

        requireView().findViewById<Button>(R.id.fragment_success_button_add_productEDIT).setOnClickListener(){
            item.brand = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyEdit).editText!!.text.toString()
            item.itemName = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameEdit).editText!!.text.toString()
            item.itemPrice = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceEdit).editText!!.text.toString().toDouble()
            findNavController().navigate(R.id.action_singleItemFragment_to_nav_items)
        }
    }
}