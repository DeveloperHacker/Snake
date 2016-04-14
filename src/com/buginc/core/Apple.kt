package com.buginc.core

import com.buginc.math.Figure
import com.buginc.math.Vector

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Apple (val name: String, val startMask: Figure, val startCalories: Double, val loseCalories: Double) {

    var calories = startCalories
        private set

    val position: Vector
        get() = mask.position

    private var mask = startMask

    fun isInside(point: Vector) = mask.isInside(point)

    fun rot() {
        if (calories > 0.0 && loseCalories > 0.0) {
            calories -= Math.abs(loseCalories)
            if (calories < 0.0) calories = 0.0
            mask = startMask.scale(mask.position + mask.center(), calories / startCalories)
        }
    }

    fun getMask() = mask
}
