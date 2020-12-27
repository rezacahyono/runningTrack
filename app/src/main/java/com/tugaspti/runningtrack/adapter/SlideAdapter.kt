package com.tugaspti.runningtrack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.data.entity.ImageRun
import kotlinx.android.synthetic.main.item_slide.view.*


class SlideAdapter: SliderViewAdapter<SlideAdapter.SlideViewHolder>() {

    private val imageRun = ArrayList<ImageRun>()

    fun setAdapter(image: List<ImageRun>?){
        if (image.isNullOrEmpty()) return
        imageRun.clear()
        imageRun.addAll(image)
        notifyDataSetChanged()
    }

    inner class SlideViewHolder(itemView: View): SliderViewAdapter.ViewHolder(itemView) {
        fun bind(imageRun: ImageRun){
            with(itemView){
                Glide.with(itemView.context)
                    .load(imageRun.image)
                    .fitCenter()
                    .error(R.drawable.ic_error)
                    .into(imageSlide)
            }
        }

    }

    override fun getCount(): Int = imageRun.size

    override fun onCreateViewHolder(parent: ViewGroup?): SlideViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SlideViewHolder?, position: Int) {
        val image = imageRun[position]
        viewHolder?.bind(image)
    }
}