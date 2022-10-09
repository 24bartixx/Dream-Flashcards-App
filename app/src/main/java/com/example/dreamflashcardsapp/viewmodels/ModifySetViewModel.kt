package com.example.dreamflashcardsapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dreamflashcardsapp.model.Flashcard
import com.example.dreamflashcardsapp.model.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    // flashcards to modify
    private val _modifyFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val modifyFlashcards: LiveData<MutableList<Flashcard>> = _modifyFlashcards

    // tells if set is already created or not
    private val _setCreated = MutableLiveData(false)
    val setCreated: LiveData<Boolean> = _setCreated


    companion object {
        private const val TAG = "ModifySetViewModel"
    }

    /** Firestore */
    private var firestore: FirebaseFirestore = Firebase.firestore

    /** FirebaseAuth */
    private var auth: FirebaseAuth

    /** SetsViewModel */

    init {
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
        _modifyFlashcards.value = mutableListOf()

        val currentTime = System.currentTimeMillis()

        val newSetData = hashMapOf(
            "set name" to setName.value,
            "creator" to auth.currentUser!!.uid,
            "number of words" to 0,
            "learned" to 0,
            "next" to 1,
            "time of creation" to currentTime
        )

        firestore.collection("Sets").add(newSetData)
            .addOnSuccessListener { documentReference ->

                Log.d(TAG, "Set added to Firestore")

                val newFlashcardsSet = FlashcardsSet(
                    setID = documentReference.id,
                    setName = setName.value!!,
                    creator = auth.currentUser!!.uid,
                    wordsCount = "0",
                    learned = "0",
                    next = "1",
                    creationTime = currentTime.toString()
                )

                _modifySet.value = newFlashcardsSet
                _setCreated.value = true

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Cannot add set to collection due to: ${e.message}")
            }

    }

    /** add flashcard to Firestore */
    fun addFlashcardToFirestore(term: String, definition: String) {

        val flashcardID = modifySet.value!!.next

        val newFlashcardData = hashMapOf(
            "term$flashcardID" to term,
            "definition$flashcardID" to definition,
            "learned$flashcardID" to "no"
        )

        firestore.collection("Sets").document(modifySet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument")
            .set(newFlashcardData, SetOptions.merge())
            .addOnSuccessListener {

                var listToUpdate = modifyFlashcards.value

                if(listToUpdate.isNullOrEmpty()){
                    listToUpdate = mutableListOf(Flashcard(flashcardID, term, definition, "no"))
                } else {
                    listToUpdate.add(Flashcard(flashcardID, term, definition, "no"))
                }

                if(!listToUpdate.isNullOrEmpty()) { _modifyFlashcards.value = listToUpdate!! }

                updateSetInFirestore()

                Log.d(TAG, "Flashcard ${Flashcard(flashcardID, term, definition, "no")} added to document with id: \"FlashcardsDocument\"")
                Log.d(TAG, "Current modify flashcards: ${modifyFlashcards.value}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Adding flashcard in the Firestore went wrong due to: ${e.message}")
            }
    }

    /** update set after flashcard is added */
    private fun updateSetInFirestore(){

        val thingsToUpdate = hashMapOf(
            "number of words" to modifySet.value!!.wordsCount.toInt() + 1,
            "next" to modifySet.value!!.next.toInt() + 1
        )

        firestore.collection("Sets").document(modifySet.value!!.setID).set(thingsToUpdate, SetOptions.merge())
            .addOnSuccessListener {
                _modifySet.value!!.wordsCount = (modifySet.value!!.wordsCount.toInt() + 1).toString()
                _modifySet.value!!.next = (modifySet.value!!.next.toInt() + 1).toString()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Set's words count or next not updated due to: ${e.message}")
            }

    }

    fun getFlashcardsToModify(flashcardsSet: FlashcardsSet){

        _modifySet.value = FlashcardsSet("", "", "", "", "", "", "")
        _modifyFlashcards.value = mutableListOf()
        var flashcardsList = mutableListOf<Flashcard>()

        firestore.collection("Sets").document(flashcardsSet.setID)
            .collection("Flashcards").document("FlashcardsDocument").get()
            .addOnSuccessListener { documentSnapshot ->

                Log.d(TAG, "Flashcards retrieved: ${documentSnapshot.data}")

                for(index in 1..flashcardsSet.next.toInt()){

                    val termString = "term${index}"
                    val definitionString = "definition${index}"
                    val learnedString = "learned${index}"

                    val flashcard = Flashcard(
                        flashcardOrder = index.toString(),
                        term = documentSnapshot.data?.get(termString).toString(),
                        definition = documentSnapshot.data?.get(definitionString).toString(),
                        learned = documentSnapshot.data?.get(learnedString).toString()
                    )

                    if(flashcard.term != "null" && flashcard.definition != "null" && flashcard.learned != "null") {
                        flashcardsList.add(flashcard)
                    }

                }

                Log.d(TAG, "Setting list of flashcards to modify to: $flashcardsList")
                _modifyFlashcards.value = flashcardsList
                Log.d(TAG, "Current list of flashcards to modify: ${modifyFlashcards.value}")

                _modifySet.value = flashcardsSet

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Cannot retrieve flashcards to modify due to: ${e.message}")
            }

    }

}