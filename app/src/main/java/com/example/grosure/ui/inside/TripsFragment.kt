package com.example.grosure.ui.inside

import android.animation.ValueAnimator
import android.content.ContentProvider
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.databinding.FragmentTripsBinding
import com.example.grosure.databinding.TripsCalendarDayBinding
import com.example.grosure.databinding.CalendarDayLegendBinding
import com.example.grosure.databinding.Example3EventItemViewBinding
import com.example.grosure.model.ItemInTrip
import com.example.grosure.model.Trip
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.android.synthetic.main.example_3_event_item_view.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class TripsFragment() : Fragment() {


    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    //    override val titleRes: Int = R.string.example_1_title
    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete this trip?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteEvent(it)
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
                .setTitle("Enter Trip Name")
                .setView(layout)
                .setPositiveButton("Save") { _, _ ->
                    saveEvent(editText.text.toString())
                    // Prepare EditText for reuse.
                    editText.setText("")
                }
                .setNegativeButton("Close", null)
                .create()
                .apply {
                    setOnShowListener {
                        // Show the keyboard
                        editText.requestFocus()
                        context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    }
                    setOnDismissListener {
                        // Hide the keyboard
                        context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    }
                }
    }


    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private var trips = mutableMapOf<LocalDate, List<Trip>>()

    private lateinit var binding: FragmentTripsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTripsBinding.bind(view)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
        val myViewModel: GroSureViewModel by activityViewModels()

        var temp = mutableMapOf<LocalDate, List<Trip>>()
        for (t in myViewModel.events){
            if (t.user.username == myViewModel.currentUser.value!!.username) {
                if (t.date in temp.keys) {
                    temp[t.date] = temp[t.date]!!.plus(t)
                } else {
                    temp[t.date] = listOf(t)
                }
            }
        }

        var memas = temp.keys
        for (dates in memas){
            trips.put(dates, temp.get(dates)!!)
        }


        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(Locale.ENGLISH)
                setTextColorRes(R.color.example_1_white_light)
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(24)
        binding.exOneCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.exOneCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = TripsCalendarDayBinding.bind(view).exOneDayText
            val dotView = TripsCalendarDayBinding.bind(view).dotView
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }

        }

        binding.exOneCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val dotView = container.dotView
                textView.text = day.date.dayOfMonth.toString()
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_1_white)
                            textView.setBackgroundResource(R.drawable.example_1_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_1_white_light)
                            textView.setBackgroundResource(R.drawable.example_1_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.example_1_white)
                            textView.background = null
                            dotView.isVisible = trips[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        binding.exOneCalendar.monthScrollListener = {
            if (binding.exOneCalendar.maxRowCount == 6) {
                binding.exOneYearText.text = it.yearMonth.year.toString()
                binding.exOneMonthText.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                // In week mode, we show the header a bit differently.
                // We show indices with dates from different months since
                // dates overflow and cells in one index can belong to different
                // months/years.
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    binding.exOneMonthText.text = monthTitleFormatter.format(firstDate)
                } else {
                    binding.exOneMonthText.text =
                        "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                        binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    } else {
                        binding.exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }
        }

        binding.floatingActionButton.setOnClickListener(){
            if (selectedDate == null){
                AlertDialog.Builder(requireContext())
                        .setTitle("Please select a date first")
                        .setMessage("")
                        .setPositiveButton("Okay"){ dialog, id ->
                            dialog.dismiss()
                        }
                        .show()
            }
            else {
                inputDialog.show()
            }
        }
        binding.tripsRV.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
    }


    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exOneCalendar.notifyDateChanged(it) }
            binding.exOneCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "Please input some text", Toast.LENGTH_LONG).show()
        }
        else if (text.contains(",")){
            Toast.makeText(requireContext(), "Please do not include a comma in the name", Toast.LENGTH_LONG).show()
        }
        else {
            selectedDate?.let {
                val myViewModel: GroSureViewModel by activityViewModels()
                var sameName = false
                for (events in myViewModel.events) {
                    if (events.date == selectedDate!!) {
                        if (events.text == text) {
                            sameName = true
                        }
                    }
                }

                if (!sameName) {
                    trips[it] = trips[it].orEmpty().plus(Trip(text, it, myViewModel.currentUser.value!!,
                            0.toDouble(), "", mutableListOf<ItemInTrip>()))
                    updateAdapterForDate(it)
                    var t = trips.keys
                    myViewModel.events.clear()
                    for (date in t) {
                        if (trips.get(date) != null) {
                            for (m in trips.get(date)!!) {
                                myViewModel.events.add(m)
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(this.context, "Two events cannot have the same text on the same day.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteEvent(trip: Trip) {
        val date = trip.date
        trips[date] = trips[date].orEmpty().minus(trip)
        updateAdapterForDate(date)
        val myViewModel: GroSureViewModel by activityViewModels()
        var t = trips.keys
        myViewModel.events.clear()
        for (date in t){
            if (trips.get(date) != null) {
                for (m in trips.get(date)!!) {
                    myViewModel.events.add(m)
                }
            }
        }
        writeTrips()
        Log.i("Trips", myViewModel.events.toString())
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            trips.clear()
            trips.addAll(this@TripsFragment.trips[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_1_white_light)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trips, container, false)
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


class Example3EventsAdapter(val onClick: (Trip) -> Unit) :
        RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {

    val trips = mutableListOf<Trip>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
        return Example3EventsViewHolder(
                Example3EventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
        viewHolder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size

    inner class Example3EventsViewHolder(private val binding: Example3EventItemViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnLongClickListener() {
                onClick(trips[bindingAdapterPosition])
                return@setOnLongClickListener  false
            }
            itemView.setOnClickListener(){
                val navController = itemView.findNavController()
                var bundle = bundleOf()
                bundle.putString("name", itemView.itemEventText.text.toString())
                bundle.putString("date", itemView.date.text.toString())
                navController.navigate(R.id.action_nav_trips_to_singleTripFragment, bundle)
            }
        }

        fun bind(trips: Trip) {
            var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
            binding.date.text = trips.date.format(sdf)
            binding.itemEventText.text = trips.text
            if (trips.time != ""){
                binding.itemEventTime.text = trips.time
            }
            var price = 0.0
            if (!trips.itemList.isEmpty()){
                for (t in trips.itemList){
                    price += t.item.itemPrice * t.number
                }
            }
            binding.itemEventPrice.text = String.format("$%.2f", price)
        }
    }
}


