package com.ajverma.jetfoodapp.data.network.auth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.ajverma.jetfoodapp.data.network.Constant.googleServiceClientId
import com.ajverma.jetfoodapp.data.network.models.authModels.GoogleAccount
import com.ajverma.jetfoodapp.domain.utils.Resource
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthUiProvider {

    suspend fun signIn(
        activityContext: Context,
        credentialManager: CredentialManager
    ): Resource<GoogleAccount> {
        Log.d("GoogleAuthUiProvider", "Starting Google Sign-In process...")
        return try {
            // Log before fetching credentials
            Log.d("GoogleAuthUiProvider", "Preparing to fetch credentials with CredentialManager")

            // Fetch credentials
            val result = credentialManager.getCredential(
                context = activityContext,
                request = getCredentialRequest()
            )
            Log.d("GoogleAuthUiProvider", "CredentialManager result: $result")

            // Extract credentials
            val creds = result.credential
            Log.d("GoogleAuthUiProvider", "Extracted credentials: $creds")

            // Handle and return credentials
            val account = handleCredentials(creds)
            Log.d("GoogleAuthUiProvider", "Successfully handled credentials: $account")

            Resource.Success(account)
        } catch (e: Exception) {
            Log.e("GoogleAuthUiProvider", "Error during Google Sign-In process", e)
            Resource.Error("Sign-in failed: ${e.message}")
        }
    }

    private fun handleCredentials(creds: Credential): GoogleAccount {
        Log.d("GoogleAuthUiProvider", "Handling credentials: $creds")

        if (creds is CustomCredential) {
            Log.d("GoogleAuthUiProvider", "Credential type: CustomCredential")

            if (creds.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Log.d("GoogleAuthUiProvider", "Credential subtype: GoogleIdTokenCredential")

                // Extract data from credential
                val idToken = creds.data["idToken"]?.toString()
                val displayName = creds.data["displayName"]?.toString()
                val profileImageUrl = creds.data["profilePictureUri"]?.toString()

                // Log extracted details
                Log.d("GoogleAuthUiProvider", "Extracted ID Token: $idToken")
                Log.d("GoogleAuthUiProvider", "Extracted Display Name: $displayName")
                Log.d("GoogleAuthUiProvider", "Extracted Profile Picture URL: $profileImageUrl")

                if (idToken != null) {
                    return GoogleAccount(
                        token = idToken,
                        displayName = displayName ?: "",
                        profileImageUrl = profileImageUrl ?: ""
                    )
                } else {
                    Log.e("GoogleAuthUiProvider", "ID Token is null or missing")
                    throw IllegalStateException("Google ID token is missing")
                }
            } else {
                Log.e("GoogleAuthUiProvider", "Unexpected credential type: ${creds.type}")
                throw IllegalStateException("Invalid credential type: Expected Google ID token credential")
            }
        } else {
            Log.e("GoogleAuthUiProvider", "Invalid credential type: Not CustomCredential")
            throw IllegalStateException("Invalid credential type")
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        Log.d("GoogleAuthUiProvider", "Building GetCredentialRequest with GoogleServiceClientId: $googleServiceClientId")

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption
                    .Builder(googleServiceClientId)
                    .build()
            )
            .build()

        Log.d("GoogleAuthUiProvider", "Built GetCredentialRequest: $request")
        return request
    }
}
