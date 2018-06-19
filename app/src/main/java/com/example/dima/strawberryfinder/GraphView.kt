package com.example.dima.strawberryfinder

import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.View


/**
 * Created by Dima on 12.06.2018.
 */

class GraphView(context : Context) : View(context) {

    val width = 150.0
    val height = 40.0
    val pixToCm = 20.0
    var x = (width*pixToCm).toInt()
    var y = (height*pixToCm).toInt()

    var bmp = Bitmap.createBitmap(IntArray(x*y, {i ->  Color.BLACK}),x, y, Bitmap.Config.ARGB_8888)

    init {
        x = 1000
        y = 500
        bmp = Bitmap.createBitmap(IntArray(x*y, {i ->  Color.BLACK}),x, y, Bitmap.Config.ARGB_8888)
    }

    override fun onDraw(canvas: Canvas?) {
        // super.onDraw(canvas)

        canvas!!.drawBitmap(bmp, 0f, 0f , null)
    }
}