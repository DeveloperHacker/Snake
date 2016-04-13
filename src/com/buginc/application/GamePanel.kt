package com.buginc.application

import com.buginc.containers.ImmutableList
import com.buginc.core.Apple
import com.buginc.core.Game
import com.buginc.core.Snake
import com.buginc.math.Vector
import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import java.util.*
import javax.swing.*
import javax.swing.Timer

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class GamePanel() : JPanel() {

    val repaintCycleTime = 10

    private lateinit var game: Game

    fun start() {
        game = Game().start(x, y, width, height)
        addKeyListener(object : KeyListener {
            override fun keyTyped(event: KeyEvent) = Unit

            override fun keyPressed(event: KeyEvent) {
                game.controller.keyPressed(event)
                if (event.keyCode == KeyEvent.VK_ENTER && game.over) start()
            }

            override fun keyReleased(event: KeyEvent) {
                game.controller.keyReleased(event)
            }

        })
        val paintClock = Timer(repaintCycleTime, ActionListener { repaint() })
        paintClock.start()
    }

    override fun paint(graphics: Graphics) {
        val snake = game.snake
        val apples = game.apples()
        graphics.color = Color.WHITE
        graphics.fillRect(x, y, width, height)
        printSnakeGeneratrix(game.snake, graphics)
        printApples(game.apples(), graphics)
        printScore(game.score, graphics)
        if (game.controller.gameInfo) printInfo(snake, apples, graphics)
        if (game.over) printGameOver(graphics)
        if (game.controller.gamePause && !game.over) printPause(graphics)
    }

    fun drawPoint(pos: Vector, radius: Int, graphics: Graphics) {
        val x = pos.x() - radius
        val y = pos.y() - radius
        graphics.drawOval(x, y, radius * 2, radius * 2)
    }

    fun fillPoint(pos: Vector, radius: Int, graphics: Graphics) {
        val x = pos.x() - radius
        val y = pos.y() - radius
        graphics.fillOval(x, y, radius * 2, radius * 2)
    }

    fun printSnakeGeneratrix(snake: Snake, graphics: Graphics) {
        val _it = snake.gIterator()
        val generatrixColor = Color.BLUE
        val headColor = Color.RED
        val boneColor = Color.BLACK
        graphics.color = boneColor
        fillPoint(snake.position, 3, graphics)
        graphics.color = headColor
        drawPoint(snake.position, 1, graphics)
        drawPoint(snake.position, 2, graphics)
        graphics.color = boneColor
        drawPoint(snake.position, 3, graphics)
        while (_it.hasNext()) {
            val pair = _it.next()
            graphics.color = generatrixColor
            graphics.drawLine(pair.first.x(), pair.first.y(), pair.second.x(), pair.second.y())
            graphics.color = boneColor
            drawPoint(pair.first, 2, graphics)
        }
    }

    fun printApples(apples: ImmutableList<Apple>, graphics: Graphics) {
        apples.forEach { apple ->
            var prev = apple.getMask().position
            var current: Vector
            val points = ArrayList<Vector>()
            for (vec in apple.getMask().outline) {
                points.add(prev)
                current = prev + vec
                prev = current
            }
            val xs = IntArray(points.size)
            val ys = IntArray(points.size)
            var point: Vector
            for (i in points.indices) {
                point = points[i]
                xs[i] = point.x.toInt()
                ys[i] = point.y.toInt()
            }
            graphics.color = Color.ORANGE
            graphics.fillPolygon(xs, ys, points.size)
            graphics.color = Color.RED
            graphics.drawPolygon(xs, ys, points.size)
        }
    }

    fun printInfo(snake: Snake, apples: ImmutableList<Apple>, graphics: Graphics) {
        graphics.color = Color.DARK_GRAY
        val dLine = graphics.fontMetrics.height + 3
        val tab = 10
        var column = 1
        var line = 3
        graphics.drawString("Snake:", x + column * tab, y + ++line * dLine)
        ++column
        graphics.drawString("Speed: ${Vector.round(snake.speed.abs, 2)}", x + column * tab, y + ++line * dLine)
        graphics.drawString("Length: ${Vector.round(snake.length, 2)}", x + column * tab, y + ++line * dLine)
        graphics.drawString("Location:", x + column * tab, y + ++line * dLine)
        graphics.drawString("x = ${Vector.round(snake.position.x, 2)}", x + (column + 7) * tab, y + line * dLine)
        graphics.drawString("y = ${Vector.round(snake.position.y, 2)}", x + (column + 14) * tab, y + line * dLine)
        graphics.drawString("Range: ${Vector.round(snake.range, 2)}", x + column * tab, y + ++line * dLine)
        graphics.drawString("Frequency: ${Vector.round(snake.clock.dTime, 2)}", x + column * tab, y + ++line * dLine)
        --column
        ++line
        graphics.drawString("Apples:", x + column * tab, y + ++line * dLine)
        ++column
        apples.forEach { apple ->
            graphics.drawString("Apple:", x + column * tab, y + ++line * dLine)
            ++column
            graphics.drawString("Calories: ${Vector.round(apple.calories, 2)}", x + column * tab, y + ++line * dLine)
            graphics.drawString("Location:", x + column * tab, y + ++line * dLine)
            graphics.drawString("x = ${Vector.round(apple.position.x, 2)}", x + (column + 7) * tab, y + line * dLine)
            graphics.drawString("y = ${Vector.round(apple.position.y, 2)}", x + (column + 14) * tab, y + line * dLine)
            --column
        }
    }

    fun printGameOver(graphics: Graphics) {
        graphics.color = Color.BLACK
        val dLine = graphics.fontMetrics.height + 3
        var line = 0
        var str = "GAME OVER"
        graphics.drawString(str, x + width / 2 - 30, y + height / 2 - 1 + line * dLine)
        str = "Press \"Enter\" to begin the game again"
        graphics.drawString(str, x + width / 2 - 100, y + height / 2 - 1 + ++line * dLine)
    }

    fun printPause(graphics: Graphics) {
        graphics.color = Color.BLACK
        val str = "PAUSE"
        graphics.drawString(str, x + width / 2 - 30, y + height / 2)
    }

    fun printScore(score: Int, graphics: Graphics) {
        val dLine = graphics.fontMetrics.height + 3
        val tab = 10
        var column = 1
        var line = 1
        graphics.color = Color.BLACK
        graphics.drawString("Score: $score", x + column * tab, y + line * dLine)
    }


}