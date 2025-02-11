package com.example.myapplication

import android.car.Car
import android.car.hardware.property.CarPropertyManager
import android.car.hardware.property.CarPropertyValue
import android.content.Context
import com.example.myapplication.view.TripActivity


class CarSpeedService(context: Context?)  {
    private val car: Car? = Car.createCar(context)
    private val carPropertyManager: CarPropertyManager =
        car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    private val VEHICLE_SPEED = 0x11600207 // Property ID for speed
    private var mSpeedLimit = 60;
    private var mContext: Context? = context;

    fun startListening() {
        carPropertyManager.registerCallback(
            carPropertyEventCallback,
            VEHICLE_SPEED,
            CarPropertyManager.SENSOR_RATE_NORMAL
        )
    }

    fun setSpeedLimit(speedLimit :Int) {
        mSpeedLimit = speedLimit
    }

    private val carPropertyEventCallback: CarPropertyManager.CarPropertyEventCallback =
        object : CarPropertyEventCallback() {
            fun onChangeEvent(carPropertyValue: CarPropertyValue<*>) {
                if (carPropertyValue.getPropertyId() === VEHICLE_SPEED) {
                    val speed = carPropertyValue.getValue() as Float
                    val speedKmh = speed * 3.6f

                    if (speedKmh > mSpeedLimit) {
                        // callAPI
                        (mContext as TripActivity).notifyOverSpeed(speedKmh.toInt())
                    }

                    println("Current speed: $speed m/s")
                }
            }

            fun onErrorEvent(propertyId: Int, areaId: Int) {
                System.err.println("Error reading property: $propertyId")
            }
        }

    fun stopListening() {
        carPropertyManager.unregisterCallback(carPropertyEventCallback)
        if (car != null) {
            car.disconnect()
        }
    }
}
}
}