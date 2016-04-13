package com.buginc.core

import com.buginc.math.Section
import com.buginc.math.Vector
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Snake constructor(
        n: Vector,
        startLocation: Vector, startLen: Double, startSpeed: Vector,
        val maxDeviation: Double, val eqError: Double
) {

    var speed = startSpeed
        private set

    var minSpeed = startSpeed.abs
        set(value) {
            if (value < maxSpeed) field = value
            if (value > speed.abs) speed *= value / speed.abs
        }

    var maxSpeed = startSpeed.abs * 3.0
        set(value) {
            if (value > minSpeed) field = value
            if (value < speed.abs) speed *= value / speed.abs
        }

    var length = startLen
        private set

    val position: Vector
        get() = generatrix.first().first

    private var generatrix: MutableList<Pair<Vector, Vector>>

    val clock: Clock

    var range: Double = 0.8
        private set

    var dRange: Double = 0.1
        set(value) {
            if (value <= maxRange - minRange) field = value
        }

    var minRange: Double = 0.0
        set(value) {
            if (value < maxRange) field = value
            if (value > range) range = value
        }

    var maxRange: Double = range
        set(value) {
            if (value > minRange) field = value
            if (value < range) range = value
        }

    fun rangeUp() {
        range += dRange
        if (range > maxRange) range = maxRange
    }

    fun rangeDown() {
        range -= dRange
        if (range < minRange) range = minRange
    }

    var fluctuation: (Double) -> Vector = { time ->
        speed.rotate(Math.PI / 2)() * Math.sin(time) * (speed.abs * range)
    }

    private var unallocatedLength: Double = 0.0

    fun move(location: Vector) {
        if (location.equals(position)) return
        val newTail = LinkedList<Pair<Vector, Vector>>()
        val it = gIterator()
        var first = location
        var second = position
        unallocatedLength += length
        while (unallocatedLength > 0) {
            val allocatedLength: Double
            var newGen = second - first
            if (newGen.abs >= unallocatedLength - eqError) {
                newGen = newGen() * unallocatedLength
                newTail.add(Pair(first, first + newGen))
                unallocatedLength = 0.0
            } else {
                newTail.add(Pair(first, second))
                allocatedLength = newGen.abs
                unallocatedLength -= allocatedLength
                if (!it.hasNext()) break
                val pair = it.next()
                first = pair.first
                second = pair.second
            }
        }
        generatrix = newTail
    }

    fun cut(section: Section): Int? {
        val it = generatrix.iterator() as MutableListIterator<Pair<Vector, Vector>>
        var b: Int? = null
        while (it.hasNext()) {
            val cur = it.next()
            val O = section.intersect(Section(cur.first, cur.second), eqError) ?: continue
            it.remove()
            b = it.nextIndex()
            it.add(Pair(cur.first, O))
            it.add(Pair(O, cur.second))
            break
        }
        return b
    }

    fun moveSegment(a: Int, b: Int, direct: Vector): Boolean {
        val it = generatrix.iterator() as MutableListIterator<Pair<Vector, Vector>>
        var inSegment = false
        while (it.hasNext()) {
            val cur = it.next()
            if (it.nextIndex() - 1 == a) inSegment = true
            if (inSegment) {
                it.set(Pair(cur.first + direct, cur.second + direct))
            }
            if (it.nextIndex() - 1 == b) break
        }
        return inSegment
    }

    fun teleportation(teleport: Section, direct: Vector): Boolean {
        moveSegment(0, cut(teleport) ?: return false, direct)
        return true
    }

    fun saturate(calories: Double) {
        if (calories <= 0) throw IllegalArgumentException("Snake can not lose weight from eating.")
        val pair = generatrix.last()
//        val last = pair.first - pair.second
        unallocatedLength += (pair.first - pair.second).abs
//        generatrix[generatrix.lastIndex] = Pair(pair.first, pair.first + last() * (calories + last.abs))
        length += calories
    }

    fun turn(theta: Double) {
        speed = speed.rotate(if (Math.abs(theta) < maxDeviation) theta else maxDeviation * theta.compareTo(0))
    }

    fun turn(direction: Vector) = turn(Math.acos(speed.cos(direction)) * (speed * direction).compareTo(0))

    fun turn(direction: Direction) = turn(direction())

    fun set(value: Vector) {
        if (value.abs > maxSpeed) speed = speed() * maxSpeed
        else if (value.abs < minSpeed) speed = speed() * minSpeed
        else speed = value
    }

    fun gIterator() = generatrix.iterator()

    init {
        if (speed.abs < 0.0) throw IllegalArgumentException("Snake speed already >= 0")
        if (n.abs < 1.0) throw IllegalArgumentException("Direction is ill-conditioned")
        generatrix = LinkedList<Pair<Vector, Vector>>()
        generatrix.add(Pair(startLocation, startLocation + n() * length))
        clock = Clock(0.0, Math.PI * 2, 0.2)
    }
}