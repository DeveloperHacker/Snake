package com.buginc.core

import com.buginc.containers.ImmutableList
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

    private val random = Random()

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
                if (apple.name == "standard") new = true
                it.remove()
                continue
            }
            apple.rot()
            if (apple.calories == 0.0) it.remove()
        }
        if (new) {
            apples.add(standardApple(x, y, width, height, random))
            if (random.nextInt(chanceBigApple) == 0) apples.add(bigApple(x, y, width, height, random))
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
        snake = snake()
        apples = ArrayList()
        apples.add(standardApple(x, y, width, height, random))
        controller = Controller(snake, false, false, true, ableCycleTime)
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
}
