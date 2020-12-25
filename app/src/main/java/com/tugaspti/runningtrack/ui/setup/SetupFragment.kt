package com.tugaspti.runningtrack.ui.setup

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_FIRST_TIME_TOGGLE
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_NAME
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var firstTimeAppOpen: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null){
            if (!firstTimeAppOpen) {
                val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.setupFragment, true)
                        .build()
                findNavController().navigate(
                        R.id.action_setupFragment_to_homeFragment,
                        savedInstanceState,
                        navOptions
                )
            }

            btnKuy.setOnClickListener {
                val success = writePersonalDataToSharedPref()
                if (success) {
                    findNavController().navigate(R.id.action_setupFragment_to_homeFragment)
                } else {
                    Snackbar.make(requireView(), "Please enter all the fields.", Snackbar.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = etName.text.toString()
        val weightText = etWeight.text.toString()
        if (name.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }
}