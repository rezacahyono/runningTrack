package com.tugaspti.runningtrack.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_MODE_THEME
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_NAME
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setting.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null){
            loadFieldsFromSharedPref()
            btnApplyChange.setOnClickListener {
                val success = applyChangesToSharedPref()
                if(success) {
                    Snackbar.make(requireView(), "Saved changes", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(requireView(), "Please fill out all the fields", Snackbar.LENGTH_SHORT).show()
                }
            }

            loadStateMode()
            swdarkMode.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked){
                    setStateMode(isChecked)
                    Snackbar.make(requireView(), "Dark Mode Active", Snackbar.LENGTH_SHORT).show()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else{
                    setStateMode(isChecked)
                    Snackbar.make(requireView(), "Dark Mode NonActive", Snackbar.LENGTH_SHORT).show()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }


        }
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        return true
    }

    private fun loadStateMode(){
        val checked = sharedPreferences.getBoolean(KEY_MODE_THEME,false)
        swdarkMode.isChecked = checked
        if (checked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setStateMode(state: Boolean){
        sharedPreferences.edit()
                .putBoolean(KEY_MODE_THEME, state)
                .apply()
    }


}