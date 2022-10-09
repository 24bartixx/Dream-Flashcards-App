package com.example.dreamflashcardsapp.model

data class FlashcardsSet (
    val setID: String,
    val setName: String,
    val creator: String,
    var wordsCount: String,
    var learned: String,
    var next: String,
    val creationTime: String
)