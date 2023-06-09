package com.digitalchotu.cheerup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var quotes: TextView
    private lateinit var getNewQuotes: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var networkManager: NetworkManager
    private lateinit var quoteGenerator: QuoteGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        hideActionBar()
        setupDatabase()

    }

    override fun onStart() {
        super.onStart()
        setupNetworkManager()
        checkInternetConnectivity()
        setupQuoteGenerator()
        generateNewQuoteOnClick()
        setupOnRefreshView()

    }


    private fun initializeViews() {
        quotes = findViewById(R.id.quotes)
        getNewQuotes = findViewById(R.id.getNewQuotes)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    private fun setupDatabase() {
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("quotes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val maximum = snapshot.childrenCount.toInt()
                quoteGenerator.maximum = maximum
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun setupNetworkManager() {

        networkManager = NetworkManager(getSystemService())
    }

    private fun setupQuoteGenerator() {
        quoteGenerator = QuoteGenerator(mDatabase, quotes, swipeRefreshLayout)
    }

    private fun generateNewQuoteOnClick() {
        getNewQuotes.setOnClickListener {
            checkInternetConnectivity()
            quoteGenerator.generateRandomQuote()

        }
    }

    private fun setupOnRefreshView() {
        swipeRefreshLayout.setOnRefreshListener { checkInternetConnectivity() }
    }

    private fun checkInternetConnectivity() {
        val isNetworkAvailable = networkManager.isNetworkAvailable()
        with(swipeRefreshLayout) {
            isRefreshing = false
            if (isNetworkAvailable) {
                getNewQuotes.visibility = View.VISIBLE
            } else {
                Toast.makeText(this@MainActivity, "You don't have internet but you will always have me", Toast.LENGTH_SHORT).show()
                getNewQuotes.visibility = View.INVISIBLE
            }
        }
    }

}


