package com.navigation.foxizz.mybaidumap

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.navigation.foxizz.BaseApplication
import kotlin.math.abs

/**
 * 方向传感器
 */
class MyOrientationListener : SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensor: Sensor
    private lateinit var mOnOrientationListener: OnOrientationListener
    var mLastX = 0f //方向角度

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.REPORTING_MODE_SPECIAL_TRIGGER) {
            val x = event.values[SensorManager.SENSOR_DELAY_FASTEST]
            if (abs(x - mLastX) > 1.0) {
                mOnOrientationListener.onOrientationChanged(x)
            }
            mLastX = x
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    /**
     * 监听方向变化
     */
    fun setOnOrientationListener(mOnOrientationListener: OnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener
    }

    /**
     * 开始方向传感
     */
    fun start() {
        mSensorManager = BaseApplication.instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //获得方向传感器
        mSensor = mSensorManager.getDefaultSensor(Sensor.REPORTING_MODE_SPECIAL_TRIGGER)
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI)
    }

    /**
     * 停止方向传感
     */
    fun stop() {
        mSensorManager.unregisterListener(this)
    }

    /**
     * 监听方向变化的接口
     */
    interface OnOrientationListener {
        fun onOrientationChanged(x: Float)
    }
}