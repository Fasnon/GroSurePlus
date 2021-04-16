package com.example.grosure.ui.inside

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Person
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.Trip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.fragment_items.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class ItemsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var clicked = false

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom)
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemFAB = requireView().findViewById<FloatingActionButton>(R.id.itemsFAB)
        val scanFAB = requireView().findViewById<FloatingActionButton>(R.id.barcodeFAB)
        val manualFAB = requireView().findViewById<FloatingActionButton>(R.id.manualFAB)


        itemFAB.setOnClickListener() {

            when (clicked) {
                true -> {
                    scanFAB.visibility = View.GONE
                    manualFAB.visibility = View.GONE
                    scanFAB.startAnimation(toBottom)
                    manualFAB.startAnimation(toBottom)
                    itemFAB.startAnimation(rotateClose)

                }
                false -> {
                    scanFAB.visibility = View.VISIBLE
                    manualFAB.visibility = View.VISIBLE
                    scanFAB.startAnimation(fromBottom)
                    manualFAB.startAnimation(fromBottom)
                    itemFAB.startAnimation(rotateOpen)
                }
            }
            clicked = !clicked
        }

        scanFAB.setOnClickListener {

            var navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_nav_items_to_scanBarcodeFragment)

            
        }
        manualFAB.setOnClickListener {

            var navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_nav_items_to_manualAddFragment)


        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
        val model : GroSureViewModel by activityViewModels()

        var itemsRV = requireView().findViewById<RecyclerView>(R.id.itemsRV)
        itemsRV.adapter = ItemRecyclerViewAdapter(model.currentUserItems)
        itemsRV.layoutManager = GridLayoutManager(requireContext(), 2)


        val nameObserver = Observer<MutableList<Item>> { newList ->
            // Update the UI, in this case, a TextView.
            Log.i("size if newList", newList.size.toString())
            var removedItem: Item? = null
            for (item in model.itemList.value!!) {
                if (item.user.username == model.currentUser.value!!.username) {
                    var ins = false
                    for (item2 in newList) {
                        if (item2.itemName == item.itemName && item2.itemPicture == item.itemPicture) {
                            ins = true
                        }
                    }
                    if (!ins) {
                        removedItem = item
                    }
                }
            }
            if (removedItem != null) {
                model.itemList.value!!.remove(removedItem)
                for (t in model.events){
                    for (i in t.itemList){
                        if (i.item == removedItem){
                            t.itemList.remove(i)
                        }
                    }
                }
        }
            else{
                Log.i("no removed item  item is", "found")

            }

            writeItems()
            writeTrips()
        }
        (itemsRV.adapter as ItemRecyclerViewAdapter).getLiveData().observe(viewLifecycleOwner, nameObserver)
        val searchTo = requireView().findViewById<EditText>(R.id.adminDiseaseSearch)

//        searchTo.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable) {
//
//                // you can call or do what you want with your EditText here
//
//                var posList = listOf<Int>()
//                var diseaseList = (itemsRV!!.adapter as ItemRecyclerViewAdapter).getItemsList()
//                for (a in diseaseList.indices){
//                    Log.i(diseaseList[a].toLowerCase(), requireView().findViewById<EditText>(R.id.adminDiseaseSearch).text.toString().toLowerCase())
//                    if (diseaseList[a].toLowerCase().contains(requireView().findViewById<EditText>(R.id.adminDiseaseSearch).text.toString().toLowerCase())){
//                        posList += a
//                    }
//                }
//
//
//                (itemsRV!!.adapter as ItemRecyclerViewAdapter).updateArrays(posList.toTypedArray())
//                // yourEditText...
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Log.i(s.toString(), "A")
//                // you can call or do what you want with your EditText here
//
//                var posList = listOf<Int>()
//                var diseaseList = (recyclerView!!.adapter as DiseaseRecyclerViewAdapter).getDiseaseList()
//                for (a in diseaseList.indices){
//                    Log.i(diseaseList[a].toLowerCase(), requireView().findViewById<EditText>(R.id.adminDiseaseSearch).text.toString().toLowerCase())
//                    if (diseaseList[a].toLowerCase().contains(requireView().findViewById<EditText>(R.id.adminDiseaseSearch).text.toString().toLowerCase())){
//                        posList += a
//                    }
//                }
//
//
//                (recyclerView!!.adapter as DiseaseRecyclerViewAdapter).updateArrays(posList.toTypedArray())
//            }
//        })
//
//        if (vaccineStatus == null) {
//            vaccineStatus = Array(8) { "" }
//        }
//        (recyclerView!!.adapter as DiseaseRecyclerViewAdapter).setStatuses(vaccineStatus!!)
//    }
    }
    private fun writeItems(){
        try {
            val myViewModel: GroSureViewModel by activityViewModels()
            val fOut = requireContext().openFileOutput("items", Context.MODE_PRIVATE)
            for (a: Item in myViewModel.itemList.value!!) {

                fOut.write((a.itemName + "," + a.brand + "," + a.itemPrice.toString() + "," + a.itemPicture.toString() +"," + a.user.username +"\n").toByteArray())
            }
            if (myViewModel.itemList.value!!.isEmpty()){
                fOut.write("".toByteArray())
            }
            fOut.close()

            Log.i("successfully written", "itemsfragments")
        }
        catch (e : java.lang.Exception){
        }
    }

    private fun writeTrips(){
        try {
            val myViewModel: GroSureViewModel by activityViewModels()
            val fOut = requireContext().openFileOutput("trips", Context.MODE_PRIVATE)
            var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
            for (a: Trip in myViewModel.events) {
                var stringBuild = a.text + "," + a.date.format(sdf) + "," + a.user.username + "," + a.price.toString() +"," + a.time
                for (item in a.itemList){
                    stringBuild += "," + item.item.itemName + "," + item.number.toString()
                }
                stringBuild+= "\n"
                fOut.write((stringBuild).toByteArray())
            }
            fOut.close()
        }
        catch (e : java.lang.Exception){
        }
    }
}
