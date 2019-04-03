package com.example.compositor_view_hack

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.transition.Transition
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout

class TargetActivity : AppCompatActivity() {
    private var splashView: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.e("ABCD", "created")
        var layoutView = LayoutView(this)
        var surfaceView = DrawingSurfaceView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(500, 300).apply { bottomMargin = 100 }
        }
        setContentView(layoutView)
        layoutView.addView(surfaceView)

        splashView = FrameLayout(this)
        splashView?.setBackgroundColor(Color.GREEN)
        layoutView.addView(splashView)

        Handler().postDelayed(object: Runnable{
            override fun run() {
                try {
                    TargetActivity::class.java.getMethod("convertFromTranslucent").invoke(this@TargetActivity)
                } catch (e:Exception) {
                    e.printStackTrace();
                }
                layoutView.setTransparentRegionRequestsEnabled(true)
                layoutView.requestTransparentRegion(surfaceView)
                layoutView.removeView(splashView)
            }
        }, 10000)
    }

    internal inner class LayoutView : FrameLayout {
        var enableTransparentRegionRequests = false

        constructor(context: Context) : super(context) {
        }

        override fun requestTransparentRegion(child: View?) {
            if (enableTransparentRegionRequests)  {
                super.requestTransparentRegion(child)
            }
        }

        fun setTransparentRegionRequestsEnabled(enabled: Boolean) {
            enableTransparentRegionRequests = enabled
        }
    }

    internal inner class DrawingSurfaceView : SurfaceView {

        constructor(context: Context) : this(context, null)
        constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
            holder.addCallback(object : SurfaceHolder.Callback2 {

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    draw()
                }

                override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
                    draw()
                }

                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                    draw();
                }

            })
        }

        private var path: Path? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.BLUE
        }

        private val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.rgb(-0, 0, 255)
        }


        override fun onTouchEvent(event: MotionEvent): Boolean {
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    path = Path().also { it.moveTo(event.x, event.y) }
                }
                event.action == MotionEvent.ACTION_MOVE -> path?.lineTo(event.x, event.y)
                event.action == MotionEvent.ACTION_UP -> path?.lineTo(event.x, event.y)
            }

            draw()
            return true
        }

        fun draw(color: Int = Color.rgb(100, 100, 255)) {
            fillPaint.color = color
            val canvas = holder.lockCanvas()
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fillPaint)
            path?.let { canvas?.drawPath(it, paint) }
            holder.unlockCanvasAndPost(canvas)
        }


    }
}
