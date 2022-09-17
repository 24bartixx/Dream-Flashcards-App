package com.example.dreamflashcardsapp.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dreamflashcardsapp.MainActivity
import com.example.dreamflashcardsapp.databinding.FragmentEmailSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class EmailSignUpFragment : Fragment() {

    private lateinit var _binding: FragmentEmailSignUpBinding
    private val binding get() = _binding!!

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    var email = ""
    var password = ""

    companion object {
        private const val TAG = "EmailSignUpFragment"
        private const val MIN_PASSWORD_LENGTH = 7
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // configure ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Signing up..")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // if sign up button pressed validate the input data
        binding.loginButton.setOnClickListener {
            validateData()
        }

    }

    /** validate data for email authorization function */
    private fun validateData(){

        email = binding.emailInputLayout.editText?.text.toString().trim()
        password = binding.passwordInputLayout.editText?.text.toString().trim()
        hideAllErrors()

        if(email == "" || email.isNullOrEmpty()){

            // if empty email, trigger an error
            Log.e(TAG, "Empty email during signing in")
            binding.emailInputLayout.error = "Please enter your email"

        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            // if email input text has incorrect format, trigger an error
            Log.e(TAG, "Incorrect email format")
            binding.emailInputLayout.error = "Incorrect email format"

        } else if (password == "" || password.isNullOrEmpty()){

            // if empty password, trigger an error
            Log.e(TAG, "Empty password during signing in")
            binding.passwordInputLayout.error = "Please enter your password"

        } else if(password.length < MIN_PASSWORD_LENGTH) {

            // if password has not at least 7 characters, trigger an error
            Log.e(TAG, "Too short password, must be at least $MIN_PASSWORD_LENGTH characters")
            binding.passwordInputLayout.error = "Your password must have at least $MIN_PASSWORD_LENGTH characters"

        } else if(!checkIfCapitalLetter(password)){

            // if password has not a capital letter, trigger an error
            Log.e(TAG, "No capital letter in the password")
            binding.passwordInputLayout.error = "Your password must have a capital letter"

        } else if(!checkIfDigit(password)){

            // if password has not a number, trigger an error
            Log.e(TAG, "No number in the password")
            binding.passwordInputLayout.error = "Your password must have a number"

        } else if(password != binding.repeatPasswordEditText.text.toString()) {

            // if repeated password is incorrect, trigger an error
            Log.e(TAG, "Two different passwords entered")
            binding.repeatPasswordInputLayout.error = "You must enter the same passwords"
            binding.repeatPasswordInputLayout.editText?.text = SpannableStringBuilder("")

        } else {
            signupUser()
        }

    }

    /** hide all errors on the screen */
    private fun hideAllErrors(){
        binding.emailInputLayout.isErrorEnabled = false
        binding.passwordInputLayout.isErrorEnabled = false
        binding.repeatPasswordInputLayout.isErrorEnabled = false
    }

    /** check if any letter is capital */
    private fun checkIfCapitalLetter(string: String): Boolean {

        var isCapital = false
        for (letter in string){
            Log.d(TAG, letter.toString())
            if(letter.isUpperCase()){
                isCapital = true
                break
            }
        }

        return isCapital

    }

    /** check if any digit */
    private fun checkIfDigit(string: String): Boolean {

        var isDigit = false

        for (letter in string) {

            val code = letter.code
            if (code in 48..57) {
                isDigit = true
                break
            }
        }

        return isDigit

    }

    /** Sign up user */
    private fun signupUser(){

        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Log.i(TAG,"User created with email: ${auth.currentUser!!.email}")
                Toast.makeText(requireContext(),"New user created", Toast.LENGTH_SHORT).show()

                startActivity(Intent(requireContext(), MainActivity::class.java))

            }
            .addOnFailureListener { exception ->

                progressDialog.dismiss()
                Log.e(TAG, "Sign up failed due to: ${exception.message}")
                Toast.makeText(requireContext(), "Sign up failed", Toast.LENGTH_SHORT).show()

            }

    }

}