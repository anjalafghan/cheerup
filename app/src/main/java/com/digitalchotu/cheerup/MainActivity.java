package com.digitalchotu.cheerup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView quotes;
    private Button getNewQuotes;
    private int maximum = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        hideActionBar();
        setupDatabase();
        setupGetNewQuotesButton();
        showButtons();
    }

    private void initializeViews() {
        quotes = findViewById(R.id.quotes);
        getNewQuotes = findViewById(R.id.getNewQuotes);
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
        Log.e("Maximum:", String.valueOf(max));
        final int random = new Random().nextInt((max - min) + 1) + min;
        String userId = String.valueOf(random);

        mDatabase.child("quotes").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data: " + task.getException());
                } else {
                    quotes.setText(String.valueOf(task.getResult().getValue()));
                    Log.e("Random:", String.valueOf(random));
                }
            }
        });
    }

    private void showButtons() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if ((networkInfo != null) && networkInfo.isConnectedOrConnecting()) {
            getNewQuotes.setVisibility(View.VISIBLE);
//            handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getNewQuotes.setVisibility(View.INVISIBLE);
//                    Log.e("DELAY1", "DELAY1");
//                }
//            }, 0);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("DELAY2", "DELAY2");
//                }
//            }, 3000);
        } else {
            getNewQuotes.setVisibility(View.INVISIBLE);

            // No internet connection, handle accordingly (e.g., show a message, disable buttons)
        }
    }

}
