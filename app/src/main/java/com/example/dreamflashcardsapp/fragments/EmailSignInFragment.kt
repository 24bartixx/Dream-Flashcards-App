package com.example.dreamflashcardsapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dreamflashcardsapp.databinding.FragmentEmailSignInBinding
import com.google.firebase.auth.FirebaseAuth

class EmailSignInFragment : Fragment() {

    private lateinit var _binding: FragmentEmailSignInBinding
    private val binding get() = _binding!!

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    var email = ""
    var password = ""

    companion object {
        private const val TAG = "EmailSignInFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEmailSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // configure ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Signing in...")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // handle login button click
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

        } else if (password == "" || password.isNullOrEmpty()){

            // if empty password, trigger an error
            Log.e(TAG, "Empty password during signing in")
            binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.passwordInputLayout.error = "Please enter your password"

        }

    }

    /** hide all errors on the screen */
    private fun hideAllErrors(){
        binding.emailInputLayout.isErrorEnabled = false
        binding.passwordInputLayout.isErrorEnabled = false
    }

}