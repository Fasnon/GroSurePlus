package com.example.grosure

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.grosure.model.Item
import com.example.grosure.model.Trip
import com.example.grosure.model.User

class GroSureViewModel : ViewModel() {
   var events = mutableListOf<Trip>()
   var currentUser = MutableLiveData<User>()
   var userList = MutableLiveData<MutableList<User>>()
   var currentUserItems = MutableLiveData<MutableList<Item>>()
   var itemList = MutableLiveData<MutableList<Item>>()
   var loggedIn = MutableLiveData<Boolean>()
   var changeDetector = MutableLiveData<Boolean>()
   var tripList = MutableLiveData<MutableList<Trip>>()
}