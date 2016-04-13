package com.buginc.core

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Clock constructor(startTime: Double, private val maxTime: Double, startDTime: Double) {

    var time = startTime
        private set

    var dTime: Double = startDTime
        set(value) {
            if (value > 0.0 && value < maxTime) field = value
        }

    fun next() {
        if (time < maxTime) time += dTime
        else time = 0.0
    }
}
