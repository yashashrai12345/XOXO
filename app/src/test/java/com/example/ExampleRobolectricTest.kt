package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PreferencesManager
import com.example.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Orvix XO", appName)
    }

    @Test
    fun `preferences manager records solo game outcomes accurately`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)
        prefs.resetAllStats()

        val stats1 = prefs.recordSoloOutcome(Player.X)
        assertEquals(1, stats1.gamesPlayed)
        assertEquals(1, stats1.wins)
        assertEquals(1, stats1.currentStreak)

        val stats2 = prefs.recordSoloOutcome(Player.O)
        assertEquals(2, stats2.gamesPlayed)
        assertEquals(1, stats2.losses)
        assertEquals(0, stats2.currentStreak)
        assertEquals(1, stats2.bestStreak)
    }
}
