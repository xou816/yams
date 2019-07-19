package dev.alextren.yams

abstract class ScoreBoard(scoreFactory: ScoreFactory) {

    abstract fun setScores(scores: List<Score>)
    abstract fun setTotalScore(total: Int)

    val scorers = (1 until 7)
        .map { SimpleScoreObjective(it, scoreFactory) }
        .plus(PlusMoinsScoreObjective(scoreFactory))
        .plus(FullScoreObjective(scoreFactory))
        .plus(SuiteScoreObjective(scoreFactory))
        .plus(CarreScoreObjective(scoreFactory))
        .plus(YamsScoreObjective(scoreFactory))

    fun lockScore(score: Score) {
        score.locked = true
        setTotalScore(scorers
            .flatMap { it.getScores() }
            .filter { it.locked }
            .map { it.value }
            .sum())
    }

    fun scoresForDices(dices: DiceGroup) {
        val counts = dices.asCounts()
        setScores(scorers
            .onEach { it.updateScoresForCounts(counts) }
            .flatMap { it.getScores() }
            .sortedWith(compareBy({ it.locked }, { -it.value }))
        )
    }

    fun refreshScores() {
        setScores(scorers
            .onEach { it.updateScoresForCounts(emptyMap()) }
            .flatMap { it.getScores() }
            .sortedBy { it.locked })
    }

}