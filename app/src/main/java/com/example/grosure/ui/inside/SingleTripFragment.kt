package com.example.grosure.ui.inside

import android.R.attr.delay
import android.app.*
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grosure.GroSureViewModel
import com.example.grosure.MainActivity
import com.example.grosure.R
import com.example.grosure.model.Trip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class SingleTripFragment : Fragment(){
    var trip: Trip? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        val itemName = requireArguments().getString("name")
        val date = requireArguments().getString("date")
        var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")


        for (ite in model.events){
            Log.i("model events got something", "?")
            if  (ite.date.format(sdf) == date){
                if (itemName == ite.text){
                    trip = ite
                }
            }
        }

        if (trip != null) {
            requireView().findViewById<TextView>(R.id.singleTripName).text = trip!!.text
            var p = 0.0
            for (i in trip!!.itemList){
                p += i.item.itemPrice * i.number
            }
            trip!!.price = p
            requireView().findViewById<TextView>(R.id.singleTripCost).text = String.format("Total Price: $%.2f", trip!!.price)
            if (trip!!.time == "")
                requireView().findViewById<TextView>(R.id.singleTripTime).text = "Time Scheduled: No time specified"
            else
                requireView().findViewById<TextView>(R.id.singleTripTime).text = "Time Scheduled: ${trip!!.time}"

        }

        requireView().findViewById<FloatingActionButton>(R.id.singleTripFAB).setOnClickListener(){
            var bundle = bundleOf()
            bundle.putString("name", trip!!.text)
            var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
            bundle.putString("date", trip!!.date.format(sdf).toString())
            findNavController().navigate(R.id.action_singleTripFragment_to_addItemsFragment, bundle)
        }


        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_singleTripFragment_to_nav_trips)
            }
        })


        val nameObserver = Observer<Boolean> { newList ->
            var p = 0.0
            for (i in trip!!.itemList){
                p += i.item.itemPrice * i.number
            }
            trip!!.price = p
            requireView().findViewById<TextView>(R.id.singleTripCost).text = String.format("Total Price: $%.2f", trip!!.price)
            writeTrips()

        }
        var changeDetector = MutableLiveData<Boolean>(true)
        changeDetector.observe(viewLifecycleOwner, nameObserver)

        var singleTripRV = requireView().findViewById<RecyclerView>(R.id.tripItemsRV)
        singleTripRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        singleTripRV.adapter = SingleTripRecyclerViewAdapter(trip!!, trip!!.itemList, changeDetector)

        writeTrips()

        requireView().findViewById<TextView>(R.id.shareTV).setOnClickListener{
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            var textMaker = "Trip Name: ${trip!!.text}\n" +
                    "Trip Date: ${trip!!.date.format(sdf)}\n" +
                    "Trip Price: ${String.format("$%.2f",trip!!.price)}\n\n"

            for (item in trip!!.itemList){
                textMaker += item.item.itemName + ": " + String.format("$%.2f", item.item.itemPrice) + "x ${item.number} = ${String.format("$%.2f", item.number * item.item.itemPrice)}\n\n"
            }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textMaker)
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        requireView().findViewById<TextView>(R.id.singleTripTime).setOnClickListener{

            // TODO Auto-generated method stub


            // TODO Auto-generated method stub
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(requireContext(), OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                requireView().findViewById<TextView>(R.id.singleTripTime).setText("Time Specified: ${String.format("%02d", selectedHour)}:${String.format("%02d", selectedMinute)}")
                trip!!.time = "${String.format("%02d", selectedHour)}:${String.format("%02d", selectedMinute)}"
                writeTrips()
            }, hour, minute, true) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()

        }


        if (model.currentUser.value!!.notifs){
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
                    .setContentTitle(requireContext().getString(R.string.app_name))
                    .setContentText(requireContext().getString(R.string.notification_name))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logomini)
                    .setLargeIcon((requireContext().resources.getDrawable(R.drawable.logo) as BitmapDrawable).bitmap)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            val intent = Intent(context, MainActivity::class.java)

            val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            var date = LocalDateTime.now()
            if (trip!!.time == "") {
                date = LocalDateTime.parse(trip!!.date.toString() + " 00:00", sdf)
            }
            else{
                date = LocalDateTime.parse(trip!!.date.toString() + " " + trip!!.time, sdf)

            }
            var now = LocalDateTime.now()
            var temp = LocalDateTime.from(now)

            var notificationId = temp.until(date, ChronoUnit.MILLIS)
            val activity = PendingIntent.getActivity(context, notificationId.toInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT)
            builder.setContentIntent(activity)

            val notification: Notification = builder.build()

            val notificationIntent = Intent(context, MyNotificationPublisher::class.java)
            notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId)
            notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
            val pendingIntent = PendingIntent.getBroadcast(context, notificationId.toInt(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            val futureInMillis = SystemClock.elapsedRealtime() + delay
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent

        }


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        requireView().findViewById<TextView>(R.id.cloneTV).setOnClickListener{

            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                if (trip!= null) {
                    var clone = Trip(trip!!.text +" 2", trip!!.date, trip!!.user, trip!!.price, trip!!.time, trip!!.itemList)
                    var sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    var ld = LocalDate.parse("${String.format("%02d", dayOfMonth)}/${String.format("%02d", monthOfYear+1)}/${year}", sdf)
                    clone.date = ld
                    model.events.add(clone)
                    writeTrips()
                    Toast.makeText(requireContext(), "Trip cloned to ${dayOfMonth}/${monthOfYear+1}", Toast.LENGTH_SHORT).show()
                }

//                textView.setText("" + dayOfMonth + " " + month + ", " + year)
            }, year, month, day)
            dpd.show()
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
            Log.i("trips written", " single trip fragment")
        }
        catch (e : java.lang.Exception){
        }
    }


}