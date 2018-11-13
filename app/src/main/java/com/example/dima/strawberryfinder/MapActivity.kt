package com.example.dima.strawberryfinder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_map.*
import android.graphics.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.view.ScaleGestureDetector

import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.text.method.Touch.onTouchEvent
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.support.v4.view.ViewCompat.setScaleY
import android.support.v4.view.ViewCompat.setScaleX
import android.text.method.Touch.onTouchEvent
import android.text.method.Touch.onTouchEvent
import android.util.AttributeSet
import android.widget.*
import android.text.method.Touch.onTouchEvent
import android.view.ViewGroup






class MapActivity : FullscreenActivity() {

    class Berry(val x : Double, val y : Double, val id : Int, var status : Int, val price : Double, val encoder : Int, val pic : String, val classId : Int, val half : Int)


    var width = 130.0
    var height = 40.0
    var berryWidth = width/20
    var pixToCmOnScreen = 10.0
    val pixToCmOnCamera = 480/20.7
    val cameraOffset = 2.0;
    val encToCm = 4.1/360
    val xa = (width*pixToCmOnScreen).toInt()
    val ya = (height*pixToCmOnScreen).toInt()
    val berryes = mutableListOf<Berry>()
    val reference = FirebaseDatabase.getInstance().reference
    val spacing = 0
    var standardGestures: StandardGestures? = null

    private var mScaleFactor = 1.0f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        main()
    }

//    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        super.dispatchTouchEvent(event)
//
//        return mScaleGestureDetector!!.onTouchEvent(event)
//    }


    private fun main() {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val field = dataSnapshot.child("field")
                val berryData = dataSnapshot.child("berrys")


                height = field.child("height").value.toString().toDouble()
                width = field.child("width").value.toString().toDouble()
                berryWidth = width/20

                val params = maplayout.layoutParams as FrameLayout.LayoutParams
                //val scale = resources.displayMetrics.density

                val display = windowManager.defaultDisplay
                val size = Point()
                display.getRealSize(size)

                pixToCmOnScreen = size.y/height*(1-spacing*2)

                params.height = (size.y).toInt()
                params.width = (width*pixToCmOnScreen).toInt()
                params.topMargin = (size.y*spacing).toInt()
                maplayout.layoutParams = params

                berryes.clear()

                berryData.children.forEach{child -> berryes.add(Berry(child.child("x").value.toString().toDouble(), child.child("y").value.toString().toDouble(),
                        child.child("id").value.toString().toInt(), child.child("status").value.toString().toInt(), child.child("price").value.toString().toDouble(),
                        child.child("encoder").value.toString().toInt(), child.child("pic").value.toString(), child.child("classID").value.toString().toInt(),
                        child.child("half").value.toString().toInt()))}

                maplayout.removeAllViews()

                berryes.forEach{berry -> addBerry(berry)}

                standardGestures = StandardGestures(this@MapActivity)

                maplayout.setOnTouchListener(standardGestures)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("DatabaseError", databaseError.code.toString())
            }


        })
    }

    fun addBerry(berry : Berry) {

        if (berry.classId > 0) {

            val view = ImageView(this)

            if (berry.classId == 1) view.setImageResource(R.drawable.strawberry_good)
            else if (berry.classId == 2) view.setImageResource(R.drawable.strawberry_unripe)
            else if (berry.classId == 3) view.setImageResource(R.drawable.strawberry_bad)

            view.scaleType = ImageView.ScaleType.FIT_START
            view.adjustViewBounds = true
            //view.background = BitmapDrawable(Bitmap.createBitmap(intArrayOf(Color.BLACK), 1, 1, Bitmap.Config.RGB_565))
            val scale = resources.displayMetrics.density


            val params = RelativeLayout.LayoutParams((berryWidth * pixToCmOnScreen).toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = (berry.encoder * encToCm * pixToCmOnCamera).toInt()
            if (berry.half == 1) params.topMargin = ((berry.y / pixToCmOnCamera + height/2 - cameraOffset)*pixToCmOnScreen).toInt()
            else if (berry.half == 2) params.topMargin = ((height/2 - berry.y / pixToCmOnCamera + cameraOffset)*pixToCmOnScreen).toInt()


            maplayout.addView(view, params)

            if (berry.status == 0) view.setOnClickListener { displayDialog1(view, berry) }
            if (berry.status == 1) {
                view.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
                view.setOnClickListener { displayDialog2(view, berry) }
            } else if (berry.status == 2) view.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)

        }
    }

    fun displayDialog1(view : View, berry : Berry) {
        textView.text = "Price : ${berry.price}. Buy?"

        val imageBytes = android.util.Base64.decode(berry.pic, android.util.Base64.DEFAULT)

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))

        button2.setOnClickListener {
            berry.status = 1
            reference.child("berrys").child(berry.id.toString()).child("status").setValue("1")
            buyLayout.visibility = View.GONE
        }

//        Log.d("params", "width : ${maplayout.width} height : ${maplayout.height}")
//
////        maplayout.scaleX = 2f
////        maplayout.scaleY = 2f
//
//        var params = maplayout.layoutParams
//        params = FrameLayout.LayoutParams((params.width*2f).toInt(), (params.height*2f).toInt())
//
//        maplayout.layoutParams = params
//
//        resizeChildren()
//
//
//        Log.d("params", "width : ${maplayout.width} height : ${maplayout.height}")

        button3.setOnClickListener { buyLayout.visibility = View.GONE }

        buyLayout.visibility = View.VISIBLE
    }

    fun displayDialog2(view : View, berry : Berry) {
        textView.text = "Price : ${berry.price}. Remove from queue?"

        val imageBytes = android.util.Base64.decode(berry.pic, android.util.Base64.DEFAULT)

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))


        button2.setOnClickListener {
            berry.status = 0
            reference.child("berrys").child(berry.id.toString()).child("status").setValue("0")
            buyLayout.visibility = View.GONE
        }
        button3.setOnClickListener { buyLayout.visibility = View.GONE }

        buyLayout.visibility = View.VISIBLE
    }

    private fun resizeChildren() {
        // size is in pixels so make sure you have taken device display density into account
        val childCount = maplayout.childCount
        for (i in 0 until childCount) {
            val v = maplayout.getChildAt(i)

            val params = v.layoutParams as RelativeLayout.LayoutParams

            params.width = (params.width*mScaleFactor).toInt()
            params.height = (params.height*mScaleFactor).toInt()

            params.topMargin = (params.topMargin*mScaleFactor).toInt()
            params.leftMargin = (params.leftMargin*mScaleFactor).toInt()

            v.layoutParams = params
        }
    }
}
