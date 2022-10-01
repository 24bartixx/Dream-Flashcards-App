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

class ModifySetViewModel: ViewModel() {

    /** Variables */

    // name of the set
    private val _setName = MutableLiveData<String>()
    private val setName: LiveData<String> = _setName

    // set to modify
    private val _modifySet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", ""))
    val modifySet: LiveData<FlashcardsSet> = _modifySet

    // tells if set is already created or not
    private val _setCreated = MutableLiveData(false)
    val setCreated: LiveData<Boolean> = _setCreated


    companion object {
        private const val TAG = "ModifySetViewModel"
    }

    /** Firestore */
    private var firestore: FirebaseFirestore

    /** FirebaseAuth */
    private var auth: FirebaseAuth

    init {
        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        _setCreated.value = false
    }

    /** set name of the set */
    fun setSetName(newName: String) {
        _setName.value = newName
        Log.i(TAG, "New set name: $newName")
    }

    /** set setCreated */
    fun setSetCreated(newSetCreatedBoolean: Boolean) {
        _setCreated.value = newSetCreatedBoolean
        Log.i(TAG, "New setCreated: $newSetCreatedBoolean")
    }

    /** add set to Firestore */
    fun addSetToFirestore() {

        _setCreated.value = false
        _modifySet.value = FlashcardsSet("", "", "", "", "", "", "")

        val currentTime = System.currentTimeMillis()

        val newSetData = hashMapOf(
            "set name" to setName.value,
            "creator" to auth.currentUser!!.uid,
            "number of words" to 0,
            "learned" to 0,
            "next" to 1,
            "current time" to currentTime
        )

        firestore.collection("Sets").add(newSetData)
            .addOnSuccessListener { documentReference ->

                _setCreated.value = true
                Log.d(TAG, "Set added to Firestore")

                _modifySet.value = FlashcardsSet(
                    setID = documentReference.id,
                    setName = setName.value!!,
                    creator = auth.currentUser!!.uid,
                    wordsCount = "0",
                    learned = "0",
                    next = "1",
                    currentTime = currentTime.toString()
                )



            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Cannot add set to collection due to: ${e.message}")
            }

    }

}