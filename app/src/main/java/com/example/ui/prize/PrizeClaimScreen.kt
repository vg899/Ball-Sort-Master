package com.example.ui.prize

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.domain.model.PrizeClaim
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun PrizeClaimScreen(
    repository: GameRepository,
    onNavigateBack: () -> Unit,
    onClaimSuccess: (PrizeClaim) -> Unit,
    onViewTerms: () -> Unit,
    onViewPrivacy: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var isConfirmed by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var existingClaim by remember { mutableStateOf<PrizeClaim?>(null) }
    var isCheckingExisting by remember { mutableStateOf(true) }

    BackHandler {
        onNavigateBack()
    }

    // Check if user already submitted a claim
    LaunchedEffect(Unit) {
        val claim = repository.getExistingPrizeClaim()
        existingClaim = claim
        isCheckingExisting = false
    }

    // Validation rules
    val isEmailValid = email.contains("@") && email.contains(".") && email.length >= 6
    val isPhoneValid = mobileNumber.filter { it.isDigit() }.length in 7..15
    val isPinValid = pinCode.filter { it.isDigit() }.length in 4..10
    val isFormValid = fullName.isNotBlank() &&
            isEmailValid &&
            isPhoneValid &&
            fullAddress.isNotBlank() &&
            isPinValid &&
            isConfirmed

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
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
                    modifier = Modifier.testTag("prize_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Grand Prize Claim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
            }

            if (isCheckingExisting) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentGold)
                }
            } else if (existingClaim != null) {
                // User already has an existing claim: show status
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Existing Claim",
                        tint = AccentEmerald,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Claim Already Submitted",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PuzzleSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Status: ${existingClaim!!.status.uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Submitted for: ${existingClaim!!.fullName}",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Email: ${existingClaim!!.email}",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("BACK TO HOME", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Claim Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PuzzleSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Congratulations on completing Level 1,000,000! Fill out the required details below. All claims are securely recorded and subject to eligibility verification.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        isError = fullName.isBlank() && fullName.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = email.isNotEmpty() && !isEmailValid,
                        supportingText = {
                            if (email.isNotEmpty() && !isEmailValid) {
                                Text("Enter a valid email address", color = AccentRose)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_email_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mobile Number
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = mobileNumber.isNotEmpty() && !isPhoneValid,
                        supportingText = {
                            if (mobileNumber.isNotEmpty() && !isPhoneValid) {
                                Text("Enter a valid phone number", color = AccentRose)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_mobile_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Full Address
                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        label = { Text("Full Address *") },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_address_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PIN Code
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it },
                        label = { Text("PIN / Postal Code *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_pincode_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirmation Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isConfirmed,
                            onCheckedChange = { isConfirmed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentGold,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("claim_confirm_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I confirm that the information provided by me is correct.",
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage!!,
                            color = AccentRose,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (isFormValid && !isSubmitting) {
                                SoundManager.playButtonClick()
                                isSubmitting = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val result = repository.submitPrizeClaim(
                                        fullName = fullName,
                                        email = email,
                                        mobileNumber = mobileNumber,
                                        fullAddress = fullAddress,
                                        pinCode = pinCode
                                    )
                                    isSubmitting = false
                                    result.onSuccess { claim ->
                                        SoundManager.playCoinSound()
                                        onClaimSuccess(claim)
                                    }.onFailure { e ->
                                        errorMessage = "We couldn't submit your claim: ${e.message ?: "Please check internet connection and try again."}"
                                    }
                                }
                            }
                        },
                        enabled = isFormValid && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold,
                            disabledContainerColor = AccentGold.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_claim_button")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF451A03),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "SUBMIT CLAIM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFormValid) Color(0xFF451A03) else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Links to Terms and Privacy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onViewTerms) {
                            Text("Terms & Conditions", fontSize = 12.sp, color = AccentGold)
                        }
                        Text(" • ", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.align(Alignment.CenterVertically))
                        TextButton(onClick = onViewPrivacy) {
                            Text("Privacy Policy", fontSize = 12.sp, color = AccentGold)
                        }
                    }
                }
            }
        }
    }
}
