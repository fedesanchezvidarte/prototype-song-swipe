package org.ilerna.song_swipe_frontend.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ilerna.song_swipe_frontend.R
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.presentation.components.AnimatedGradientBorder
import org.ilerna.song_swipe_frontend.presentation.components.PrimaryButton
import org.ilerna.song_swipe_frontend.presentation.theme.NeonGradientColors
import org.ilerna.song_swipe_frontend.presentation.theme.Sizes
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing


/**
 * Main Login Screen (UI Layer)
 * - Displays different UI based on AuthState
 * - Hides the logo entirely when an error occurs (full-screen error UI)
 */
@Composable
fun LoginScreen(
    authState: AuthState,
    onLoginClick: () -> Unit,
    onResetState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated neon border around the whole screen
        AnimatedGradientBorder(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp),
            strokeWidth = Sizes.borderStrokeWidth,
            cornerRadius = Sizes.borderCornerRadius
        )

        // If error → show only the full-screen error UI
        if (authState is AuthState.Error) {
            LoginScreenError(
                errorMessage = authState.message, onNavigateBack = onResetState
            )
        } else {
            // Normal login UI (logo + states)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Logo shown only when NOT in error state
                Image(
                    painter = painterResource(id = R.drawable.songswipe_logo),
                    contentDescription = "SongSwipe Logo",
                    modifier = Modifier.size(170.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                //  UI STATE HANDLING
                when (authState) {

                    is AuthState.Idle -> {
                        Text(
                            text = "Swipe to discover new music!",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        PrimaryButton(
                            text = "Continue with Spotify", onClick = onLoginClick
                        )
                    }

                    is AuthState.Loading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    // Success state is handled by MainActivity navigation
                    else -> Unit
                }
            }
        }
    }
}


/**
 * Composable that displays an error after a failed LOGIN attempt.
 */
@Composable
private fun LoginScreenError(
    errorMessage: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vibrantGradient = Brush.horizontalGradient(colors = NeonGradientColors)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // PNG Image
            Image(
                painter = painterResource(id = R.drawable.audio_waves),
                contentDescription = "Error Indicator",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.spaceXl))

            // Title
            Text(
                text = "Oh! Something went wrong...",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Spacing.spaceMd))

            // Error message
            Text(
                text = if (errorMessage.isNotEmpty()) {
                    errorMessage
                } else {
                    "We couldn't complete your login request. Please try again or contact support."
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(Spacing.spaceXl))

            // "Back to Login" button with gradient background
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Sizes.buttonHeight)
                    .background(vibrantGradient, MaterialTheme.shapes.extraLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = Spacing.spaceLg,
                    vertical = Spacing.spaceSm
                )
            ) {
                Text(
                    text = "Back to Login",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/* PREVIEWS */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginIdle() {
    SongSwipeTheme {
        LoginScreen(
            authState = AuthState.Idle, onLoginClick = {}, onResetState = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginLoading() {
    SongSwipeTheme {
        LoginScreen(
            authState = AuthState.Loading, onLoginClick = {}, onResetState = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginError() {
    SongSwipeTheme {
        LoginScreen(
            authState = AuthState.Error("Login failed"), onLoginClick = {}, onResetState = {})
    }
}
