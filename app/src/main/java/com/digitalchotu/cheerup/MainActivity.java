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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private void setupOnRefreshView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternetConnectivity();
            }
        });
    }

    private void checkInternetConnectivity() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Internet connection is available, perform the refresh action
            generateRandomQuote();
        } else {
            // No internet connection, show a message or handle accordingly
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
        }
    }

    private void initializeViews() {
        quotes = findViewById(R.id.quotes);
        getNewQuotes = findViewById(R.id.getNewQuotes);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    }

    private void hideActionBar() {
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        getNewQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomQuote();
            }
        });
    }

    private void generateRandomQuote() {
        final int min = 1;
        final int max = maximum;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String userId = String.valueOf(random);

        mDatabase.child("quotes").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null) {
                        String quote = String.valueOf(dataSnapshot.getValue());
                        quotes.setText(quote);
                        Log.e("Random:", String.valueOf(random));
                        swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation

                    }
                } else {
                    Log.e("Firebase", "Error getting data: " + task.getException());
                    swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation

                }
            }
        });
    }

    private void showButtons() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            getNewQuotes.setVisibility(View.VISIBLE);
        } else {
            getNewQuotes.setVisibility(View.INVISIBLE);
            // No internet connection, handle accordingly (e.g., show a message, disable buttons)
        }
    }
}
