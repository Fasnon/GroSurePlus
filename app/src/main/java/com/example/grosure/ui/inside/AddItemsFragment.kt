package com.example.grosure.ui.inside

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.ItemInGetItem
import com.example.grosure.model.ItemInTrip
import com.example.grosure.model.Trip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.format.DateTimeFormatter

class AddItemsFragment : Fragment(){
    var trip: Trip? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        val itemName = requireArguments().getString("name")
        val date = requireArguments().getString("date")
        var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
        for (ite in model.events){
            if  (ite.date.format(sdf) == date){
                if (itemName == ite.text){
                    trip = ite
                }
            }
        }

        var temp = mutableListOf<Item>()
        for (i in model.currentUserItems.value!!){
            var ins = false
            for (i2 in trip!!.itemList){
                if (i2.item == i){
                    ins= true
                }
            }
            if (!ins){
                temp.add(i)
            }

        }

        var temp2 = mutableListOf<ItemInGetItem>()
        for (tem in temp){
            temp2.add(ItemInGetItem(tem, false))
        }



        val rv = requireView().findViewById<RecyclerView>(R.id.addItemsRV)
        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        rv.adapter = AddItemsRecyclerViewAdapter(temp2)


        requireView().findViewById<FloatingActionButton>(R.id.addItemsFAB).setOnClickListener(){

            for (a in (rv.adapter as AddItemsRecyclerViewAdapter).getSelectedItems()){
                trip!!.itemList.add(ItemInTrip(a, 1))
                Log.i("added item", trip!!.text + "," + a.itemName)
            }


            var bundle = bundleOf()
            bundle.putString("name", trip!!.text)
            var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
            bundle.putString("date", trip!!.date.format(sdf).toString())
            findNavController().navigate(R.id.action_addItemsFragment_to_singleTripFragment, bundle)
        }
    }

}