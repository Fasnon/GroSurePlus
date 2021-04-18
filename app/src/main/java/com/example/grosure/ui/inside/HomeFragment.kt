package com.example.grosure.ui.inside

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.ItemForCompare
import com.example.grosure.model.Trip
import com.google.android.material.card.MaterialCardView
import com.wajahatkarim3.easyflipview.EasyFlipView
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        val model: GroSureViewModel by activityViewModels()

        requireView().findViewById<TextView>(R.id.greetingTV).text = "Hello, ${model.currentUser.value!!.username}!"

        var tripHomeRV = requireView().findViewById<RecyclerView>(R.id.tripsRV)

        tripHomeRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        var pendingTrips = mutableListOf<Trip>()
        var userTrips = mutableListOf<Trip>()

        requireView().findViewById<RecyclerView>(R.id.storesRV).layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        requireView().findViewById<RecyclerView>(R.id.storesRV).adapter = StoreRecyclerViewAdapter()
            for (e in model.events){
            if (e.user == model.currentUser.value!!){
                if (e.date.isAfter(LocalDate.now())) {
                    pendingTrips.add(e)
                }
                userTrips.add(e)
            }
        }

        pendingTrips.sortBy {trip ->
            trip.date
        }
        tripHomeRV.adapter = HomeFragmentTripRecyclerViewAdapter(pendingTrips)

        if (pendingTrips.isEmpty()){
            tripHomeRV.visibility = View.GONE
        }
        else{
            requireView().findViewById<TextView>(R.id.emptyPendingPrompt).visibility = View.GONE
        }

        if (userTrips.isEmpty()){
            requireView().findViewById<TextView>(R.id.emptyPromptInsights).text = "Go create some trips!"
            requireView().findViewById<EasyFlipView>(R.id.mostExpensiveCardFlip).visibility = View.GONE
            requireView().findViewById<EasyFlipView>(R.id.mostSpentItemCardFlip).visibility = View.GONE
            requireView().findViewById<EasyFlipView>(R.id.mostUsedItemCardFlip).visibility = View.GONE
            requireView().findViewById<EasyFlipView>(R.id.thisWeekVsLastWeekCardFlip).visibility = View.GONE
        }
        else{
            requireView().findViewById<TextView>(R.id.emptyPromptInsights).visibility = View.GONE
            var today= LocalDate.now()
            var oneWeekBefore = today.minusDays(7)
            var twoWeeksBefore = oneWeekBefore.minusDays(7)

            var moneyOneWeek = 0.0
            var moneyTwoWeek = 0.0
            var itemComparisonList = mutableListOf<ItemForCompare>()
            for (i in model.currentUserItems.value!!){
                itemComparisonList.add(ItemForCompare(i, 0.0, 0))
            }

            var mostExTrip: Trip? = null
            Log.i("user trip", userTrips.size.toString())

            for (trip in userTrips){
                if (trip.date.isAfter(twoWeeksBefore) && trip.date.isBefore(oneWeekBefore)){
                    moneyTwoWeek += trip.price
                }
                else if (trip.date.isAfter(oneWeekBefore) && trip.date.isBefore(today)){
                    moneyOneWeek += trip.price
                }
                if (mostExTrip == null){
                    mostExTrip = trip
                }
                if (trip.price > mostExTrip.price){
                    mostExTrip = trip
                }

                for (item2 in trip.itemList){
                    for (item in itemComparisonList){
                        if (item.item == item2.item){
                            item.number += item2.number
                            item.refreshPrice()
                        }
                    }
                }
            }

            var mostQ : ItemForCompare? = null
            var mostC : ItemForCompare? = null

            for (t in itemComparisonList){
                if (mostC == null){
                    mostC = t
                }
                if (mostQ == null){
                    mostQ = t
                }
                if (t.price > mostC.price){
                    mostC = t
                }
                if (t.number > mostQ.number)
                    mostQ = t
            }

            var sdf = DateTimeFormatter.ofPattern("dd MMM yyyy")

            requireView().findViewById<TextView>(R.id.mostExpensiveTripTV).text = "Your most expensive trip is ${mostExTrip!!.text}, at ${String.format("$%.2f", mostExTrip!!.price)}, on ${mostExTrip!!.date.format(sdf)}"

            if (mostQ != null)
                requireView().findViewById<TextView>(R.id.mostUsedItemTV).text  = "The item you buy the most is " + mostQ!!.item.itemName
            if (mostC != null)
                requireView().findViewById<TextView>(R.id.mostSpentItemTV).text  = "You've spent the most on ${mostC!!.item.itemName}, at ${String.format("$%.2f", mostC!!.price)}"
            requireView().findViewById<TextView>(R.id.thisWeekVsLastWeekTV).text = "You’ve spent ${String.format("%.2f", moneyOneWeek)} this week as compared to ${String.format("%.2f", moneyTwoWeek)} last week, which is a ${String.format("%02.2f", moneyOneWeek/moneyTwoWeek*100)}% increase."
        }
        super.onViewCreated(view, savedInstanceState)



    }


}