package com.example.checkcalendar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkcalendar.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(
            "check_calendar_prefs",
            Context.MODE_PRIVATE
        )

        setContent {
            var isDarkTheme by remember {
                mutableStateOf(
                    prefs.getBoolean("dark_mode", false)
                )
            }

            var showSplash by remember { mutableStateOf(true) }

            CheckCalendarTheme(darkTheme = isDarkTheme) {
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                } else {
                    CheckCalendarScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { enabled ->
                            isDarkTheme = enabled
                            prefs.edit()
                                .putBoolean("dark_mode", enabled)
                                .apply()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CheckCalendarScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val today = LocalDate.now()

    val prefs = remember {
        context.getSharedPreferences(
            "check_calendar_prefs",
            Context.MODE_PRIVATE
        )
    }

    // Restore checked dates from local storage
    var checkedDates by remember {
        mutableStateOf(
            prefs.getStringSet(
                "checked_dates",
                emptySet<String>()
            )?.mapNotNull { savedDate ->
                runCatching {
                    LocalDate.parse(savedDate)
                }.getOrNull()
            }?.toSet() ?: emptySet()
        )
    }

    var currentMonth by remember {
        mutableStateOf(YearMonth.from(today))
    }

    val daysInMonth = currentMonth.lengthOfMonth()

    // Sunday-first calendar
    val firstDayOffset =
        currentMonth.atDay(1).dayOfWeek.value % 7

    val monthName = currentMonth.month.getDisplayName(
        TextStyle.FULL,
        Locale.getDefault()
    ).replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else {
            it.toString()
        }
    }

    val checkedThisMonth = checkedDates.count {
        YearMonth.from(it) == currentMonth
    }

    val progress = checkedThisMonth.toFloat() / daysInMonth

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val mainText = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    val todayBackground =
        if (isDarkTheme) DarkTodayBackground else TodayGreen

    val todayText =
        if (isDarkTheme) DarkTodayText else Green

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {

        // Header and theme toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "D A I L Y",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Aptick calendar",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = mainText
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isDarkTheme) "☾" else "☀",
                    fontSize = 22.sp,
                    color = mainText
                )

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Small steps. Consistent progress.",
            fontSize = 14.sp,
            color = secondaryText
        )

        Spacer(Modifier.height(24.dp))

        // Monthly progress card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY PROGRESS",
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryText
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "$checkedThisMonth of $daysInMonth days",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = mainText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .background(
                                color = if (isDarkTheme) {
                                    DarkTodayBackground
                                } else {
                                    LightGreen
                                },
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = when {
                        checkedThisMonth == 0 ->
                            "Start building your streak today."
                        checkedThisMonth == daysInMonth ->
                            "Amazing! Every day is checked."
                        else ->
                            "Every check is a step forward."
                    },
                    fontSize = 13.sp,
                    color = secondaryText
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Calendar card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentMonth = currentMonth.minusMonths(1)
                        }
                    ) {
                        Text(
                            text = "‹",
                            fontSize = 32.sp,
                            color = mainText
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = monthName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = mainText
                        )

                        Text(
                            text = currentMonth.year.toString(),
                            fontSize = 13.sp,
                            color = secondaryText
                        )
                    }

                    IconButton(
                        onClick = {
                            currentMonth = currentMonth.plusMonths(1)
                        }
                    ) {
                        Text(
                            text = "›",
                            fontSize = 32.sp,
                            color = mainText
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Weekday headings
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "S", "M", "T", "W", "T", "F", "S"
                    ).forEach { day ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = secondaryText
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Square calendar cells
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            (
                                    ((firstDayOffset + daysInMonth + 6) / 7) * 44 +
                                            (((firstDayOffset + daysInMonth + 6) / 7) - 1) * 5
                                    ).dp
                        ),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(firstDayOffset) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }

                    items(daysInMonth) { index ->
                        val date = currentMonth.atDay(index + 1)
                        val isChecked = date in checkedDates
                        val isToday = date == today

                        val cellBackground = when {
                            isChecked ->
                                MaterialTheme.colorScheme.primary
                            isToday ->
                                todayBackground
                            isDarkTheme ->
                                DarkCell
                            else ->
                                LightCell
                        }

                        val cellTextColor = when {
                            isChecked ->
                                MaterialTheme.colorScheme.onPrimary
                            isToday ->
                                todayText
                            else ->
                                mainText
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(
                                    color = cellBackground,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .then(
                                    if (isToday) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    val updatedDates =
                                        if (isChecked) {
                                            checkedDates - date
                                        } else {
                                            checkedDates + date
                                        }

                                    checkedDates = updatedDates

                                    prefs.edit()
                                        .putStringSet(
                                            "checked_dates",
                                            updatedDates
                                                .map { it.toString() }
                                                .toSet()
                                        )
                                        .apply()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isChecked) {
                                    "✓"
                                } else {
                                    date.dayOfMonth.toString()
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isToday || isChecked) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                },
                                color = cellTextColor
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(3.dp)
                                )
                        )

                        Text(
                            text = "Completed",
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    todayBackground,
                                    RoundedCornerShape(3.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(3.dp)
                                )
                        )

                        Text(
                            text = "Today",
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Return to the current month
        OutlinedButton(
            onClick = {
                currentMonth = YearMonth.from(today)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Go to Today",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}