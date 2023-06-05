package com.digitalchotu.cheerup

import android.util.Log
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.util.Random

class QuoteGenerator(private val mDatabase: DatabaseReference, private val quotes: TextView, private val swipeRefreshLayout: SwipeRefreshLayout) {

    var maximum: Int = 1

    fun generateRandomQuote() {
        val min = 1
        val max = maximum
        val random = Random().nextInt(max - min + 1) + min
        val userId = random.toString()

        mDatabase.child("quotes").child(userId).get().addOnCompleteListener { task: Task<DataSnapshot?> ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                if (dataSnapshot != null) {
                    val quote = dataSnapshot.value.toString()
                    quotes.text = quote
                    Log.e("Random:", random.toString())
                }
            } else {
                Log.e("Firebase", "Error getting data: ${task.exception}")
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}