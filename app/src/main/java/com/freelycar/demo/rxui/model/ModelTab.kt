package com.freelycar.demo.rxui.model

import com.freelycar.demo.view.tablayout.listener.TabLayoutModel


data class ModelTab(override var tabTitle: String, override var tabSelectedIcon: Int, override var tabUnselectedIcon: Int) :
    TabLayoutModel