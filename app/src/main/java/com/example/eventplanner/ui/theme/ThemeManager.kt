package com.example.eventplanner.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.eventplanner.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ThemeManager {


    private val OliveGreenLight = EventPlannerTheme(
        primary = Color(0xFF9EC156),
        secondary = Color(0xFF676937),
        tertiary = Color(0xFFBFD07B),
        background = Color(0xFFF0F5E1),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        icon = R.drawable.icon_olive
    )

    private val OliveGreenDark = EventPlannerTheme(
        primary = Color(0xFF9EC156),
        secondary = Color(0xFF676937),
        tertiary = Color(0xFFBFD07B),
        background = Color(0xFF1A1A1A),
        surface = Color(0xFF2B2B2B),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        icon = R.drawable.icon_black
    )

    private val BlueLight = EventPlannerTheme(
        primary = Color(0xFF4A90E2),
        secondary = Color(0xFF357ABD),
        tertiary = Color(0xFF78B3F4),
        background = Color(0xFFEAF2FA),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        icon = R.drawable.icon_white
    )

    private val BlueDark = EventPlannerTheme(
        primary = Color(0xFF4A90E2),
        secondary = Color(0xFF357ABD),
        tertiary = Color(0xFF78B3F4),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        icon = R.drawable.icon_black
    )

    private val MintLight = EventPlannerTheme(
        primary = Color(0xFF4A90E2),
        secondary = Color(0xFF357ABD),
        tertiary = Color(0xFF78B3F4),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        icon = R.drawable.icon_white
    )

    private val MintDark = EventPlannerTheme(
        primary = Color(0xFF4A90E2),
        secondary = Color(0xFF357ABD),
        tertiary = Color(0xFF78B3F4),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        icon = R.drawable.icon_red
    )

    private val _selectedTheme = MutableStateFlow<EventPlannerTheme>(OliveGreenLight)
    val theme: StateFlow<EventPlannerTheme> = _selectedTheme.asStateFlow()

    fun changeTheme(theme: UITheme) {
        _selectedTheme.value = when (theme) {
            UITheme.OLIVE_LIGHT -> OliveGreenLight
            UITheme.OLIVE_DARK -> OliveGreenDark
            UITheme.BLUE_LIGHT -> BlueLight
            UITheme.BLUE_DARK -> BlueDark
            UITheme.MINT_LIGHT -> MintLight
            UITheme.MINT_DARK -> MintDark
            else -> MintLight
        }
    }
}