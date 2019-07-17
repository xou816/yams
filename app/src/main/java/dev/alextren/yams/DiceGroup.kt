package dev.alextren.yams

class DiceGroup(val dices: List<Dice> = emptyList()) {

    fun rollDices(onAnimatedEnd: (List<Dice>) -> Unit = {}) {
        val filteredDices = dices.filter { !it.isSelected() }
        val finalCallback = { onAnimatedEnd(filteredDices) }
        val chainedAnimation = filteredDices.foldRight(finalCallback) { dice, callback ->
            { dice.rollDice(callback) }
        }
        chainedAnimation()
    }

    fun asCounts() = dices.fold(mutableMapOf<Int, Int>()) { map, dice ->
        map[dice.getValue()] = map[dice.getValue()]?.plus(1) ?: 1
        map
    }

    fun deselectAll() {
        dices.forEach { it.resetDice() }
    }

    fun hasBeenThrown() = dices.all { it.getValue() > 0 }

}