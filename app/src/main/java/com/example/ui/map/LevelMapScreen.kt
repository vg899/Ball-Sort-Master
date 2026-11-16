package com.example.ui.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.BannerAdView
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurface
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class MapChapter(
    val id: Int,
    val name: String,
    val startLevel: Int,
    val endLevel: Int,
    val icon: String
)

val MAP_CHAPTERS = listOf(
    MapChapter(1, "Beginner Springs", 1, 100, "🌱"),
    MapChapter(2, "Crystal Caverns", 101, 1_000, "💎"),
    MapChapter(3, "Emerald Valley", 1_001, 10_000, "🌲"),
    MapChapter(4, "Sapphire Heights", 10_001, 50_000, "🏔️"),
    MapChapter(5, "Amethyst Peaks", 50_001, 100_000, "🔮"),
    MapChapter(6, "Ruby Citadel", 100_001, 250_000, "🏰"),
    MapChapter(7, "Obsidian Depths", 250_001, 500_000, "🌋"),
    MapChapter(8, "Celestial Spire", 500_001, 750_000, "✨"),
    MapChapter(9, "Mythic Stratum", 750_001, 999_999, "🌌"),
    MapChapter(10, "The Grand Sanctuary", 1_000_000, 1_000_000, "👑")
)

val MILESTONE_LEVELS = setOf(
    1, 10, 25, 50, 100, 500, 1_000, 5_000, 10_000,
    25_000, 50_000, 100_000, 250_000, 500_000, 750_000,
    900_000, 999_999, 1_000_000
)

@Composable
fun LevelMapScreen(
    repository: GameRepository,
    onNavigateBack: () -> Unit,
    onSelectLevel: (Int) -> Unit
) {
    val progress by repository.progress.collectAsState()
    val highestCompleted = progress.highestCompletedLevel
    val currentLevel = progress.currentLevel

    // Find which chapter corresponds to current level
    val initialChapterIndex = MAP_CHAPTERS.indexOfFirst {
        currentLevel in it.startLevel..it.endLevel
    }.coerceAtLeast(0)

    var selectedChapterIndex by remember { mutableIntStateOf(initialChapterIndex) }
    val currentChapter = MAP_CHAPTERS[selectedChapterIndex]

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        onNavigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("map_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LEVEL MAP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Progress: Level $highestCompleted / 1,000,000",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Coins
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${progress.totalCoins}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }
            }

            // Chapter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedChapterIndex,
                containerColor = PuzzleSurface,
                contentColor = PrimaryLight,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                MAP_CHAPTERS.forEachIndexed { index, chapter ->
                    val isChapterUnlocked = highestCompleted + 1 >= chapter.startLevel
                    Tab(
                        selected = selectedChapterIndex == index,
                        onClick = {
                            SoundManager.playButtonClick()
                            selectedChapterIndex = index
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = chapter.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = chapter.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedChapterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isChapterUnlocked) TextPrimary else TextSecondary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    )
                }
            }

            // Virtualized Levels in Selected Chapter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Calculate display range for this chapter
                // If chapter has <= 100 levels, show all. If large, show around current progress or sample windows + milestone checkpoints
                val levelsInChapter = remember(selectedChapterIndex, currentLevel) {
                    generateLevelsForChapter(currentChapter, currentLevel, highestCompleted)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Milestone Chapter Header Card
                    item {
                        ChapterInfoCard(currentChapter, highestCompleted)
                    }

                    items(levelsInChapter, key = { it }) { levelNum ->
                        val isCompleted = levelNum <= highestCompleted
                        val isCurrent = levelNum == currentLevel
                        val isLocked = levelNum > highestCompleted + 1
                        val isMilestone = levelNum in MILESTONE_LEVELS

                        LevelMapNode(
                            levelNumber = levelNum,
                            isCompleted = isCompleted,
                            isCurrent = isCurrent,
                            isLocked = isLocked,
                            isMilestone = isMilestone,
                            onClick = {
                                if (!isLocked) {
                                    SoundManager.playButtonClick()
                                    onSelectLevel(levelNum)
                                }
                            }
                        )
                    }
                }

                // Jump to Current Level FAB
                FloatingActionButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        selectedChapterIndex = MAP_CHAPTERS.indexOfFirst {
                            currentLevel in it.startLevel..it.endLevel
                        }.coerceAtLeast(0)
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = PrimaryIndigo,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 60.dp, end = 16.dp)
                        .testTag("jump_to_current_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Jump to current level"
                    )
                }
            }

            // Bottom Banner Ad
            BannerAdView(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ChapterInfoCard(chapter: MapChapter, highestCompleted: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PuzzleSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = chapter.icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = chapter.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Levels ${chapter.startLevel} – ${chapter.endLevel}",
                    fontSize = 12.sp,
                    color = AccentGold
                )
            }
        }
    }
}

@Composable
private fun LevelMapNode(
    levelNumber: Int,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    isMilestone: Boolean,
    onClick: () -> Unit
) {
    val bgModifier = when {
        isCurrent -> Modifier.background(
            Brush.linearGradient(listOf(PrimaryIndigo, Color(0xFF818CF8)))
        )
        isCompleted -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
        )
        isMilestone -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFFB45309), AccentGold))
        )
        else -> Modifier.background(PuzzleSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked, onClick = onClick)
            .testTag("level_node_$levelNumber")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(bgModifier)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Icon circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isCompleted -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            isCurrent -> Text(
                                text = "▶",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            isLocked -> Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            else -> Text(
                                text = "•",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Level $levelNumber",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocked) TextSecondary else Color.White
                            )
                            if (isMilestone) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (levelNumber == 1_000_000) "🏆 GRAND FINAL" else "⭐ MILESTONE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentGold
                                )
                            }
                        }

                        Text(
                            text = when {
                                isCurrent -> "CURRENT LEVEL"
                                isCompleted -> "Completed"
                                isLocked -> "Locked"
                                else -> "Available"
                            },
                            fontSize = 12.sp,
                            color = if (isCurrent) AccentGold else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                if (isCompleted) {
                    Row {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Procedurally generates visible levels for a chapter without loading 1M objects.
 */
private fun generateLevelsForChapter(chapter: MapChapter, currentLevel: Int, highestCompleted: Int): List<Int> {
    val totalInChapter = chapter.endLevel - chapter.startLevel + 1

    if (totalInChapter <= 100) {
        return (chapter.startLevel..chapter.endLevel).toList()
    }

    // For large chapters: show window around current level within chapter + milestones
    val result = linkedSetOf<Int>()

    // Always include chapter start
    result.add(chapter.startLevel)

    // Add milestones inside chapter
    for (m in MILESTONE_LEVELS) {
        if (m in chapter.startLevel..chapter.endLevel) {
            result.add(m)
        }
    }

    // Add a window around currentLevel if inside this chapter
    if (currentLevel in chapter.startLevel..chapter.endLevel) {
        val windowStart = (currentLevel - 15).coerceAtLeast(chapter.startLevel)
        val windowEnd = (currentLevel + 25).coerceAtMost(chapter.endLevel)
        for (i in windowStart..windowEnd) {
            result.add(i)
        }
    } else {
        // Show first 30 levels of the chapter
        for (i in chapter.startLevel..(chapter.startLevel + 30).coerceAtMost(chapter.endLevel)) {
            result.add(i)
        }
    }

    // Always include chapter end
    result.add(chapter.endLevel)

    return result.sorted()
}
