package com.example.grosure.ui.before


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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


class RegisterFragment: Fragment() , View.OnClickListener {
    private lateinit var navController: NavController

    companion object {
        var registrationFragment: RegisterFragment? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        requireView().findViewById<Button>(R.id.regisBtn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.regisBtn ->{
                val myViewModel: GroSureViewModel by activityViewModels()

                var alreadyRegistered  = false

                for (t in myViewModel.userList.value!!){
                    if (requireView().findViewById<TextInputLayout>(R.id.editTextTextPersonNameRegistration).editText!!.text.toString() == t.username)
                        alreadyRegistered = true


                }

                if (alreadyRegistered){
                    requireView().findViewById<TextView>(R.id.warningTextRegistration).text = "An account with this username already exists.\nPlease try logging in."

                }
                else if (requireView().findViewById<TextInputLayout>(R.id.editTextTextPasswordRegistration).editText!!.text.toString().length < 8){
                    requireView().findViewById<TextView>(R.id.warningTextRegistration).text = "Your password is too weak.\n Please use a password with 8 characters or more."

                        }
                else{

                    val newUser = User(requireView().findViewById<TextInputLayout>(R.id.editTextTextPersonNameRegistration).editText!!.text.toString(),requireView().findViewById<TextInputLayout>(R.id.editTextTextPasswordRegistration).editText!!.text.toString(), "nil", false)

                    Toast.makeText(requireContext(), "New user ${newUser.username} created.", Toast.LENGTH_LONG).show()

                    myViewModel.currentUser.value = newUser
                    myViewModel.currentUserItems.value = mutableListOf<Item>()
                    myViewModel.loggedIn.value = true
                    myViewModel.userList.value!!.add(newUser)
                    navController.navigate(R.id.action_registerFragment_to_inFragment)

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
                            Log.i("Writing informations", "Regis")
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
                            Log.i("Writing informations", "Regis")
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
