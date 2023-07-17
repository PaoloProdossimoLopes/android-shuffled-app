package com.programou.shuffled.authenticated

data class ItemViewData<D>(
    val identifier: Int,
    val viewData: D
)