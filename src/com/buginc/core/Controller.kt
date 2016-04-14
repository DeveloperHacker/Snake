package com.buginc.core

import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import javax.swing.Timer

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Controller (
        val snake: Snake,
        var gamePause: Boolean,
        var gameInfo: Boolean,
        var fluctuation: Boolean,
        val ableCycleTime: Int
) : KeyListener {

    private val pressedKeys = HashMap<Int, Boolean>()
    private val clickedKeys = HashMap<Int, Boolean>()

    private val pressedClock = Timer(ableCycleTime, ActionListener {
        pressedKeys.forEach { keyCode, pressed -> if (pressed) pressKey(keyCode) }
    })

    override fun keyTyped(event: KeyEvent) = Unit

    override fun keyReleased(event: KeyEvent) {
        pressedKeys[event.keyCode] = false
        clickedKeys[event.keyCode] = false
    }

    override fun keyPressed(event: KeyEvent) {
        if (pressedKeys[event.keyCode] != true) pressKey(event.keyCode)
        if (clickedKeys[event.keyCode] != true) clickKey(event.keyCode)
    }

    fun pressKey(keyCode: Int) {
        var pressed = true
        when (keyCode) {
            KeyEvent.VK_NUMPAD1 -> snake.turn(Direction.SOUTHWEST)
            KeyEvent.VK_NUMPAD2 -> snake.turn(Direction.SOUTH)
            KeyEvent.VK_NUMPAD3 -> snake.turn(Direction.SOUTHEAST)
            KeyEvent.VK_NUMPAD4 -> snake.turn(Direction.WEST)
            KeyEvent.VK_NUMPAD6 -> snake.turn(Direction.EAST)
            KeyEvent.VK_NUMPAD7 -> snake.turn(Direction.NORTHWEST)
            KeyEvent.VK_NUMPAD8 -> snake.turn(Direction.NORTH)
            KeyEvent.VK_NUMPAD9 -> snake.turn(Direction.NORTHEAST)
            KeyEvent.VK_LEFT -> snake.turn(-Math.PI)
            KeyEvent.VK_RIGHT -> snake.turn(Math.PI)
            KeyEvent.VK_A -> snake.turn(-Math.PI)
            KeyEvent.VK_D -> snake.turn(Math.PI)
            else -> pressed = false
        }
        if (pressed) pressedKeys[keyCode] = true
    }

    fun clickKey(keyCode: Int) {
        if (keyCode == KeyEvent.VK_DOWN) snake.set(snake.speed() * (snake.speed.abs - snake.minSpeed * 0.5))
        if (keyCode == KeyEvent.VK_UP) snake.set(snake.speed() * (snake.speed.abs + snake.minSpeed * 0.5))
        if (keyCode == KeyEvent.VK_S) snake.set(snake.speed() * (snake.speed.abs - snake.minSpeed * 0.5))
        if (keyCode == KeyEvent.VK_W) snake.set(snake.speed() * (snake.speed.abs + snake.minSpeed * 0.5))
        if (keyCode == KeyEvent.VK_F) fluctuation = !fluctuation
        if (keyCode == KeyEvent.VK_R) snake.rangeUp()
        if (keyCode == KeyEvent.VK_E) snake.rangeDown()
        if (keyCode == KeyEvent.VK_Y) snake.clock.dTime += 0.05
        if (keyCode == KeyEvent.VK_T) snake.clock.dTime -= 0.05
        if (keyCode == KeyEvent.VK_SPACE) gamePause = !gamePause
        if (keyCode == KeyEvent.VK_I) gameInfo = !gameInfo
        clickedKeys[keyCode] = true
    }

    fun start() = pressedClock.start()

    fun stop() = pressedClock.stop()
}