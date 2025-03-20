package com.ajverma.jetfoodapp.presentation.screens.auth.authScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.presentation.screens.auth.components.AlreadyHaveAnAccountText
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInOptionButton
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInTextWithLine
import com.ajverma.jetfoodapp.presentation.screens.navigation.AuthOption
import com.ajverma.jetfoodapp.presentation.screens.navigation.Home
import com.ajverma.jetfoodapp.presentation.screens.navigation.Login
import com.ajverma.jetfoodapp.presentation.screens.navigation.SignUp
import com.ajverma.jetfoodapp.presentation.utils.components.BasicDialog
import com.ajverma.jetfoodapp.ui.theme.primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    isCostumer: Boolean = true,
    navController: NavController
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current

    var errorMessage by remember { mutableStateOf<String?>("") }
    var isLoading by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsState()
    when(uiState.value){
        is AuthViewModel.AuthEvent.Error -> {
            isLoading = false
            errorMessage = (uiState.value as AuthViewModel.AuthEvent.Error).message
        }
        is AuthViewModel.AuthEvent.Loading -> {
            isLoading = true
            errorMessage = null
        }
        else -> {
            isLoading = false
            errorMessage = null
        }
    }

    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when(event){
                is AuthViewModel.AuthNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home){
                        popUpTo(AuthOption) {
                            inclusive = true
                        }
                    }
                }

                is AuthViewModel.AuthNavigationEvent.NavigateToLogin -> {
                    navController.navigate(Login)
                }

                is AuthViewModel.AuthNavigationEvent.ShowDialog -> {
                    showDialog = true
                }
            }
        }
    }


    var imageSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
        startY = imageSize.height.toFloat() / 3
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    imageSize = it.size
                }
                .alpha(0.7f),
            contentScale = ContentScale.FillBounds
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(brush = brush))

        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(16.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, primary)
        ) {
            Text(
                stringResource(R.string.skip),
                color = primary,
                fontSize = 16.sp
            )
        }

        // welcome text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp)
            ,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.welcome),
                color = Color.Black,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp),
            )
            Text(
                stringResource(R.string.jetfood),
                color = primary,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp),
            )

            Text(
                stringResource(R.string.description),
                color = Color.DarkGray,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
            )
        }



        Box(
            modifier = Modifier.fillMaxSize()
        ){
           Column(
               modifier = Modifier.padding(horizontal = 10.dp)
                   .align(Alignment.BottomCenter)
           ) {

               // sign in options
               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(bottom = 20.dp)
                   ,
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Top
               ) {
                  if (isCostumer){
                      //sign in with line
                      SignInTextWithLine(
                          text = "Sign In With",
                          lineWidth = 80.dp
                      )


                      // google and facebook buttons
                      Row(
                          modifier = Modifier.fillMaxWidth()
                              .padding(top = 16.dp),
                          horizontalArrangement = Arrangement.SpaceEvenly,
                          verticalAlignment = Alignment.CenterVertically
                      ) {
                          //google button
                          SignInOptionButton(
                              onClick = {
                                  viewModel.onGoogleClick(context as ComponentActivity)
                              },
                              image = R.drawable.ic_google,
                              text = R.string.google
                          )

                          //facebook button
                          SignInOptionButton(
                              onClick = {
                                  viewModel.onFacebookClick(context as ComponentActivity)
                              },
                              image = R.drawable.ic_facebook,
                              text = R.string.facebook
                          )


                      }

                      //sign in with email or button

                      Button(
                          onClick = {
                              navController.navigate(SignUp)
                          },
                          colors = ButtonDefaults.buttonColors(
                              containerColor = Color.White.copy(alpha = 0.2f)
                          ),
                          border = BorderStroke(1.dp, Color.White),
                          modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                              .fillMaxWidth()
                          ,

                          ) {
                          Text(
                              "Start with email",
                              color = Color.White,
                              fontSize = 16.sp,
                              modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                          )
                      }
                  }


                   // already have an account text
                   AlreadyHaveAnAccountText(
                       trailingText = "Login",
                       textDecoration = TextDecoration.Underline,
                       onClick = {
                           navController.navigate(Login)
                       }
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
