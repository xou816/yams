package dev.alextren.yams

interface Score {
    val name: String
    var value: Int
    var locked: Boolean
}

typealias ScoreFactory = (String) -> Score