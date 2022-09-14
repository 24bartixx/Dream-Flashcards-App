package com.example.dreamflashcardsapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dreamflashcardsapp.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    // viewBinding
    private lateinit var binding: ActivityLoginBinding

    // progressDialog
    private lateinit var progressDialog: ProgressDialog

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    /** Facebook variables */
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val GOOGLE_TAG = "Google Authorization"
        private const val FACEBOOK_TAG = "Facebook Authorization"
        private const val GOOGLE_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // prepare a ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing in...")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        checkIfLogged()

        // set photo in the background
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.transparent)
        window.navigationBarColor = resources.getColor(android.R.color.transparent)
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_photo))

        /** Google authorization */
        // GoogleSignInOptions contains options to configure GoogleSignIn API
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // client for interacting with GoogleSignIn API
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.googleSignInButton.setOnClickListener {

            Log.d(GOOGLE_TAG, "begin GoogleSignIn")
            // intent required to start GoogleSignIn flow
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, GOOGLE_SIGN_IN)

        }

        /** Facebook authorization */

        // CallbackManager manages the callbacks into FacebookSDK from onActivityResult()
        callbackManager = CallbackManager.Factory.create()

        binding.facebookButton.setReadPermissions("email", "public_profile")

        binding.facebookButton.setOnClickListener {
            facebookLogin()
        }

    }

    /** check if logged */
    private fun checkIfLogged(){
        if(auth.currentUser != null){
            // if authorization has a user, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){

            /** Google */
            Log.d(GOOGLE_TAG, "Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                signInWithGoogleAccount(account)
            } catch(e: Exception) {
                // failed Google SignIn
                Log.d(GOOGLE_TAG, "Error during Google authorization in the result Activity: ${e.message}")
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }

        }

    }

    /** Google authorization */
    private fun signInWithGoogleAccount(account: GoogleSignInAccount){

        Log.d(GOOGLE_TAG, "Begin firebase auth with Google account")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->

                Log.i(GOOGLE_TAG, "Authorization succeeded")
                Log.i(GOOGLE_TAG, "Logged with email: ${auth.currentUser!!.email}")

                if(authResult.additionalUserInfo!!.isNewUser){
                    Log.i(GOOGLE_TAG, "Account created...")
                    Toast.makeText(this, "Account created with email:\n${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(GOOGLE_TAG, "Logged in...")
                    Toast.makeText(this, "Signed in with email:\n${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()
                }

                // start app activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->

                Log.e(GOOGLE_TAG, "Authorization failed due to ${e.message}")
                Toast.makeText(this, "Google authorization failed", Toast.LENGTH_SHORT).show()

            }

    }

    /** Facebook authorization */
    private fun facebookLogin(){

        Log.i(FACEBOOK_TAG, "begin Facebook Authorization")

        binding.facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{

            override fun onSuccess(result: LoginResult) {
                Log.d(FACEBOOK_TAG, "Facebook Authorization succeeded, handleFacebookAccessToken() function called")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.w(FACEBOOK_TAG, "Facebook Authorization cancelled")
                Toast.makeText(applicationContext, "Facebook Authorization cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.e(FACEBOOK_TAG, "Facebook Authorization failed due to: ${error.message}")
                Toast.makeText(applicationContext, "Facebook Authorization failed", Toast.LENGTH_SHORT).show()
            }

        })

    }

    /** Facebook authorization */
    private fun handleFacebookAccessToken(accessToken: AccessToken){

        Log.d(FACEBOOK_TAG, "handleFacebookAccessToken: ${accessToken}")

        val credential = FacebookAuthProvider.getCredential(accessToken.token)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Log.i(FACEBOOK_TAG, "signInWithCredential succeeded with email ${auth.currentUser!!.email}")
                Toast.makeText(this, "Facebook Authorization succeeded", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(FACEBOOK_TAG, "signInWithCredential failed due to ${e.message}")
                Toast.makeText(this, "Facebook Authorization failed", Toast.LENGTH_SHORT).show()
            }

    }

}