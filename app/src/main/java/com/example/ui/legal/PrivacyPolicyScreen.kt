package com.example.ui.legal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    SoundManager.playButtonClick()
                    onNavigateBack()
                },
                modifier = Modifier.testTag("privacy_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Privacy Policy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PuzzleSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Ball Sort Master Privacy Policy",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. Information We Collect\n" +
                                "Ball Sort Master utilizes Firebase Anonymous Authentication. No personal identifiers (such as name or email) are required to play normal puzzle levels.\n\n" +
                                "2. Grand Prize Claim Information\n" +
                                "If you complete Level 1,000,000 and submit a Grand Prize Claim, you voluntarily provide your Full Name, Email, Mobile Number, Address, and PIN code. This information is strictly used for prize eligibility verification and shipping fulfillment.\n\n" +
                                "3. Data Security & Storage\n" +
                                "Data is stored securely via Google Firebase Realtime Database. Personal contact information is restricted and never made public.\n\n" +
                                "4. Third-Party Services & Advertising\n" +
                                "This application integrates Google Mobile Ads (AdMob) to display advertisements. AdMob may collect device-level identifiers in compliance with Google Play Developer Policies.\n\n" +
                                "5. Contact & Questions\n" +
                                "For any privacy-related questions or data deletion requests, contact support@ballsortmaster.com.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
