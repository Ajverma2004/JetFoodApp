package com.ajverma.jetfoodapp.presentation.screens.auth.signup

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.presentation.screens.auth.BaseAuthViewModel
import com.ajverma.jetfoodapp.presentation.screens.auth.components.AlreadyHaveAnAccountText
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInOptionButton
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInTextWithLine
import com.ajverma.jetfoodapp.presentation.screens.navigation.AuthOption
import com.ajverma.jetfoodapp.presentation.screens.navigation.Home
import com.ajverma.jetfoodapp.presentation.screens.navigation.Login
import com.ajverma.jetfoodapp.presentation.utils.components.BasicDialog
import com.ajverma.jetfoodapp.presentation.utils.components.JetFoodTextField
import com.ajverma.jetfoodapp.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    val name = viewModel.name.collectAsStateWithLifecycle()
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>("") }
    var isLoading by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsState()
    when(uiState.value){
        is SignUpViewModel.SignupEvent.Error -> {
            isLoading = false
            errorMessage = (uiState.value as SignUpViewModel.SignupEvent.Error).message
        }
        is SignUpViewModel.SignupEvent.Loading -> {
            isLoading = true
            errorMessage = null
        }
        else -> {
            isLoading = false
            errorMessage = null
        }
    }
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when(event){
                is SignUpViewModel.SignupNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home){
                        popUpTo(AuthOption) {
                            inclusive = true
                        }
                    }
                }
                is SignUpViewModel.SignupNavigationEvent.NavigateToLogin -> {
                    navController.navigate(Login)
                }

                is SignUpViewModel.SignupNavigationEvent.ShowDialog -> {
                    showDialog = true
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // Ensure the background is solid white
    ) {
        Image(
            painter = painterResource(R.drawable.ic_auth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(), // Ensure the image fills the entire screen
            contentScale = ContentScale.Crop // Use Crop for full coverage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f)) // Push content down evenly

            // Title
            Text(
                text = stringResource(R.string.sign_up),
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(46.dp))

            // Full name text field
            JetFoodTextField(
                value = name.value,
                onValueChange = {
                    viewModel.onNameChange(it)
                },
                label = { Text(stringResource(R.string.full_name), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            // Email text field
            JetFoodTextField(
                value = email.value,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
                label = { Text(stringResource(R.string.email), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            // Password text field
            JetFoodTextField(
                value = password.value,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                },
                label = { Text(stringResource(R.string.password), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_eye),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(Modifier.height(26.dp))


            // Sign up button
            Button(
                onClick = viewModel::onSignUpClick,
                modifier = Modifier.size(250.dp, 70.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
            ) {
                Box{
                    AnimatedContent(
                        targetState = isLoading,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                        }, label = ""
                    ){ target ->
                        if (target) {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "SIGN UP",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // Sign-in options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Already have an account text
                AlreadyHaveAnAccountText(
                    trailingText = "Login",
                    initialTextColor = Color.Black,
                    trailingTextColor = Orange,
                    onClick = {
                        viewModel.onLoginClick()
                    }
                )

                Spacer(Modifier.height(26.dp))

                // Sign in with line
                SignInTextWithLine(
                    text = "Sign Up With",
                    textColor = Color.Black,
                    lineWidth = 80.dp
                )

                // Google and Facebook buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SignInOptionButton(
                        onClick = {
                            viewModel.onGoogleClick(context as ComponentActivity)
                        },
                        elevation = 7.dp,
                        image = R.drawable.ic_google,
                        text = R.string.google
                    )

                    SignInOptionButton(
                        onClick = {
                            viewModel.onFacebookClick(context as ComponentActivity)
                        },
                        elevation = 7.dp,
                        image = R.drawable.ic_facebook,
                        text = R.string.facebook
                    )
                }
            }
        }

        if (showDialog){
            ModalBottomSheet(
                onDismissRequest = { showDialog = false },
                sheetState = sheetState
            ) {
                BasicDialog(
                    title = viewModel.error,
                    description = errorMessage.toString(),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showDialog = false
                        }
                    }
                )
            }
        }
    }
}


