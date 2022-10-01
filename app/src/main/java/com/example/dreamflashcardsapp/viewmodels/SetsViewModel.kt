package com.example.dreamflashcardsapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dreamflashcardsapp.model.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SetsViewModel: ViewModel() {

    /** Variables */

    private val _sets = MutableLiveData<MutableList<FlashcardsSet>>()
    val sets: LiveData<MutableList<FlashcardsSet>> = _sets

    companion object {
        private const val TAG = "SetsViewModel"
    }

    /** Firestore */
    private var firestore: FirebaseFirestore

    /** FirebaseAuth */
    private var auth: FirebaseAuth

    init {
        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()
    }

    /** get sets from Firestore */
    fun getSetsFromFirestore() {

        firestore.collection("Sets").orderBy("current time").get()
            .addOnSuccessListener { querySnapshot ->

                Log.d(TAG, "Sets to display retrieved: ${querySnapshot.documents}")

                val list = mutableListOf<FlashcardsSet>()

                for(set in querySnapshot){

                    val flashcardsSet = FlashcardsSet(
                        setID = set.id,
                        setName = set.data["set name"].toString(),
                        creator = set.data["creator"].toString(),
                        wordsCount = set.data["number of words"].toString(),
                        learned = set.data["learned"].toString(),
                        next = set.data["next"].toString(),
                        currentTime = set.data["current time"].toString()
                    )

                    list.add(flashcardsSet)
                }

                Log.d(TAG, "Adding list sets to ViewModel: $list")
                _sets.value = list
                Log.d(TAG, "Current list sets: ${sets.value}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve sets from Firestore due to: ${e.message}")
            }

    }

}