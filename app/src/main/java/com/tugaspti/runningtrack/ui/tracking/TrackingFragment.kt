package com.tugaspti.runningtrack.ui.tracking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.data.entity.Run
import com.tugaspti.runningtrack.service.TrackingService
import com.tugaspti.runningtrack.ui.main.MainViewModel
import com.tugaspti.runningtrack.utils.Constant.Companion.ACTION_PAUSE_SERVICE
import com.tugaspti.runningtrack.utils.Constant.Companion.ACTION_START_OR_RESUME_SERVICE
import com.tugaspti.runningtrack.utils.Constant.Companion.ACTION_STOP_SERVICE
import com.tugaspti.runningtrack.utils.Constant.Companion.MAP_VIEW_BUNDLE_KEY
import com.tugaspti.runningtrack.utils.Constant.Companion.MAP_ZOOM
import com.tugaspti.runningtrack.utils.Constant.Companion.POLYLINE_COLOR
import com.tugaspti.runningtrack.utils.Constant.Companion.POLYLINE_WIDTH
import com.tugaspti.runningtrack.utils.TrackingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    companion object{
        const val CANCEL_DIALOG_TAG = "cancel_dialog_tag"
    }
    @set:Inject
    var weight: Float = 80f

    private var map: GoogleMap? = null

    private var isTracking = false
    private var curTimeInMillis = 0L
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

    private val viewModel: MainViewModel by viewModels()

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewMapBundle =savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        mapView.onCreate(viewMapBundle)

        if (savedInstanceState != null){
            val cancelDialog =parentFragmentManager.findFragmentByTag(CANCEL_DIALOG_TAG) as CancelRunDialog?
            cancelDialog?.setYesListener {
                stopRun()
            }
        }
        btnStart.setOnClickListener {
            runStart()
        }
        btnFinish.setOnClickListener {
            zoomToWholeTrack()
            endRunAndSaveToDb()
        }

        btnClose.setOnClickListener {
            if (view.id == R.id.btnClose){
                showCancelTrackingDialog()
            }
        }
        mapView.getMapAsync{maps ->
            map = maps
            addAllPolylines()
        }
        showButtonCancel()
        subscribeToObservers()
    }

    // subscribe to change livadata object
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, { tracking ->
            updateTracking(tracking)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {path ->
            pathPoints = path
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunMillis.observe(viewLifecycleOwner, {time ->
            curTimeInMillis = time
            val formateTime = TrackingUtils.getFormattedStopWatchTime(time, true)
            tvTimer.text = formateTime
        })
    }

    // when move camera to user location
    private fun moveCameraToUser(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    // add all polyline to path list display screen rotate
    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    // Polyline draw
    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatlng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatlng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatlng)
                .add(lastLatlng)
            map?.addPolyline(polylineOptions)
        }
    }

    // update tracing
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L){
            btnStart.text = getString(R.string.start)
            btnFinish.visibility = View.VISIBLE
        }else if (isTracking){
            btnStart.text = getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            btnFinish.visibility = View.GONE

        }
    }

    @SuppressLint("MissingPermission")
    private fun runStart() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            pauseTrackingService()
        } else {
            startOrResumeTrackingService()
            Timber.d("Started service")
        }
    }

    // start tracking
    private fun startOrResumeTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            requireContext().startService(it)
        }

    // pause tracking
    private fun pauseTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_PAUSE_SERVICE
            requireContext().startService(it)
        }


    // stop tracking
    private fun stopTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_STOP_SERVICE
            requireContext().startService(it)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        val mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    // zoom out the whole is visible , used to make screenshot list track
    private fun zoomToWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (point in polyline) {
                bounds.include(point)
            }
        }
        val width = mapView.width
        val height = mapView.height
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                width,
                height,
                (height * 0.05f).toInt()
            )
        )
    }

    // save run in the room database
    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtils.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run =
                Run(bmp, timestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.mainActtivity),
                "Run saved successfully.",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun stopRun() {
        Timber.d("STOPPING RUN")
        tvTimer.text = getString(R.string._00_00_00_00)
        stopTrackingService()
        findNavController().navigate(R.id.action_trackingFragment_to_homeFragment)
    }

    private fun showButtonCancel(){
        if (curTimeInMillis > 0L){
            btnClose.visibility = View.VISIBLE
        }else{
            btnClose.visibility = View.GONE
        }
    }

    // hows a dialog to cancel the current run.
    private fun showCancelTrackingDialog() {
        CancelRunDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_DIALOG_TAG)
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}