package com.example.grosure.ui.before

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.User
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception


class LoginFragment: Fragment() , View.OnClickListener {
    private lateinit var navController: NavController


    companion object {
        var loginFragment: LoginFragment? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        requireView().findViewById<Button>(R.id.enterBtn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.enterBtn -> {
                val myViewModel: GroSureViewModel by activityViewModels()
                var accountRegistered = false
                var correspondingAccount = User("dummy", "dummy", "dummy", false)

                for (t in myViewModel.userList.value!!){

                    if (requireView().findViewById<TextInputLayout>(R.id.editTextTextPersonNameLogin).editText!!.text.toString() == t.username) {
                        accountRegistered = true
                        correspondingAccount = t
                    }
                }
                if (!accountRegistered){
                    requireView().findViewById<TextView>(R.id.warningTextLogin).text = "No such account found, check your username again."

                }
                else if(requireView().findViewById<TextInputLayout>(R.id.editTextTextPasswordLogin).editText!!.text.toString() != correspondingAccount.password){
                    requireView().findViewById<TextView>(R.id.warningTextLogin).text = "Wrong password, check again."


                }
                else{
                    myViewModel.currentUser.value = correspondingAccount
                    myViewModel.currentUserItems.value = mutableListOf<Item>()
                    myViewModel.loggedIn.value = true
                    navController.navigate(R.id.action_loginFragment_to_inFragment)
                    for (t in myViewModel.itemList.value!!){
                        if (t.user.username == correspondingAccount.username){
                            myViewModel.currentUserItems.value!!.add(t)
                        }
                    }

                    val nameObserver = Observer<MutableList<User>> { newName ->
                        try {
                            val fOut = requireContext().openFileOutput("users", Context.MODE_PRIVATE)
                            if (myViewModel.loggedIn.value!!) {
                                fOut.write(("true" +"," +  myViewModel.currentUser.value!!.username +"\n").toByteArray())
                            } else {
                                fOut.write(("false" +",\n").toByteArray())
                            }

                            for (a: User in myViewModel.userList.value!!) {
                                fOut.write((a.username + "," + a.password + "," + a.profilePicture + "," + a.notifs.toString() +"\n").toByteArray())
                            }
                            fOut.close()
                            Log.i("Writing informations", "Login")
                        }
                        catch (e : Exception){
                        }
                    }
                    val nameObserver2 = Observer<Boolean> { newName ->
                        try {
                            val fOut = requireContext().openFileOutput("users", Context.MODE_PRIVATE)
                            if (myViewModel.loggedIn.value!!) {
                                fOut.write(("true" +"," +  myViewModel.currentUser.value!!.username +"\n").toByteArray())
                            } else {
                                fOut.write(("false" +",\n").toByteArray())
                            }

                            for (a: User in myViewModel.userList.value!!) {
                                fOut.write((a.username + "," + a.password + "," + a.profilePicture + "," + a.notifs.toString() +"\n").toByteArray())
                            }
                            fOut.close()
                            Log.i("Writing informations", "Login")
                        }
                        catch (e : Exception){
                        }
                    }
                    myViewModel.userList.observe(viewLifecycleOwner, nameObserver)
                    myViewModel.loggedIn.observe(viewLifecycleOwner, nameObserver2)


                }
            }
        }
    }
}