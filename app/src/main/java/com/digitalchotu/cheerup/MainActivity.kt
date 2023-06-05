package com.digitalchotu.cheerup
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var quotes: TextView
    private lateinit var getNewQuotes: Button
    private var maximum: Int = 0
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        hideActionBar()
        setupDatabase()
        checkInternetConnectivity()
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
                maximum = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun generateNewQuoteOnClick() {
        getNewQuotes.setOnClickListener { generateRandomQuote() }
    }

    private fun setupOnRefreshView() {
        swipeRefreshLayout.setOnRefreshListener { checkInternetConnectivity() }
    }

    private fun checkInternetConnectivity() {
        if (isNetworkAvailable()) {
            getNewQuotes.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        } else {
            Toast.makeText(this, "You don't have internet but you will always have me", Toast.LENGTH_SHORT).show()
            getNewQuotes.visibility = View.INVISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService<ConnectivityManager>()
        val network = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.activeNetwork
        } else {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
        val capabilities = connectivityManager?.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun generateRandomQuote() {
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
