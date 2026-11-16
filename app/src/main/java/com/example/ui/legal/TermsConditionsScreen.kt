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
fun TermsConditionsScreen(
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
                modifier = Modifier.testTag("terms_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Terms & Conditions",
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
                        text = "Grand Prize Challenge Terms & Eligibility",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. Promotion Overview\n" +
                                "Ball Sort Master offers a Grand Prize Challenge for players who legitimately reach and solve all 1,000,000 deterministic puzzle levels.\n\n" +
                                "2. Eligibility & Verification\n" +
                                "To qualify for the bumper prize:\n" +
                                "• The player must legitimately solve Level 1,000,000 in accordance with standard puzzle rules.\n" +
                                "• Completion must be verifiable through cryptographic game state validation.\n" +
                                "• Players must submit accurate contact and address details.\n" +
                                "• Only one claim per device / verified UID is permitted.\n\n" +
                                "3. Fraud Prevention & Disqualification\n" +
                                "Any use of modified APKs, memory editors, unauthorized automated bots, or fraudulent data submission will result in immediate disqualification and voiding of the claim.\n\n" +
                                "4. Review Period\n" +
                                "Submitted claims are marked with status 'pending' and subject to review within 30 to 60 business days.\n\n" +
                                "5. Governing Law\n" +
                                "This promotional challenge is void where prohibited by local legislation.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
