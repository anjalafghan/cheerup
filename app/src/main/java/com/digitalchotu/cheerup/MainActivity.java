package com.digitalchotu.cheerup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView quotes;
    private Button getNewQuotes;
    private int maximum = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        hideActionBar();
        setupDatabase();
        setupGetNewQuotesButton();
        showButtons();
        setupOnRefreshView();
    }

    private void initializeViews() {
        quotes = findViewById(R.id.quotes);
        getNewQuotes = findViewById(R.id.getNewQuotes);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void hideActionBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void setupDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maximum = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void setupGetNewQuotesButton() {
        getNewQuotes.setOnClickListener(view -> generateRandomQuote());
    }

    private void setupOnRefreshView() {
        swipeRefreshLayout.setOnRefreshListener(this::checkInternetConnectivity);
    }

    private void checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            generateRandomQuote();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void generateRandomQuote() {
        final int min = 1;
        final int max = maximum;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String userId = String.valueOf(random);

        mDatabase.child("quotes").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot != null) {
                    String quote = String.valueOf(dataSnapshot.getValue());
                    quotes.setText(quote);
                    Log.e("Random:", String.valueOf(random));
                }
            } else {
                Log.e("Firebase", "Error getting data: " + task.getException());
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void showButtons() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        getNewQuotes.setVisibility(networkInfo != null && networkInfo.isConnectedOrConnecting() ? View.VISIBLE : View.INVISIBLE);
    }
}
