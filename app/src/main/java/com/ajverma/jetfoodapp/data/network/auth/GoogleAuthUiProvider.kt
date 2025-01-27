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

        if (creds is CustomCredential && creds.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Parse the credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(creds.data)
                
                // Extract data using the proper methods
                return GoogleAccount(
                    token = googleIdTokenCredential.idToken ?: throw IllegalStateException("ID Token is null"),
                    displayName = googleIdTokenCredential.displayName ?: "",
                    profileImageUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                )
            } catch (e: Exception) {
                Log.e("GoogleAuthUiProvider", "Error parsing Google credential", e)
                throw IllegalStateException("Failed to parse Google credential: ${e.message}")
            }
        } else {
            Log.e("GoogleAuthUiProvider", "Invalid credential type: ${creds::class.java.simpleName}")
            throw IllegalStateException("Invalid credential type: Expected Google ID token credential")
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        Log.d("GoogleAuthUiProvider", "Building GetCredentialRequest with GoogleServiceClientId: $googleServiceClientId")
        
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(googleServiceClientId)
                    .build()
            )
            .build()
    }
}
