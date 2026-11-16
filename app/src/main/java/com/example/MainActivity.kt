package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ads.AdManager
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.domain.model.PrizeClaim
import com.example.ui.gameplay.GameplayScreen
import com.example.ui.home.HomeScreen
import com.example.ui.legal.PrivacyPolicyScreen
import com.example.ui.legal.TermsConditionsScreen
import com.example.ui.map.LevelMapScreen
import com.example.ui.prize.ClaimSuccessScreen
import com.example.ui.prize.GrandFinalScreen
import com.example.ui.prize.PrizeClaimScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme

object AppDestinations {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val LEVEL_MAP = "level_map"
    const val GAMEPLAY = "gameplay/{levelNumber}"
    const val GRAND_FINAL_INTRO = "grand_final_intro"
    const val GRAND_FINAL_VICTORY = "grand_final_victory"
    const val PRIZE_CLAIM = "prize_claim"
    const val CLAIM_SUCCESS = "claim_success"
    const val SETTINGS = "settings"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS_CONDITIONS = "terms_conditions"

    fun gameplayRoute(levelNumber: Int) = "gameplay/$levelNumber"
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob and Audio
        AdManager.initialize(this)
        repository = GameRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                BallSortMasterApp(
                    repository = repository,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic()
        AdManager.showAppOpen(this)
    }

    override fun onPause() {
        super.onPause()
        SoundManager.stopBackgroundMusic()
    }
}

@Composable
fun BallSortMasterApp(
    repository: GameRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var submittedClaim: PrizeClaim? = remember { null }

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH,
        modifier = modifier
    ) {
        composable(AppDestinations.SPLASH) {
            SplashScreen(
                repository = repository,
                onSplashFinished = {
                    navController.navigate(AppDestinations.HOME) {
                        popUpTo(AppDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.HOME) {
            HomeScreen(
                repository = repository,
                onPlayLevel = { levelNum ->
                    if (levelNum == 1_000_000) {
                        navController.navigate(AppDestinations.GRAND_FINAL_INTRO)
                    } else {
                        navController.navigate(AppDestinations.gameplayRoute(levelNum))
                    }
                },
                onOpenLevelMap = {
                    navController.navigate(AppDestinations.LEVEL_MAP)
                },
                onOpenSettings = {
                    navController.navigate(AppDestinations.SETTINGS)
                },
                onOpenPrizeInfo = {
                    navController.navigate(AppDestinations.TERMS_CONDITIONS)
                }
            )
        }

        composable(AppDestinations.LEVEL_MAP) {
            LevelMapScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectLevel = { levelNum ->
                    if (levelNum == 1_000_000) {
                        navController.navigate(AppDestinations.GRAND_FINAL_INTRO)
                    } else {
                        navController.navigate(AppDestinations.gameplayRoute(levelNum))
                    }
                }
            )
        }

        composable(
            route = AppDestinations.GAMEPLAY,
            arguments = listOf(navArgument("levelNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("levelNumber") ?: 1
            GameplayScreen(
                levelNumber = level,
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNextLevel = { nextLevel ->
                    if (nextLevel == 1_000_000) {
                        navController.navigate(AppDestinations.GRAND_FINAL_INTRO) {
                            popUpTo(AppDestinations.GAMEPLAY) { inclusive = true }
                        }
                    } else {
                        navController.navigate(AppDestinations.gameplayRoute(nextLevel)) {
                            popUpTo(AppDestinations.GAMEPLAY) { inclusive = true }
                        }
                    }
                },
                onNavigateToGrandFinalVictory = {
                    navController.navigate(AppDestinations.GRAND_FINAL_VICTORY) {
                        popUpTo(AppDestinations.GAMEPLAY) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.GRAND_FINAL_INTRO) {
            GrandFinalScreen(
                isCompleted = false,
                onStartFinalLevel = {
                    navController.navigate(AppDestinations.gameplayRoute(1_000_000))
                },
                onClaimPrize = {
                    navController.navigate(AppDestinations.PRIZE_CLAIM)
                },
                onBackToHome = {
                    navController.popBackStack(AppDestinations.HOME, inclusive = false)
                }
            )
        }

        composable(AppDestinations.GRAND_FINAL_VICTORY) {
            GrandFinalScreen(
                isCompleted = true,
                onStartFinalLevel = {},
                onClaimPrize = {
                    navController.navigate(AppDestinations.PRIZE_CLAIM)
                },
                onBackToHome = {
                    navController.popBackStack(AppDestinations.HOME, inclusive = false)
                }
            )
        }

        composable(AppDestinations.PRIZE_CLAIM) {
            PrizeClaimScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onClaimSuccess = { claim ->
                    submittedClaim = claim
                    navController.navigate(AppDestinations.CLAIM_SUCCESS)
                },
                onViewTerms = {
                    navController.navigate(AppDestinations.TERMS_CONDITIONS)
                },
                onViewPrivacy = {
                    navController.navigate(AppDestinations.PRIVACY_POLICY)
                }
            )
        }

        composable(AppDestinations.CLAIM_SUCCESS) {
            val claim = submittedClaim ?: PrizeClaim(completedLevel = 1_000_000)
            ClaimSuccessScreen(
                claim = claim,
                onBackToHome = {
                    navController.popBackStack(AppDestinations.HOME, inclusive = false)
                }
            )
        }

        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onViewPrivacy = {
                    navController.navigate(AppDestinations.PRIVACY_POLICY)
                },
                onViewTerms = {
                    navController.navigate(AppDestinations.TERMS_CONDITIONS)
                }
            )
        }

        composable(AppDestinations.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.TERMS_CONDITIONS) {
            TermsConditionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
