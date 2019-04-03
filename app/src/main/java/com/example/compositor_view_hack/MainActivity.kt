package com.example.compositor_view_hack

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.transition.Transition
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var layout = FrameLayout(this)
        layout.setBackgroundColor(Color.WHITE)
        setContentView(layout)

        Handler().postDelayed(object: Runnable{
            override fun run() {
                val intent = Intent(this@MainActivity, TargetActivity::class.java)
                overridePendingTransition(0, 0)
                startActivity(intent, null)
                overridePendingTransition(0, 0)
            }
        }, 2000)
    }
}
