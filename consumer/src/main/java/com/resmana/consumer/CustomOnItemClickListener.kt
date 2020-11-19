package com.resmana.consumer

import android.view.View

class CustomOnItemClickListener(private val position: Int, private val onItemClickCallback: OnItemClickCallback) : View.OnClickListener {
    override fun onClick(v: View) {
        onItemClickCallback.onItemClicked(v, position)
    }

    interface OnItemClickCallback {
        fun onItemClicked(v: View, position: Int)
    }
}


