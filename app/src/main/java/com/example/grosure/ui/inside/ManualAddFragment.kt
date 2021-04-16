package com.example.grosure.ui.inside

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_success.view.*
import org.json.JSONObject


class ManualAddFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_manual, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model:GroSureViewModel by activityViewModels()


        var imageUrl = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + requireContext().getResources().getResourcePackageName(R.drawable.index)
                + '/' + requireContext().getResources().getResourceTypeName(R.drawable.index)
                + '/' + requireContext().getResources().getResourceEntryName(R.drawable.index) )


        requireView().findViewById<Button>(R.id.fragment_success_button_add_productManual).setOnClickListener{
            Log.i("a",requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceManual).editText!!.toString())
            model.currentUserItems.value!! += (Item(requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameManual).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyManual).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceManual).editText!!.text.toString().toDouble(), imageUrl.toString(), model.currentUser.value!!))
            model.itemList.value!! += (Item(requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameManual).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyManual).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceManual).editText!!.text.toString().toDouble(), imageUrl.toString(), model.currentUser.value!!))
            findNavController().navigate(R.id.action_manualAddFragment_to_nav_items)

        }
    }

}