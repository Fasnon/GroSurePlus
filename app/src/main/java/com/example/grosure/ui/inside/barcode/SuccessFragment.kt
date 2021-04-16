package com.example.grosure.ui.inside.barcode

import android.content.ContentResolver
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
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


class SuccessFragment : Fragment() {

    lateinit var code: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_success, container, false)

        code = requireArguments().get("code") as String

        v.fragment_success_text_view_title.text = "Product barcode: " + code.toString()

        v.fragment_success_button_back_to_scanner.setOnClickListener {
            findNavController().navigateUp()
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val model: GroSureViewModel by activityViewModels()



        var url = "https://barcode.monster/api/${code}"

        var imageUrl = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + requireContext().getResources().getResourcePackageName(R.drawable.index)
                + '/' + requireContext().getResources().getResourceTypeName(R.drawable.index)
                + '/' + requireContext().getResources().getResourceEntryName(R.drawable.index) )




        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {
                    Log.i("WTF", "WT2F2")
                    val jsonObject = JSONObject(response)
                    if (response != null) {
                        Log.i("responce", response)
                    }

                    val availableFields  = mutableListOf<String>()

                    var keys = jsonObject.keys()

                    while (keys.hasNext()){
                        availableFields +=  keys.next()
                    }


                    if ("image_url" in availableFields){
                        Picasso.get().load(jsonObject.getString("image_url").toString())
                            .into(requireView().findViewById<ImageView>(R.id.productImage))

                        imageUrl  = jsonObject.getString("image_url").toUri()
                        if (jsonObject.getString("image_url") == ""){
                            Picasso.get().load(imageUrl.toString())
                                .into(requireView().findViewById<ImageView>(R.id.productImage))
                        }
                    }
                    else{
                        requireView().findViewById<ImageView>(R.id.productImage).setImageResource(R.drawable.index)
                    }

                    if ("company" in availableFields){
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setText(jsonObject.getString("company"))


                        if ( jsonObject.getString("company") == ""){
                            requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setHint("no manufacturer found")
                        }

                    }
                    else {
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setHint("no manufacturer found")
                    }

                    if ("description" in availableFields){
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setText(
                            jsonObject.getString("description"))

                        if ( jsonObject.getString("description") == ""){
                            requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setHint("No Product name found")
                        }

                    }
                    else {
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setHint("No Product name found")
                    }

                    requireView().findViewById<ProgressBar>(R.id.progressBar2).visibility = View.GONE

                }
                catch(e: Exception){
                    Log.i("WTF", "WTF12")
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

                Log.i("WTF", "WTF22")
                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).visibility = View.GONE
                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).visibility = View.GONE
                requireView().findViewById<TextView>(R.id.fragment_success_text_view_name).setText("Use manual addition for now")
                requireView().findViewById<TextView>(R.id.fragment_success_text_view_company).setText( "No Internet Connection found")
                requireView().findViewById<ImageView>(R.id.productImage).setImageResource(R.drawable.index)
                requireView().findViewById<ProgressBar>(R.id.progressBar2).visibility = View.GONE

            }
        })

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        stringRequest.retryPolicy = DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(stringRequest)


        requireView().findViewById<Button>(R.id.fragment_success_button_add_product).setOnClickListener{
            Log.i("a",requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.toString())
            model.currentUserItems.value!!.plusAssign(Item(requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.text.toString().toDouble(), imageUrl.toString(), model.currentUser.value!!))
            model.itemList.value!!.plusAssign(Item(requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.text.toString(), requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.text.toString().toDouble(), imageUrl.toString(), model.currentUser.value!!))
            findNavController().navigate(R.id.action_successFragment_to_nav_items)

        }
    }

}