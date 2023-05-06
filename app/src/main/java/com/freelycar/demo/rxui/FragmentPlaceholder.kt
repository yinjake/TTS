package com.freelycar.demo.rxui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.freelycar.demo.R

class FragmentPlaceholder : FragmentLazy() {
    override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View {
        return layoutInflater.inflate(R.layout.fragment_placeholder, viewGroup, false)
    }

    override fun initView() {

    }

    override fun initData() {}

    companion object {
        fun newInstance(): FragmentPlaceholder {
            return FragmentPlaceholder()
        }
    }
}