package com.example.camera

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_capture_image.view.*

class CameraAdapter(context: Context, var model: ArrayList<Model>,var onClick: OnClick) :
    RecyclerView.Adapter<CameraAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_capture_image,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return model.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide .with(holder.itemView.context) .load(model.get(position).image).into(holder.itemView.profile_image);
        holder.itemView.name.setText(model.get(position).name)
        holder.itemView.job_title.setText(model.get(position).jobTitle)

        holder.itemView.profile_image.setOnClickListener {
            onClick.onClick(position)
        }

    }

    fun updateList(path : String,position : Int) {
        model.get(position).image = path
            notifyDataSetChanged()
    }
    interface OnClick{ fun onClick(position: Int) }
}