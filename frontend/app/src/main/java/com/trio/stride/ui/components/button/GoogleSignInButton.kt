package com.trio.stride.ui.components.button

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun GoogleSignInButton(
    text: String = "Log In With Google",
    context: Context = LocalContext.current,
    onTokenReceived: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != android.app.Activity.RESULT_OK) {
            Log.w("GoogleSignIn", "User cancelled Google Sign-In")
            onTokenReceived(null)
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            onTokenReceived(idToken)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Error during Google Sign-In: ${e.message}")
            onTokenReceived(null)
        }
    }

    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(stringResource(R.string.google_login_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, signInOptions)
    }

    OutlinedButton(
        onClick = {
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = googleSignInClient.signInIntent
                launcher.launch(intent)
            }
        },
        colors = ButtonDefaults.outlinedButtonColors().copy(
            containerColor = StrideTheme.colors.transparent,
            contentColor = StrideTheme.colors.gray600
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(42.dp)
            .fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = "Google Icon"
            )
            Spacer(Modifier.width(12.dp))
            Text(text, style = StrideTheme.typography.titleMedium)
        }
    }
}