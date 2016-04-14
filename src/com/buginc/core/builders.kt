package com.buginc.core

import com.buginc.math.Figure
import com.buginc.math.Vector
import java.util.*

const val chanceBigApple = 10
const val eqError = 0.01

fun standardApple(x: Int, y: Int, width: Int, height: Int, random: Random) = Apple(
        name = "standard",
        startMask = Figure(
                position = Vector.random(x + 20.0, x + width - 20.0, y + 20.0, y + height - 20.0, random),
                qVertices = 20,
                radius = 10.0,
                theta = 0.0,
                eqError = eqError
        ),
        startCalories = 10.0,
        loseCalories = 0.0
)

fun bigApple(x: Int, y: Int, width: Int, height: Int, random: Random) = Apple(
        name = "big",
        startMask = Figure(
                position = Vector.random(x + 50.0, x + width - 50.0, y + 50.0, y + height - 50.0, random),
                qVertices = 50,
                radius = 50.0,
                theta = 0.0,
                eqError = eqError
        ),
        startCalories = 50.0,
        loseCalories = 0.4
)

fun snake() = Snake(
        n = Vector(0, -1),
        startLocation = Vector(750, 250),
        startLen = 200.0,
        startSpeed = Vector(0, 5),
        maxDeviation = Math.PI / 16.0,
        eqError = eqError
)

