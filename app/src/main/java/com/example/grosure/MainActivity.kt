package com.example.grosure

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.grosure.model.Item
import com.example.grosure.model.ItemInTrip
import com.example.grosure.model.Trip
import com.example.grosure.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Scanner

class MainActivity : AppCompatActivity(){
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        val myViewModel: GroSureViewModel by viewModels()
        myViewModel.changeDetector.value = true
        myViewModel.loggedIn.value = false
        myViewModel.userList.value = mutableListOf()
        myViewModel.currentUserItems.value = mutableListOf()
        myViewModel.itemList.value = mutableListOf()


        var name = ""


        try {
//            val fo = this.openFileOutput("users", Context.MODE_PRIVATE)
//            fo.write("".toByteArray())
//            fo.close()
            val fin = this.openFileInput("users")

            var temp = ""
            var c = 0
            var bool = true
            while (bool) {
                c = fin.read()
                if (c == -1)
                    bool = false
                else {
                    var cha: Char = c.toChar()
                    temp = temp + cha.toString()
                }
            }

            myViewModel.userList.value = mutableListOf<User>()
            temp = "true,ppp\nppp,11992222,nil,true"


            val scn = Scanner(temp)
            Log.i("aso", temp)
            var firstLine = true
            while (scn.hasNextLine()) {
                if (firstLine) {
                    var two = scn.nextLine().split(",")
                    myViewModel.loggedIn.value = two[0].toBoolean()
                    firstLine = false
                    name = two[1]
                } else {
                    val m = scn.nextLine()
                    val four = m.split(",")
                    myViewModel.userList.value!!.add(User(four[0], four[1], four[2], four[3].toBoolean()))
                }
            }
            scn.close()
            Log.i("loaded", "loaded")
        } catch (e: Exception) {
            Log.i("error", "error loading information from user")
        }




        if (myViewModel.loggedIn.value!!) {
            for (t in myViewModel.userList.value!!){
                if (t.username  == name){
                    myViewModel.currentUser.value = t
                }
            }
        }
        val nameObserver = Observer<MutableList<User>> { newName ->
            writeUsers()
            writeItems()
        }
        val nameObserver4 = Observer<MutableList<Item>> { newName ->
            writeItems()
        }
        val nameObserver2 = Observer<Boolean> { newName ->
            writeUsers()
            writeItems()
        }
        val nameObserver3 = Observer<User> { newName ->
            writeUsers()
            writeItems()
        }
        myViewModel.userList.observe(this, nameObserver)
        myViewModel.loggedIn.observe(this, nameObserver2)
        myViewModel.currentUser.observe(this, nameObserver3)
        myViewModel.itemList.observe(this, nameObserver4)
        myViewModel.currentUserItems.observe(this, nameObserver4)





        try {
            val fin = this.openFileInput("items")

            myViewModel.itemList.value = mutableListOf<Item>()
            var temp = ""
            var c = 0
            var bool = true
            while (bool) {
                c = fin.read()
                if (c == -1)
                    bool = false
                else {
                    var cha: Char = c.toChar()
                    temp = temp + cha.toString()
                }
            }


            val scn = Scanner(temp)
            while (scn.hasNextLine()) {
                var m = scn.nextLine()
                var five = m.split(",")

                var associatedUser = User("dummy", " dummy" , "dummy" , false)
                for (t in myViewModel.userList.value!!){
                    if (five[4] == t.username){
                        associatedUser = t
                    }
                }

                if (associatedUser.profilePicture != "dummy") {
                    myViewModel.itemList.value!!.add(Item(five[0], five[1], five[2].toDouble(), five[3], associatedUser))
                }

            }
            scn.close()
            Log.i("loaded", "loaded")
        } catch (e: Exception) {
            Log.i("error", "error loading information from item")
        }

        if (myViewModel.loggedIn.value!!){
            val tempList = mutableListOf<Item>()
            for (t in myViewModel.itemList.value!!){
                if (t.user.username == myViewModel.currentUser.value!!.username){
                    tempList += t
                }

            }
            myViewModel.currentUserItems.value = tempList
        }
        try {
            val fin = this.openFileInput("trips")

            myViewModel.events = mutableListOf<Trip>()
            var temp = ""
            var c = 0
            var bool = true
            while (bool) {
                c = fin.read()
                if (c == -1)
                    bool = false
                else {
                    var cha: Char = c.toChar()
                    temp = temp + cha.toString()
                }
            }


            val scn = Scanner(temp)
            Log.i("aso", temp)
            while (scn.hasNextLine()) {
                var m = scn.nextLine()
                var five = m.split(",")

                var associatedUser = User("dummy", " dummy" , "dummy" , false)
                for (t in myViewModel.userList.value!!){
                    if (five[2] == t.username){
                        associatedUser = t
                    }
                }

                if (associatedUser.profilePicture != "dummy") {
                    var buildItemInTripList = mutableListOf<ItemInTrip>()
                    if (five.size >= 6) {
                        for (a in 5 until five.size - 1) {
                            if (a % 2 == 1) {
                                var findItem: Item? = null
                                for (item in myViewModel.itemList.value!!) {
                                    if (item.itemName == five[a] && item.user.username == five[2]) {
                                        findItem = item
                                    }
                                }

                                if (findItem != null) {
                                    buildItemInTripList.add(ItemInTrip(findItem, five[a + 1].toInt()))
                                }

                            }
                        }
                    }

                    var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")

                    myViewModel.events.add(Trip(five[0] , LocalDate.parse(five[1], sdf) , associatedUser, five[3].toDouble(), five[4], buildItemInTripList))
                }

            }
            scn.close()
            Log.i("loaded", "loaded")
        } catch (e: Exception) {
            Log.i("error", "error loading information from trip")
        }

        if (myViewModel.loggedIn.value!!){
            val tempList = mutableListOf<Item>()
            for (t in myViewModel.itemList.value!!){
                if (t.user.username == myViewModel.currentUser.value!!.username){
                    tempList += t
                }
            }
            myViewModel.currentUserItems.value = tempList
        }

        if (myViewModel.loggedIn.value!!){

            findNavController(R.id.navHostFragment).navigate(R.id.action_enterFragment_to_inFragment)
        }


    }

    private fun writeUsers(){
        try {
            val myViewModel: GroSureViewModel by viewModels()
            val fOut = this.openFileOutput("users", Context.MODE_PRIVATE)
            if (myViewModel.loggedIn.value!!) {
                fOut.write(("true" +"," +  myViewModel.currentUser.value!!.username +"\n").toByteArray())
            } else {
                fOut.write(("false" +",\n").toByteArray())
            }

            for (a: User in myViewModel.userList.value!!) {
                fOut.write((a.username + "," + a.password + "," + a.profilePicture + "," + a.notifs.toString() +"\n").toByteArray())
            }
            fOut.close()
        }
        catch (e : java.lang.Exception){
        }
    }
    private fun writeItems(){
        try {
            val myViewModel: GroSureViewModel by viewModels()
            val fOut = this.openFileOutput("items", Context.MODE_PRIVATE)
            for (a: Item in myViewModel.itemList.value!!) {
                fOut.write((a.itemName + "," + a.brand + "," + a.itemPrice.toString() + "," + a.itemPicture.toString() +"," + a.user.username +"\n").toByteArray())
            }
            fOut.close()
        }
        catch (e : java.lang.Exception){
        }
    }

    private fun writeTrips(){
        try {
            val myViewModel: GroSureViewModel by viewModels()
            val fOut = this.openFileOutput("trips", Context.MODE_PRIVATE)
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