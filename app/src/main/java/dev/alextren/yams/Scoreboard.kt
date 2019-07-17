package dev.alextren.yams

class Scoreboard(scoreFactory: ScoreFactory) {

    val scorers = (1 until 7)
        .map { SimpleScoreObjective(it, scoreFactory) }
        .plus(PlusMoinsScoreObjective(scoreFactory))
        .plus(FullScoreObjective(scoreFactory))
        .plus(SuiteScoreObjective(scoreFactory))
        .plus(CarreScoreObjective(scoreFactory))
        .plus(YamsScoreObjective(scoreFactory))

    fun lockScore(score: Score): Int {
        score.locked = true
        return scorers
            .flatMap { it.getScores() }
            .filter { it.locked }
            .map { it.value }
            .sum()
    }

    fun updatedScores(dices: DiceGroup): List<Score> {
        val counts = dices.asCounts()
        return scorers
            .onEach { it.updateScoresForCounts(counts) }
            .flatMap { it.getScores() }
            .sortedWith(compareBy({ it.locked }, { -it.value }))
    }

    fun getScores(): List<Score> {
        return scorers.flatMap { it.getScores() }
    }

}