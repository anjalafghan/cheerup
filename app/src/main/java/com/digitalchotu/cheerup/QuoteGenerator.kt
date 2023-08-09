package com.digitalchotu.cheerup

import android.util.Log
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.util.Random

class QuoteGenerator(
        private val mDatabase: DatabaseReference,
        private val quotes: TextView,
        private val swipeRefreshLayout: SwipeRefreshLayout
) {

    var maximum: Int = 1

    fun generateRandomQuote() {
        val random = Random().nextInt(maximum) + 1
        val userId = random.toString()

        mDatabase.child("quotes").child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                val quote = dataSnapshot?.value.toString()
                quotes.text = quote
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun generateNoInternetQuote() {
        quotes.text = "You don't have internet but you will always have me!"
    }
}
