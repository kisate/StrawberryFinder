package com.example.dima.strawberryfinder

import android.support.v4.view.ViewCompat.setScaleY
import android.support.v4.view.ViewCompat.setScaleX
import android.support.v4.view.ViewCompat.setY
import android.support.v4.view.ViewCompat.setX
import android.R.attr.y
import android.opengl.ETC1.getHeight
import android.R.attr.x
import android.content.Context
import android.opengl.ETC1.getWidth
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.text.method.Touch.onTouchEvent
import android.util.Log
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.GestureDetector.OnDoubleTapListener
import android.view.View.OnTouchListener


class StandardGestures(c: Context) : OnTouchListener, GestureDetector.OnGestureListener, OnDoubleTapListener, OnScaleGestureListener {
    private var view: View? = null
    private val gesture: GestureDetector
    private val gestureScale: ScaleGestureDetector
    private var scaleFactor = 1f
    private var inScale: Boolean = false

    init {
        gesture = GestureDetector(c, this)
        gestureScale = ScaleGestureDetector(c, this)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        this.view = view
        gesture.onTouchEvent(event)
        gestureScale.onTouchEvent(event)
        return true
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onFling(event1: MotionEvent, event2: MotionEvent, x: Float, y: Float): Boolean {
        return true
    }

    override fun onLongPress(event: MotionEvent) {}

    override fun onScroll(event1: MotionEvent?, event2: MotionEvent?, x: Float, y: Float): Boolean {
        var newX = view!!.getX()
        var newY = view!!.getY()

        Log.e("coords", "$newX $newY")

        if (!inScale) {
            newX -= x*2
            newY -= y*2
        }
        val wm = view!!.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = wm.defaultDisplay
        val p = Point()
        d.getRealSize(p)



        if (newX > (view!!.getWidth() * scaleFactor - p.x) / 2 + 200) {
            newX = (view!!.getWidth() * scaleFactor - p.x) / 2 + 200
        } else if (newX < -((view!!.getWidth() * scaleFactor - p.x) / 2 + 200)) {
            newX = -((view!!.getWidth() * scaleFactor - p.x) / 2 + 200)
        }

        if (newY > (view!!.getHeight() * scaleFactor - p.y) / 2) {
            newY = (view!!.getHeight() * scaleFactor - p.y) / 2
        } else if (newY < -((view!!.getHeight() * scaleFactor - p.y) / 2)) {
            newY = -((view!!.getHeight() * scaleFactor - p.y) / 2)
        }

        view!!.setX(newX)
        view!!.setY(newY)

        return true
    }

    override fun onShowPress(event: MotionEvent) {}

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        view!!.setVisibility(View.GONE)
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        scaleFactor *= detector.scaleFactor
        scaleFactor = if (scaleFactor < 1) 1f else scaleFactor // prevent our image from becoming too small
        scaleFactor = (scaleFactor * 100).toInt().toFloat() / 100 // Change precision to help with jitter when user just rests their fingers //
        view!!.setScaleX(scaleFactor)
        view!!.setScaleY(scaleFactor)
        onScroll(null, null, 0f, 0f) // call scroll to make sure our bounds are still ok //
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        inScale = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        inScale = false
        onScroll(null, null, 0f, 0f) // call scroll to make sure our bounds are still ok //
    }
}