package com.example.dima.strawberryfinder

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Shader.TileMode
import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_map.*
import android.widget.RelativeLayout
import java.util.*
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.view.Display
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener




class MapActivity : FullscreenActivity() {

    class Berry(val x : Double, val y : Double, val id : Int, var status : Int, val price : Double)


    var width = 150.0
    var height = 40.0
    var berryWidth = width/20
    var pixToCm = 10.0
    val xa = (width*pixToCm).toInt()
    val ya = (height*pixToCm).toInt()
    val berryes = mutableListOf<Berry>()
    val reference = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        main()
    }

    fun main() {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val field = dataSnapshot.child("field")
                val berryData = dataSnapshot.child("berrys")

                height = field.child("height").value.toString().toDouble()
                width = field.child("width").value.toString().toDouble()
                berryWidth = width/10

                val params = maplayout.layoutParams as FrameLayout.LayoutParams
                //val scale = resources.displayMetrics.density

                val display = windowManager.defaultDisplay
                val size = Point()
                display.getRealSize(size)

                params.height = (size.y*0.8).toInt()
                params.width = (width*pixToCm).toInt()
                params.topMargin = (size.y*0.1).toInt()
                maplayout.layoutParams = params

                berryData.children.forEach({child -> berryes.add(Berry(child.child("x").value.toString().toDouble(), child.child("y").value.toString().toDouble(), child.child("id").value.toString().toInt(), child.child("status").value.toString().toInt(), child.child("price").value.toString().toDouble()))})

                maplayout.removeAllViews()

                berryes.forEach({berry -> addBerry(berry)})


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("DatabaseError", databaseError.code.toString())
            }
        })
    }

    fun addBerry(berry : Berry) {
        val view = ImageView(this)

        view.setImageResource(R.drawable.strawberry)
        view.scaleType = ImageView.ScaleType.CENTER
        view.adjustViewBounds = true
        //view.background = BitmapDrawable(Bitmap.createBitmap(intArrayOf(Color.BLACK), 1, 1, Bitmap.Config.RGB_565))
        val scale = resources.displayMetrics.density


        val params = RelativeLayout.LayoutParams((berryWidth*pixToCm).toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.leftMargin = (berry.x*pixToCm).toInt()
        params.topMargin = (berry.y*pixToCm).toInt()
        maplayout.addView(view, params)


        view.setOnClickListener { displayDialog(view, berry) }
    }

    fun displayDialog(view : View, berry : Berry) {
        textView.text = "Price : ${berry.price}\n Buy?"
        button2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                berry.status = 1
                reference.child("berrys").child(berry.id.toString()).child("status").setValue("1")
                buyLayout.visibility = View.GONE
            }
        })
        button3.setOnClickListener { buyLayout.visibility = View.GONE }

        buyLayout.visibility = View.VISIBLE
    }
}
