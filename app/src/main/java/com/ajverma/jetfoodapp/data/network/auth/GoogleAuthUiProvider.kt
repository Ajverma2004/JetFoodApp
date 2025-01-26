package com.ajverma.jetfoodapp.data.network.auth

import android.content.Context
import androidx.credentials.CredentialManager
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.ajverma.jetfoodapp.data.network.Constant
import com.ajverma.jetfoodapp.data.network.Constant.googleServiceClientId
import com.ajverma.jetfoodapp.data.network.models.authModels.GoogleAccount
import com.ajverma.jetfoodapp.domain.utils.Resource
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthUiProvider {
    suspend fun signIn(
        activityContext: Context,
        credentialManager: CredentialManager
    ): Resource<GoogleAccount>{
        return try {
            val creds = credentialManager.getCredential(
                context = activityContext,
                request = getCredentialRequest()
            ).credential

            Resource.Success(handleCredentials(creds))
        } catch (e: Exception) {
            Resource.Error("Sign-in failed: ${e.message}")
        }
    }

    private fun handleCredentials(creds: Credential): GoogleAccount {
        Log.d("GoogleAuthUiProvider", "Credentials received: $creds")
        if (creds is CustomCredential) {
            if (creds.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idToken = creds.data["idToken"]?.toString()
                val displayName = creds.data["displayName"]?.toString()
                val profileImageUrl = creds.data["profilePictureUri"]?.toString()

                Log.d("GoogleAuthUiProvider", "ID Token: $idToken")
                Log.d("GoogleAuthUiProvider", "Display Name: $displayName")
                Log.d("GoogleAuthUiProvider", "Profile Picture URL: $profileImageUrl")

                if (idToken != null) {
                    return GoogleAccount(
                        token = idToken,
                        displayName = displayName ?: "",
                        profileImageUrl = profileImageUrl ?: ""
                    )
                } else {
                    throw IllegalStateException("Google ID token is missing")
                }
            } else {
                throw IllegalStateException("Invalid credential type: Expected Google ID token credential")
            }
        } else {
            throw IllegalStateException("Invalid credential type")
        }
    }


    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption
                    .Builder(googleServiceClientId)
                    .build()
                )
            .build()
    }
}