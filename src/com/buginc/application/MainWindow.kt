package com.buginc.application

import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class MainWindow (title: String, x: Int, y: Int, width: Int, height: Int) : JFrame(title) {

    init {
        val panel = GamePanel()
        setBounds(x, y, width, height)
        isResizable = false
        contentPane = panel
        contentPane.isFocusable = true
        isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE
        panel.start()
    }
}

fun main(args: Array<String>) = SwingUtilities.invokeLater { MainWindow("Snake", 50, 50, 16 * 70, 9 * 70) }