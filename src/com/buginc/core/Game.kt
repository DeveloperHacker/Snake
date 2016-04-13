package com.buginc.core

import com.buginc.containers.ImmutableList
import com.buginc.math.Figure
import com.buginc.math.Section
import com.buginc.math.Vector
import java.awt.event.ActionListener
import java.util.*
import javax.swing.Timer

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Game {

    private val moveCycleTime = 20
    private val ableCycleTime = 20

    private val random = Random(20)

    var score: Int = 0
        private set

    var over: Boolean = false
        private set

    lateinit var snake: Snake
        private set

    lateinit var controller: Controller
        private set

    private lateinit var apples: MutableList<Apple>

    private var x: Int = 0
    private var y: Int = 0
    private var width: Int = 0
    private var height: Int = 0

    private lateinit var moveClock: Timer

    fun apples() = ImmutableList(apples)

    fun next(): Boolean {
        var over = false
        snake.move(snake.position + snake.speed + if (controller.fluctuation) snake.fluctuation(snake.clock.time) else Vector())
        snake.clock.next()
        val barb = Math.max(width, height)
        when {
            snake.position.y() < y ->
                snake.teleportation(Section(x - barb, y, x + width + barb, y), Vector(0, height))
            snake.position.x() < x ->
                snake.teleportation(Section(x, y - barb, x, y + height + barb), Vector(width, 0))
            snake.position.y() > y + height ->
                snake.teleportation(Section(x - barb, y + height, x + width + barb, y + height), Vector(0, -height))
            snake.position.x() > x + width ->
                snake.teleportation(Section(x + width, y - barb, x + width, y + height + barb), Vector(-width, 0))
        }
        var new = false
        val it = apples.iterator()
        while (it.hasNext()) {
            val apple = it.next()
            if (apple.isInside(snake.position)!!) {
                snake.saturate(apple.calories)
                score += apple.calories.toInt()
                if (apple.name == "Standard") new = true
                it.remove()
                continue
            }
            apple.rot()
            if (apple.calories == 0.0) it.remove()
        }
        if (new) {
            val mask = Figure(APPLE.maskStandardApple)
            mask.position = Vector.Companion.random(
                    x + mask.width(),
                    x + width - mask.width(),
                    y + mask.height(),
                    y + height - mask.height(),
                    random
            )
            apples.add(Apple("Standard", mask, APPLE.standardCalories, 0.0))
            if (random.nextInt(APPLE.chanceBigApple) == 0) {
                val bigMask = Figure(APPLE.maskBigApple)
                bigMask.position = Vector.Companion.random(
                        x + bigMask.width(),
                        x + width - bigMask.width(),
                        y + bigMask.height(),
                        y + height - bigMask.height(),
                        random
                )
                apples.add(Apple("Big", bigMask, APPLE.bigCalories, APPLE.losingCalories))
            }
        }
        var unallocatedLength = snake.speed.abs
        var prevPi: Pair<Vector, Vector>? = null
        val i = snake.gIterator()
        loop@while (i.hasNext() && unallocatedLength > 0)  {
            val pi = i.next()
            unallocatedLength -= (pi.first - pi.second).abs
            val section = Section(pi.first, pi.second)
            var prevPj: Pair<Vector, Vector>? = null
            val j = snake.gIterator()
            while (j.hasNext()) {
                val pj = j.next()
                if (pj == pi || pj == prevPi || prevPj == pi) {
                    prevPj = pj
                    continue
                }
                if (section.intersect(Section(pj.first, pj.second), eqError) != null) {
                    over = true
                    break@loop
                }
                prevPj = pj
            }
            prevPi = pi
        }
        return over
    }

    fun start(x: Int, y: Int, width: Int, height: Int): Game {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        score = 0
        snake = Snake(
                SNAKE.directSnake,
                SNAKE.startLocation,
                SNAKE.startLength,
                SNAKE.startSpeed,
                SNAKE.maxDeviation,
                eqError
        )
        controller = Controller(snake, false, false, true, ableCycleTime)
        apples = ArrayList()
        val mask = Figure(APPLE.maskStandardApple)
        mask.position = Vector.random(x + mask.width(), x + width - mask.width(), y + mask.height(), y + height - mask.height(), random)
        apples.add(Apple("Standard", mask, APPLE.standardCalories, 0.0))
        moveClock = Timer(moveCycleTime, ActionListener {
            if (!controller.gamePause) over = next()
            if (over) stop()
        })
        moveClock.start()
        controller.start()
        over = false
        return this
    }

    fun stop() {
        moveClock.stop()
        controller.stop()
    }

    companion object {

        const val eqError = 0.01

        object SNAKE {
            val startSpeed = Vector(0, 5)
            val directSnake = -startSpeed()
            val startLocation = Vector(750, 250)
            const val startLength = 200.0
            const val maxDeviation = Math.PI / 16.0
        }

        object APPLE {
            const val standardCalories = 10.0
            const val bigCalories = 50.0
            const val losingCalories = 0.4
            const val standardRadius = 10.0
            const val bigRadius = 50.0
            const val standardQVertexes = 10
            const val bigQVertexes = 50
            const val chanceBigApple = 10
            val maskStandardApple = Figure(
                    Vector(),
                    APPLE.standardQVertexes,
                    APPLE.standardRadius,
                    0.0,
                    eqError
            )
            val maskBigApple = Figure(
                    Vector(),
                    APPLE.bigQVertexes,
                    APPLE.bigRadius,
                    0.0,
                    eqError
            )
        }
    }
}
