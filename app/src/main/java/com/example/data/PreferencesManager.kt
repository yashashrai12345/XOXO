package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Player
import com.example.model.SoloStats
import com.example.model.TwoPlayerStats
import kotlin.math.max

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("orvix_xo_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "key_haptics_enabled"
        private const val KEY_DARK_THEME = "key_dark_theme"

        // Solo stats
        private const val KEY_SOLO_GAMES = "key_solo_games"
        private const val KEY_SOLO_WINS = "key_solo_wins"
        private const val KEY_SOLO_LOSSES = "key_solo_losses"
        private const val KEY_SOLO_DRAWS = "key_solo_draws"
        private const val KEY_SOLO_CURRENT_STREAK = "key_solo_current_streak"
        private const val KEY_SOLO_BEST_STREAK = "key_solo_best_streak"

        // 2-Player stats
        private const val KEY_2P_GAMES = "key_2p_games"
        private const val KEY_2P_WINS_X = "key_2p_wins_x"
        private const val KEY_2P_WINS_O = "key_2p_wins_o"
        private const val KEY_2P_DRAWS = "key_2p_draws"
    }

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    fun getSoloStats(): SoloStats {
        return SoloStats(
            gamesPlayed = prefs.getInt(KEY_SOLO_GAMES, 0),
            wins = prefs.getInt(KEY_SOLO_WINS, 0),
            losses = prefs.getInt(KEY_SOLO_LOSSES, 0),
            draws = prefs.getInt(KEY_SOLO_DRAWS, 0),
            currentStreak = prefs.getInt(KEY_SOLO_CURRENT_STREAK, 0),
            bestStreak = prefs.getInt(KEY_SOLO_BEST_STREAK, 0)
        )
    }

    fun recordSoloOutcome(winner: Player?): SoloStats {
        val current = getSoloStats()
        val isHumanWin = winner == Player.X
        val isLoss = winner == Player.O
        val isDraw = winner == null

        val newGames = current.gamesPlayed + 1
        val newWins = current.wins + (if (isHumanWin) 1 else 0)
        val newLosses = current.losses + (if (isLoss) 1 else 0)
        val newDraws = current.draws + (if (isDraw) 1 else 0)

        val newCurrentStreak = if (isHumanWin) current.currentStreak + 1 else if (isLoss) 0 else current.currentStreak
        val newBestStreak = max(current.bestStreak, newCurrentStreak)

        prefs.edit()
            .putInt(KEY_SOLO_GAMES, newGames)
            .putInt(KEY_SOLO_WINS, newWins)
            .putInt(KEY_SOLO_LOSSES, newLosses)
            .putInt(KEY_SOLO_DRAWS, newDraws)
            .putInt(KEY_SOLO_CURRENT_STREAK, newCurrentStreak)
            .putInt(KEY_SOLO_BEST_STREAK, newBestStreak)
            .apply()

        return SoloStats(newGames, newWins, newLosses, newDraws, newCurrentStreak, newBestStreak)
    }

    fun getTwoPlayerStats(): TwoPlayerStats {
        return TwoPlayerStats(
            gamesPlayed = prefs.getInt(KEY_2P_GAMES, 0),
            winsX = prefs.getInt(KEY_2P_WINS_X, 0),
            winsO = prefs.getInt(KEY_2P_WINS_O, 0),
            draws = prefs.getInt(KEY_2P_DRAWS, 0)
        )
    }

    fun recordTwoPlayerOutcome(winner: Player?): TwoPlayerStats {
        val current = getTwoPlayerStats()
        val newGames = current.gamesPlayed + 1
        val newWinsX = current.winsX + (if (winner == Player.X) 1 else 0)
        val newWinsO = current.winsO + (if (winner == Player.O) 1 else 0)
        val newDraws = current.draws + (if (winner == null) 1 else 0)

        prefs.edit()
            .putInt(KEY_2P_GAMES, newGames)
            .putInt(KEY_2P_WINS_X, newWinsX)
            .putInt(KEY_2P_WINS_O, newWinsO)
            .putInt(KEY_2P_DRAWS, newDraws)
            .apply()

        return TwoPlayerStats(newGames, newWinsX, newWinsO, newDraws)
    }

    fun resetAllStats() {
        prefs.edit()
            .remove(KEY_SOLO_GAMES)
            .remove(KEY_SOLO_WINS)
            .remove(KEY_SOLO_LOSSES)
            .remove(KEY_SOLO_DRAWS)
            .remove(KEY_SOLO_CURRENT_STREAK)
            .remove(KEY_SOLO_BEST_STREAK)
            .remove(KEY_2P_GAMES)
            .remove(KEY_2P_WINS_X)
            .remove(KEY_2P_WINS_O)
            .remove(KEY_2P_DRAWS)
            .apply()
    }
}
