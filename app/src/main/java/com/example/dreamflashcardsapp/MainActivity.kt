package com.example.dreamflashcardsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dreamflashcardsapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        /** binding.logOutButton.setOnClickListener{

            auth.signOut()
            LoginManager.getInstance().logOut()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        } */

    }
}