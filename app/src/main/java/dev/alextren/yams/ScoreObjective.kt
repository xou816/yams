package dev.alextren.yams

interface ScoreObjective {
    fun getScores(): List<Score>
    fun updateScoresForCounts(counts: Map<Int, Int>)
    fun Score.ifNotLocked(consumer: (Score) -> Unit) {
        if (!locked) run(consumer)
    }

    companion object {
        fun getNGroup(counts: Map<Int, Int>, n: Int) = counts.entries
            .firstOrNull { (_, count) -> count == n }
            ?.key?.let { it * n }
    }
}

class PlusMoinsScoreObjective(scoreFactory: ScoreFactory) : ScoreObjective {

    private val scores = Pair(scoreFactory("Moins"), scoreFactory("Plus"))

    override fun getScores() = scores.toList()
    override fun updateScoresForCounts(counts: Map<Int, Int>) {
        val (m, p) = scores
        val score = counts.map { it.value * it.key }.sum()
        p.ifNotLocked {
            p.value = if (score >= m.value) score else 0
        }
        m.ifNotLocked {
            m.value = if (score <= p.value) score else 0
        }
    }

}

class CarreScoreObjective(scoreFactory: ScoreFactory) : ScoreObjective {

    private val score = scoreFactory("Carré")

    override fun getScores() = listOf(score)
    override fun updateScoresForCounts(counts: Map<Int, Int>) = score.ifNotLocked {
        it.value = ScoreObjective.getNGroup(counts, 4)
            ?.let { it + 50 }
            ?: 0
    }
}

class YamsScoreObjective(scoreFactory: ScoreFactory) : ScoreObjective {

    private val score = scoreFactory("Yams!")

    override fun getScores() = listOf(score)
    override fun updateScoresForCounts(counts: Map<Int, Int>) = score.ifNotLocked {
        it.value = ScoreObjective.getNGroup(counts, 5)
            ?.let { it + 60 }
            ?: 0
    }
}

class SimpleScoreObjective(private val number: Int, scoreFactory: ScoreFactory) : ScoreObjective {

    private val score = scoreFactory("${number}s")

    override fun getScores() = listOf(score)
    override fun updateScoresForCounts(counts: Map<Int, Int>) = score.ifNotLocked {
        it.value = counts[number]?.times(number) ?: 0
    }
}

class FullScoreObjective(scoreFactory: ScoreFactory) : ScoreObjective {

    private val score = scoreFactory("Full")

    override fun getScores() = listOf(score)
    override fun updateScoresForCounts(counts: Map<Int, Int>) = score.ifNotLocked {
        val trio = ScoreObjective.getNGroup(counts, 3) ?: 0
        val duo = ScoreObjective.getNGroup(counts, 2) ?: 0
        it.value = if (trio > 0 && duo > 0) duo + trio + 10 else 0
    }
}

class SuiteScoreObjective(scoreFactory: ScoreFactory) : ScoreObjective {

    private val score = scoreFactory("Suite")

    override fun getScores() = listOf(score)
    override fun updateScoresForCounts(counts: Map<Int, Int>) = score.ifNotLocked {
        it.value = when {
            (1 until 6).all { counts[it] == 1 } -> 15
            (2 until 7).all { counts[it] == 1 } -> 20
            else -> 0
        }
    }
}