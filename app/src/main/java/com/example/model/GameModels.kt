package com.example.model

enum class Player {
    X,
    O;

    fun opponent(): Player = if (this == X) O else X
}

enum class GameMode {
    VS_AI,
    TWO_PLAYER
}

enum class BoardSize(
    val dimension: Int,
    val winLength: Int,
    val label: String,
    val subtitle: String,
    val winRuleText: String
) {
    THREE_BY_THREE(3, 3, "3×3", "Classic", "3 in a row"),
    SIX_BY_SIX(6, 4, "6×6", "Tactical", "4 in a row"),
    NINE_BY_NINE(9, 5, "9×9", "Strategy", "5 in a row"),
    ELEVEN_BY_ELEVEN(11, 5, "11×11", "Master", "5 in a row")
}

enum class MatchFormat(val targetWins: Int, val label: String) {
    SINGLE(1, "Single Game"),
    BEST_OF_THREE(2, "Best of 3")
}

data class CellPosition(val row: Int, val col: Int)

data class WinningLine(
    val cells: List<CellPosition>,
    val winner: Player
)

sealed class RoundResult {
    data class Win(val winner: Player, val line: WinningLine) : RoundResult()
    data object Draw : RoundResult()
}

data class MatchSeriesState(
    val winsX: Int = 0,
    val winsO: Int = 0,
    val targetWins: Int = 1,
    val seriesWinner: Player? = null
) {
    val isComplete: Boolean get() = seriesWinner != null
}

data class SessionScore(
    val winsX: Int = 0,
    val winsO: Int = 0,
    val draws: Int = 0
)

data class SoloStats(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
) {
    val winRate: Int
        get() = if (gamesPlayed > 0) (wins * 100) / gamesPlayed else 0
}

data class TwoPlayerStats(
    val gamesPlayed: Int = 0,
    val winsX: Int = 0,
    val winsO: Int = 0,
    val draws: Int = 0
)

enum class AppScreen {
    SPLASH,
    HOME,
    GAME,
    SETTINGS,
    HOW_TO_PLAY,
    STATS
}
