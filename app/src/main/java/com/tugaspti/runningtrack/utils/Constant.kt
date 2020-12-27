package com.tugaspti.runningtrack.utils

import android.graphics.Color
import com.github.mikephil.charting.data.LineDataSet

class Constant {

    companion object{
        // name database
        const val RUNNING_DATABASE_NAME = "running_db"

        // location perimssions
        const val REQUEST_CODE_LOCATION_PERMISSION = 0

        // service intent action
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

        // timer
        const val TIMER_UPDATE_INTERVAL = 50L

        // tracking
        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

        // map
        val POLYLINE_COLOR = Color.rgb(0,176,166)
        const val POLYLINE_WIDTH = 17f
        const val MAP_ZOOM = 15f

        // map view key
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

        // MapView
        const val MAP_VIEW_HEIGHT_IN_DP = 200f

        // notification
        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val NOTIFICATION_ID = 1

        // LineChart
        val LINE_DATA_MODE = LineDataSet.Mode.CUBIC_BEZIER

        // Shared Preferences
        const val SHARED_PREFERENCES_NAME = "sharedPref"
        const val KEY_NAME = "KEY_NAME"
        const val KEY_WEIGHT = "KEY_WEIGHT"
        const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
        const val KEY_MODE_THEME = "KEY_MODE_THEME"
    }

}