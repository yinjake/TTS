package com.freelycar.demo.view.wavesidebar.adapter

import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {
    fun onItemClick(vh: RecyclerView.ViewHolder?, position: Int)
}