package com.example.dreamflashcardsapp

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dreamflashcardsapp.databinding.ActivityEmailPasswordBinding
import com.example.dreamflashcardsapp.fragments.EmailSignInFragment
import com.example.dreamflashcardsapp.fragments.EmailSignUpFragment

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailPasswordBinding

    // ActionBar to support navigation back
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure ActionBar to support navigation back
        actionBar = supportActionBar!!
        actionBar.title = "Email and Password"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(EmailSignInFragment(), "Sign In")
        pagerAdapter.addFragment(EmailSignUpFragment(), "Sign Up")
        binding.viewPager.adapter = pagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

    }

    class PagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragmentList: MutableList<Fragment> = ArrayList()
        private val titleList: MutableList<String> = ArrayList()

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }

    /** Go to previous activity support function */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionbar is clicked
        finish()
        return super.onSupportNavigateUp()
    }

}