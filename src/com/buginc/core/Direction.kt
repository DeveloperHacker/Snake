package com.buginc.core

import com.buginc.math.Vector

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

enum class Direction private constructor(val direct: Vector) {
    NORTH(Vector(0, -1)),
    SOUTH(Vector(0, 1)),
    WEST(Vector(-1, 0)),
    EAST(Vector(1, 0)),
    NORTHEAST(NORTH() + EAST()),
    NORTHWEST(NORTH() + WEST()),
    SOUTHEAST(SOUTH() + EAST()),
    SOUTHWEST(SOUTH() + WEST());

    operator fun invoke() = direct
}
