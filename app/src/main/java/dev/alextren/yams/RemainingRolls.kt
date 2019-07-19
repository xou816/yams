package dev.alextren.yams

interface RemainingRolls {
    fun decrement()
    fun restore()
    fun canRoll(): Boolean
}