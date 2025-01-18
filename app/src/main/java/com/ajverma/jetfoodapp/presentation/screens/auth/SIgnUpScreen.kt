package com.ajverma.jetfoodapp.presentation.screens.auth

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.presentation.screens.auth.components.AlreadyHaveAnAccountText
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInOptionButton
import com.ajverma.jetfoodapp.presentation.screens.auth.components.SignInTextWithLine
import com.ajverma.jetfoodapp.presentation.utils.components.JetFoodTextField
import com.ajverma.jetfoodapp.ui.theme.Orange

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
            .background(Color.White)
    ){
        Image(
            painter = painterResource(R.drawable.ic_auth),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f))

            //title
            Text(
                stringResource(R.string.sign_up),
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(Modifier.height(46.dp))

            // Full name text field
            JetFoodTextField(
                value = "",
                onValueChange = {},
                label = { Text(
                    stringResource(R.string.full_name),
                    color = Color.Gray
                ) },
                modifier = Modifier.fillMaxWidth()
            )


            // email text field
            JetFoodTextField(
                value = "",
                onValueChange = {},
                label = { Text(
                    stringResource(R.string.email),
                    color = Color.Gray
                ) },
                modifier = Modifier.fillMaxWidth()
            )




            //password text field
            JetFoodTextField(
                value = "",
                onValueChange = {},
                label = { Text(
                    stringResource(R.string.password),
                    color = Color.Gray
                ) },
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

            Button(
                modifier = Modifier
                    .size(250.dp, 70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp
                ),
                onClick = {}
            ) {
                Text(
                    "SIGN UP" ,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            
            Spacer(Modifier.height(36.dp))

            //sign in buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // already have an account text
                AlreadyHaveAnAccountText(
                    trailingText = "Login",
                    initialTextColor = Color.Black,
                    trailingTextColor = Orange,
                    onClick = {}
                )

                Spacer(Modifier.height(26.dp))

                //sign in with line
                SignInTextWithLine(
                    text = "Sign In With",
                    textColor = Color.Black,
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
                        onClick = {},
                        elevation = 7.dp,
                        image = R.drawable.ic_google,
                        text = R.string.google
                    )

                    //facebook button
                    SignInOptionButton(
                        onClick = {},
                        elevation = 7.dp,
                        image = R.drawable.ic_facebook,
                        text = R.string.facebook
                    )
                }
            }

        }
    }

}


@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen()
}