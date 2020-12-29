package com.tugaspti.runningtrack.ui.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.utils.Constant.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_MODE_THEME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(navContainer.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {}

        navigateTrackingFragmentNeeded(intent)


        navContainer.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.setupFragment, R.id.trackingFragment -> bottomNavigationView.visibility = View.GONE
                    else -> bottomNavigationView.visibility = View.VISIBLE
                }
            }

        val theme = sharedPref.getBoolean(KEY_MODE_THEME, false)
        if (theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateTrackingFragmentNeeded(intent)
    }

    private fun navigateTrackingFragmentNeeded(intent: Intent?){
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navContainer.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}