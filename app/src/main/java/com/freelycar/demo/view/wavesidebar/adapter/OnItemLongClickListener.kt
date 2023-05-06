package com.freelycar.demo.view.wavesidebar.adapter

import androidx.recyclerview.widget.RecyclerView

interface OnItemLongClickListener {
    fun onItemLongClick(vh: RecyclerView.ViewHolder?, position: Int)
}