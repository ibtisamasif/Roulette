package com.thedesignerx.roulette

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import com.thedesignerx.roulette.PredictorActivity

@Deprecated("")
class FloatingViewService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        //Add the view to the window.
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
        val sp = getSharedPreferences("roulette", MODE_PRIVATE)
        val paramx = sp.getInt("paramx", 0)
        val paramsy = sp.getInt("paramy", 100)
        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
        params.x = paramx
        params.y = paramsy

        //Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mFloatingView, params)

        //Drag and move floating view using user's touch action.
        mFloatingView?.findViewById<View>(R.id.root_container)?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val XDiff = (event.rawX - initialTouchX).toInt()
                        val YDiff = (event.rawY - initialTouchY).toInt()

                        //The check for XDiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (XDiff < 10 && YDiff < 10) {
                            if (isViewCollapsed) {
                                val intent = Intent(this@FloatingViewService, PredictorActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                                //close the service and remove view from the view hierarchy
                                stopSelf()
                            }
                        }
                        val displayMetrics = DisplayMetrics()
                        mWindowManager!!.defaultDisplay.getMetrics(displayMetrics)
                        val height = displayMetrics.heightPixels
                        if (params.y > height * 0.8) {
                            mFloatingView?.setVisibility(View.GONE)
                            stopSelf()
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //store values in persistant storage
                        val sp = getSharedPreferences("roulette", MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putInt("paramx", params.x)
                        editor.putInt("paramy", params.y)
                        editor.commit()

                        //Update the layout with new X & Y coordinate
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private val isViewCollapsed: Boolean
        private get() = mFloatingView == null || mFloatingView!!.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager!!.removeView(mFloatingView)
    }
}