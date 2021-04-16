package com.example.grosure.ui.inside

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.R.layout
import com.example.grosure.model.Item
import com.google.android.material.navigation.NavigationView


class InFragment: Fragment() , View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layout.fragment_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val model: GroSureViewModel by activityViewModels()
        val drawerLayout: DrawerLayout = requireView().findViewById(R.id.drawer_layout)
        val navView: NavigationView = requireView().findViewById(R.id.nav_view)
        val navControllerMenu = findNavController(this.requireActivity(), R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack(String name, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        var appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_items,
                R.id.nav_trips,
                R.id.nav_locator,
                R.id.nav_settings
            ), drawerLayout
        )

        navView.setupWithNavController(navControllerMenu)

        requireView().findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            drawerLayout.open()
            requireView().findViewById<ImageButton>(R.id.imageButton).animate()
        }

        var header = requireView().findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        header.findViewById<TextView>(R.id.profileTextView).setOnClickListener {
            navController.navigate(R.id.action_inFragment_to_profileFragment)
        }
        header.findViewById<TextView>(R.id.usernameHeaderTV).text =
            model.currentUser.value!!.username
    }





    override fun onClick(v: View?) {
        when (v!!.id) {
            else -> requireActivity().onBackPressed()
        }
    }


}
//    override fun onOptionsItemSelected(item: MenuItem) {
//        when (item.getItemId()) {
//            R.id.nav_home -> {
//                navController.navigate(R.layout.fragment_home)
//            }
//            }
//        }
//
//    return "a" as Unit
//
//
//    }