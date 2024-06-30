package org.quantumbadger.redreader.compose.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import org.quantumbadger.redreader.compose.prefs.ComposePrefs
import org.quantumbadger.redreader.compose.prefs.LocalComposePrefs
import org.quantumbadger.redreader.settings.types.AppearanceTheme
import org.quantumbadger.redreader.settings.types.ThemeLightness

interface ComposeThemePostCard {
	val title: TextStyle
	val subtitle: TextStyle
	val caption: TextStyle
	val iconColor: Color
	val backgroundColor: Color
	val listBackgroundColor: Color
	val previewImageBackgroundColor: Color
}

interface ComposeThemeAlbum {
	val toolbarIconColor: Color
	val title: TextStyle
	val subtitle: TextStyle
}

interface ComposeThemeDropdownMenu {
	val text: TextStyle
	val background: Color
}

interface ComposeThemeError {
	val title: TextStyle
	val message: TextStyle
	val border: Color
	val background: Color
	val detailsButton: ComposeThemeButton
}

@Immutable
data class ComposeThemeButton(
	val text: TextStyle,
	val background: Color,
	val shape: Shape,
	val border: Color? = null,
	val borderThickness: Dp? = null,
)

interface ComposeTheme {
	val dropdownMenu: ComposeThemeDropdownMenu
	val postCard: ComposeThemePostCard
	val album: ComposeThemeAlbum
	val error: ComposeThemeError
}

@Composable
fun RRComposeContextTheme(
	content: @Composable () -> Unit,
) {
	val prefs = LocalComposePrefs.current
	val themePref = prefs.appearanceTheme.value

	val theme = ComposeThemeImpl(prefs)

	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			WindowCompat.getInsetsController(window, view).apply {
				isAppearanceLightStatusBars = themePref.lightness == ThemeLightness.Light
			}
		}
	}

	MaterialTheme(
		colorScheme = when (themePref.lightness) {
			ThemeLightness.Light -> lightColorScheme(
				primary = themePref.colorPrimary,
				secondary = themePref.colorPrimaryDark,
			)

			ThemeLightness.Dark -> darkColorScheme(
				primary = themePref.colorPrimary,
				secondary = themePref.colorPrimaryDark,
			)
		}
	) {
		CompositionLocalProvider(LocalComposeTheme provides theme) {
			content()
		}
	}
}

class ComposeThemeImpl(prefs: ComposePrefs) : ComposeTheme {

	val theme = prefs.appearanceTheme.value
	val light = theme.lightness == ThemeLightness.Light

	val baseTextStyle = TextStyle()

	val colorText = if (light) {
		Colors.Grey.s10
	} else if (theme == AppearanceTheme.NIGHT_LOWCONTRAST) {
		Colors.Grey.s2
	} else {
		Colors.Grey.s1
	}

	val colorSubtext = if (light) {
		Colors.Grey.s7
	} else if (theme == AppearanceTheme.NIGHT_LOWCONTRAST) {
		Colors.Grey.s5
	} else {
		Colors.Grey.s3
	}

	val colorIcon = if (light) {
		Colors.Grey.s7
	} else if (theme == AppearanceTheme.NIGHT_LOWCONTRAST) {
		Colors.Grey.s6
	} else {
		Colors.Grey.s4
	}

	val colorCardBackground = if (light) {
		Color.White
	} else if (theme == AppearanceTheme.ULTRABLACK) {
		Color.Black
	} else {
		Colors.Grey.s9
	}

	val colorListBackground = if (light) {
		Colors.Grey.s1
	} else if (theme == AppearanceTheme.NIGHT_LOWCONTRAST) {
		Colors.Grey.s10
	} else {
		Color.Black
	}

	val colorPopupBackground = if (light) {
		Color.White
	} else if (theme == AppearanceTheme.NIGHT_LOWCONTRAST) {
		Colors.Grey.s7
	} else {
		Colors.Grey.s8
	}

	val colorImageBackground = if (light) {
		Colors.Grey.s2
	} else {
		Colors.Grey.s8
	}

	override val postCard = object : ComposeThemePostCard {

		override val title = baseTextStyle.copy(
			color = colorText,
			fontWeight = FontWeight.W600,
			fontSize = 18.sp * prefs.appearanceFontScalePosts
		)

		override val subtitle = baseTextStyle.copy(
			color = colorSubtext,
			fontWeight = FontWeight.W400,
			fontSize = 14.sp * prefs.appearanceFontScalePostSubtitles
		)

		override val caption = baseTextStyle.copy(
			color = colorText,
			fontWeight = FontWeight.W500,
			fontSize = 16.sp * prefs.appearanceFontScalePostSubtitles
		)

		override val iconColor = colorIcon
		override val backgroundColor = colorCardBackground
		override val listBackgroundColor = colorListBackground
		override val previewImageBackgroundColor = colorImageBackground
	}

	override val album = object : ComposeThemeAlbum {
		override val title = baseTextStyle.copy(
			color = colorText,
			fontWeight = FontWeight.W600,
			fontSize = 22.sp * prefs.appearanceFontScalePosts // TODO different setting?
		)

		override val subtitle = baseTextStyle.copy(
			color = colorSubtext,
			fontWeight = FontWeight.W400,
			fontSize = 16.sp * prefs.appearanceFontScalePostSubtitles
		)

		override val toolbarIconColor = colorIcon
	}

	override val dropdownMenu = object : ComposeThemeDropdownMenu {
		override val text = baseTextStyle.copy(
			color = colorText,
			fontWeight = FontWeight.W500,
			fontSize = 16.sp * prefs.appearanceFontScaleGlobal // TODO different setting
		)
		override val background = colorPopupBackground
	}

	override val error = object : ComposeThemeError {
		override val title = baseTextStyle.copy(
			color = colorText,
			fontWeight = FontWeight.W600,
			fontSize = 16.sp
		)

		override val message = baseTextStyle.copy(
			color = colorSubtext,
			fontWeight = FontWeight.W400,
			fontSize = 13.sp
		)

		override val background = if (light) {
			Colors.Red.s0
		} else {
			Colors.Red.s10
		}

		override val border = if (light) {
			Colors.Red.s4
		} else {
			Colors.Red.s7
		}

		override val detailsButton = ComposeThemeButton(
			text = baseTextStyle.copy(
				color = Color.White,
				fontWeight = FontWeight.W500,
				fontSize = 13.sp
			),
			background = if (light) {
				Colors.Red.s5
			} else {
				Colors.Red.s7
			},
			shape = RoundedCornerShape(6.dp)
		)
	}
}

val LocalComposeTheme =
	staticCompositionLocalOf<ComposeTheme> { throw RuntimeException("Theme not initialized") }

object Colors {
	object Grey {
		val s1 = Color(0xFFF8F9FA)
		val s2 = Color(0xFFE9ECEF)
		val s3 = Color(0xFFDEE2E6)
		val s4 = Color(0xFFCED4DA)
		val s5 = Color(0xFFADB5BD)
		val s6 = Color(0xFF6C757D)
		val s7 = Color(0xFF495057)
		val s8 = Color(0xFF343A40)
		val s9 = Color(0xFF212529)
		val s10 = Color(0xFF111213)
	}

	object Red {
		val s0 = Color(0xFFfdf6f6)
		val s1 = Color(0xFFf6d8d8)
		val s2 = Color(0xFFeeb2b2)
		val s3 = Color(0xFFe68c8c)
		val s4 = Color(0xFFde6565)
		val s5 = Color(0xFFd63f3f)
		val mid = Color(0xFFd32f2f)
		val s6 = Color(0xFFbf2828)
		val s7 = Color(0xFF992020)
		val s8 = Color(0xFF721818)
		val s9 = Color(0xFF4c1010)
		val s10 = Color(0xFF260808)
	}
}
