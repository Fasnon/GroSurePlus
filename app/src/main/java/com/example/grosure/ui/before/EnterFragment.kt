package com.example.grosure.ui.before


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.databinding.FragmentEnterBinding
import com.example.grosure.model.User
import java.lang.Exception


class EnterFragment: Fragment() , View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var fragmentEnterBinding: FragmentEnterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEnterBinding = FragmentEnterBinding.inflate(inflater, container, false)
        return fragmentEnterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        fragmentEnterBinding.loginBtn.setOnClickListener(this)
        fragmentEnterBinding.registerBtn.setOnClickListener(this)
        val myViewModel: GroSureViewModel by activityViewModels()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginBtn -> navController.navigate(R.id.action_enterFragment_to_loginFragment)
            R.id.registerBtn -> navController.navigate(R.id.action_enterFragment_to_registerFragment)
        }
    }
}