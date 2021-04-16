package com.example.grosure.ui.inside

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.User
import java.lang.Exception

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        requireView().findViewById<CheckBox>(R.id.checkbox).isChecked = model.currentUser.value!!.notifs

        requireView().findViewById<CheckBox>(R.id.checkbox).setOnClickListener{
            var t = model.currentUser.value!!
            t.notifs =  requireView().findViewById<CheckBox>(R.id.checkbox).isChecked
            model.currentUser.value = t
            try {
                val fOut = requireContext().openFileOutput("users", Context.MODE_PRIVATE)
                if (model.loggedIn.value!!) {
                    fOut.write(("true" +"," +  model.currentUser.value!!.username +"\n").toByteArray())
                } else {
                    fOut.write(("false" +",\n").toByteArray())
                }

                for (a: User in model.userList.value!!) {
                    fOut.write((a.username + "," + a.password + "," + a.profilePicture + "," + a.notifs.toString() +"\n").toByteArray())
                }
                fOut.close()
                Log.i("Writing informations", "Write from notifications")
            }
            catch (e : Exception){
            }
        }
    }
}