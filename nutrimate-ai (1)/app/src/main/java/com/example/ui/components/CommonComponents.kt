package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthRating
import com.example.ui.theme.*
import com.example.util.AppLanguage

@Composable
fun LanguageSwitcherButton(
    currentLang: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = {
            val next = if (currentLang == AppLanguage.TELUGU) AppLanguage.ENGLISH else AppLanguage.TELUGU
            onLanguageChange(next)
        },
        modifier = modifier
            .height(38.dp)
            .testTag("language_switch_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = currentLang.flagEmoji,
                fontSize = 14.sp
            )
            Text(
                text = if (currentLang == AppLanguage.TELUGU) "తెలుగు (TE)" else "English (EN)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NonMedicalDisclaimerCard(
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("disclaimer_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (lang == AppLanguage.TELUGU)
                    "గమనిక: NutriMate AI సాధారణ పోషకాహార అవగాహన మరియు సమతుల్య జీవనశైలి కోసం రూపొందించబడింది. ఇది వైద్యపరమైన చికిత్స లేదా నిర్ధారణ కోసం కాదు."
                else
                    "Disclaimer: NutriMate AI is designed for lifestyle nutritional awareness and habit coaching. It is not intended for medical advice or diagnosis.",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HealthRatingBadge(
    rating: HealthRating,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val label = if (lang == AppLanguage.TELUGU) rating.labelTe else rating.labelEn
    val bgColor = Color(rating.colorHex)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, bgColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(bgColor)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = bgColor
            )
        }
    }
}

@Composable
fun MacroMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ThemeToggleButton(
    themeMode: AppThemeMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalIconButton(
        onClick = onToggle,
        modifier = modifier
            .size(38.dp)
            .testTag("theme_toggle_button"),
        shape = CircleShape,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        val icon = when (themeMode) {
            AppThemeMode.LIGHT -> Icons.Default.LightMode
            AppThemeMode.DARK -> Icons.Default.DarkMode
            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
        }
        Icon(
            imageVector = icon,
            contentDescription = "Toggle Theme",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ThemeSelectorRow(
    currentTheme: AppThemeMode,
    lang: AppLanguage,
    onThemeSelect: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppThemeMode.values().forEach { mode ->
            val isSelected = currentTheme == mode
            val label = if (lang == AppLanguage.TELUGU) mode.titleTe else mode.titleEn

            FilterChip(
                selected = isSelected,
                onClick = { onThemeSelect(mode) },
                leadingIcon = {
                    Text(text = mode.icon, fontSize = 14.sp)
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("theme_chip_${mode.name.lowercase()}")
            )
        }
    }
}
