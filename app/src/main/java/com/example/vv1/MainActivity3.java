package com.example.vv1;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;
import java.util.UUID;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity3 extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private TextView logTextView;
    private EditText codeEditText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        logTextView = findViewById(R.id.logTextView);
        codeEditText = findViewById(R.id.codeEditText);
        Button generateButton = findViewById(R.id.generateButton);
        Button checkButton = findViewById(R.id.checkButton);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode();
            }
        });

        // Добавляем слушателя для обработки добавления новых кодов в базу данных
        mDatabase.child("codes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                String newCode = dataSnapshot.getValue(String.class);
                String enteredCode = codeEditText.getText().toString();
                if (enteredCode.equals(newCode)) {
                    logTextView.append("Connected\n");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void generateCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        String code = stringBuilder.toString();
        logTextView.append("Generated code: " + code + "\n");
        mDatabase.child("codes").push().setValue(code);
    }

    private void checkCode() {
        final String enteredCode = codeEditText.getText().toString();
        mDatabase.child("codes").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String code = snapshot.getValue(String.class);
                    if (code != null && code.equals(enteredCode)) {
                        connected = true;
                        break;
                    }
                }
                if (connected) {
                    logTextView.append("Connected\n");
                } else {
                    logTextView.append("Incorrect code\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "checkCode:onCancelled", databaseError.toException());
            }
        });
    }
}