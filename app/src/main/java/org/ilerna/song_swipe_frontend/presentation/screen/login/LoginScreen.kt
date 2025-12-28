package org.ilerna.song_swipe_frontend.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ilerna.song_swipe_frontend.R
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.presentation.components.AnimatedGradientBorder
import org.ilerna.song_swipe_frontend.presentation.components.LoadingIndicator
import org.ilerna.song_swipe_frontend.presentation.components.PrimaryButton
import org.ilerna.song_swipe_frontend.presentation.theme.ContentAlphaMedium
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
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
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
                    painter = painterResource(id = R.drawable.ss_logo_color),
                    contentDescription = "SongSwipe Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                //  UI STATE HANDLING
                when (authState) {

                    is AuthState.Idle -> {
                        Text(
                            text = "Swipe to discover new music!",
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        PrimaryButton(
                            text = "Continue with Spotify", onClick = onLoginClick
                        )
                    }

                    is AuthState.Loading -> {
                        LoadingIndicator(
                            fillMaxSize = false
                        )
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
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorScheme.background
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
                painter = painterResource(id = R.drawable.ss_logo_gray),
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
                    color = colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Spacing.spaceMd))

            // Error message
            Text(
                text = errorMessage.ifEmpty {
                    "We couldn't complete your login request. Please try again or contact support."
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = colorScheme.onBackground.copy(alpha = ContentAlphaMedium),
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(Spacing.spaceXl))

            // Use PrimaryButton component for consistency
            PrimaryButton(
                text = "Back to Login",
                onClick = onNavigateBack
            )
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
