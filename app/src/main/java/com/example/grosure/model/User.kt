package com.example.grosure.model

import com.example.grosure.ui.before.LoginFragment
import com.example.grosure.ui.before.RegisterFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


data class User(var username: String, var password: String, var profilePicture: String, var notifs: Boolean) {    }