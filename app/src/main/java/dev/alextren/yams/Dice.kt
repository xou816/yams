package dev.alextren.yams

interface Dice {
    fun rollDice(callback: () -> Unit)
    fun resetDice()
    fun isSelected(): Boolean
    fun toggleSelection()
    fun getValue(): Int
}