package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }

    val titles = listOf(
        "NIVA",
        "Life gets messy.\nNIVA turns it into a plan.",
        "You stay in control.\nNIVA only remembers what you allow."
    )

    val subtitles = listOf(
        "Tell NIVA. It gets handled.",
        "Unclutter your daily mental load — from unexpected guests to tonight’s cooking, family chores, and money.",
        "Transparent, private, and non-judgmental. No fake actions, no complicated questionnaires."
    )

    val emojis = listOf("✨", "🧠", "🛡️")

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Header Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (step == index) 32.dp else 12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (step == index) SageGreenPrimary else SageContainer
                            )
                    )
                }
            }

            // Main Content
            AnimatedContent(
                targetState = step,
                label = "onboarding_step"
            ) { currentStep ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(SageContainer.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emojis[currentStep],
                            fontSize = 50.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = titles[currentStep],
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = subtitles[currentStep],
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = CharcoalMedium,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (step < 2) {
                    Button(
                        onClick = { step++ },
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_next_button")
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp
                        )
                    }

                    TextButton(
                        onClick = onGetStarted,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Skip to Home",
                            color = CharcoalMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Button(
                        onClick = onGetStarted,
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_get_started_button")
                    ) {
                        Text(
                            text = "GET STARTED",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
