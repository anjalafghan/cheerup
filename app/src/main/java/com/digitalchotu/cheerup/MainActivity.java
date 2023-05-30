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
    TextView quotes;
    Button getNewQuotes;
    int maximum = 0;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if (connected){
            showButtons();
        }
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_main);
        quotes = (TextView) findViewById(R.id.quotes);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                snapshot.getChildrenCount();
                Log.d("CHILDREN COUNT:",String.valueOf(snapshot.getChildrenCount()));
                maximum = (int) snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        getNewQuotes = (Button) findViewById(R.id.getNewQuotes);




        getNewQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int min = 1;
                final int max = maximum;
                Log.e("Maximum:", String.valueOf(max));
                final int random = new Random().nextInt((max - min) + 1) + min;
                String userId = String.valueOf(random);
                mDatabase.child("quotes").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {

                            quotes.setText(String.valueOf(task.getResult().getValue()));
                            Log.e("Random:",String.valueOf(random));

                        }
                    }
                });
            }
        });

    }

    private void showButtons() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Button) findViewById(R.id.getNewQuotes)).setVisibility(View.INVISIBLE);
                Log.e("DELAY1","DELAY1");
            }
        },0);
      handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              ((Button) findViewById(R.id.getNewQuotes)).setVisibility(View.VISIBLE);
              Log.e("DELAY2","DELAY2");
          }
      }, 3000);
    }


}