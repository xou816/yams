package dev.alextren.testapp

interface Dice {
    var value: Int
    var selected: Boolean
    fun rollDice(callback: () -> Unit)
}