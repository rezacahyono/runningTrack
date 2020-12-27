package com.tugaspti.runningtrack.utils

import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.data.entity.ImageRun

object DataDummy {

    fun loadImage(): List<ImageRun>{
        val image = ArrayList<ImageRun>()

        image.add(
            ImageRun(R.drawable.image1)
        )
        image.add(
            ImageRun(R.drawable.image2)
        )
        image.add(
            ImageRun(R.drawable.image3)
        )
        image.add(
            ImageRun(R.drawable.image4)
        )
        return  image
    }
}