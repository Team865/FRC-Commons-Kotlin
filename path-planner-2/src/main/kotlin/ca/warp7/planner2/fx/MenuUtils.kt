@file:Suppress("unused")

package ca.warp7.planner2.fx

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCombination


inline fun menuBar(builder: MenuBar.() -> Unit): MenuBar = MenuBar().apply(builder)


fun MenuItem.name(name: String) {
    text = name
}


fun MenuItem.action(handler: (ActionEvent) -> Unit) {
    onAction = EventHandler(handler)
}


fun Menu.name(name: String) {
    text = name
}

fun menuItem(name: String, combo: KeyCombination, action: ()->Unit) {
    val mi = MenuItem(name)
    mi.accelerator = combo
    mi.setOnAction { action() }
}