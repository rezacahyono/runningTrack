package com.tugaspti.runningtrack.ui.home

import android.Manifest
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.adapter.RunAdapter
import com.tugaspti.runningtrack.adapter.SlideAdapter
import com.tugaspti.runningtrack.ui.main.MainViewModel
import com.tugaspti.runningtrack.utils.Constant
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_MODE_THEME
import com.tugaspti.runningtrack.utils.Constant.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.tugaspti.runningtrack.utils.SortType
import com.tugaspti.runningtrack.utils.TrackingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var runAdapter: RunAdapter
    lateinit var slideAdapter: SlideAdapter

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runAdapter = RunAdapter()
        slideAdapter = SlideAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null){
            setupRecyclerView()
            setupSlideImage()
            requestPermissions()
            fabRun.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_trackingFragment)
            }
            val name = sharedPref.getString(Constant.KEY_NAME, "")
            if (name != null){
                tvName.text = name
            }
            val theme = sharedPref.getBoolean(KEY_MODE_THEME, false)
            if (theme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            viewModel.imageRun().observe(viewLifecycleOwner, {image ->
                slideAdapter.setAdapter(image)
            })

            when (viewModel.sortType) {
                SortType.DATE -> spFilter.setSelection(0)
                SortType.RUNNING_TIME -> spFilter.setSelection(1)
                SortType.DISTANCE -> spFilter.setSelection(2)
                SortType.AVG_SPEED -> spFilter.setSelection(3)
                SortType.CALORIES_BURNED -> spFilter.setSelection(4)
            }
            viewModel.run.observe(viewLifecycleOwner,  { runs ->
                runAdapter.submitList(runs)
            })

            spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}

                override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        pos: Int,
                        id: Long
                ) {
                    when (pos) {
                        0 -> viewModel.sortRuns(SortType.DATE)
                        1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                        2 -> viewModel.sortRuns(SortType.DISTANCE)
                        3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                        4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                    }
                }
            }
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val run = runAdapter.differ.currentList[position]
            viewModel.deleteRun(run)
            Snackbar.make(requireView(), "Successfully deleted run", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertRun(run)
                }
                show()
            }
        }
    }

    private fun setupRecyclerView() = rvRun.apply {
        adapter = runAdapter
        layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }

    private fun setupSlideImage(){
        slideView.setSliderAdapter(slideAdapter)
        slideView.setIndicatorAnimation(IndicatorAnimations.DROP)
        slideView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        slideView.indicatorSelectedColor = ContextCompat.getColor(requireContext(), R.color.accent)
        slideView.indicatorUnselectedColor = Color.WHITE
        slideView.startAutoCycle()
        slideView.setOnIndicatorClickListener{position -> slideView.currentPagePosition = position}
    }

    private fun requestPermissions() {
        if (TrackingUtils.locationPermision(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setThemeResId(R.style.AlertDialogTheme).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}