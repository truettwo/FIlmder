package com.example.vv1.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vv1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CodeAuthActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private TextView logTextView;
    private EditText codeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeauth);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        logTextView = findViewById(R.id.logTextView);
        codeEditText = findViewById(R.id.codeEditText);
        Button generateButton = findViewById(R.id.generateButton);
        Button checkButton = findViewById(R.id.checkButton);
        Button watchAloneButton = findViewById(R.id.watchAloneButton);

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

        watchAloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToActSwipeAct();
            }
        });

        // Добавляем слушателя для обработки изменений статуса соединения
        mDatabase.child("status").child("connectionStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if ("Connected".equals(status)) {
                    logTextView.append("Connected\n");
                    switchToMainActivity4();
                }
            }

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
        mDatabase.child("codes").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    mDatabase.child("status").child("connectionStatus").setValue("Connected")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Успешно записано, удаляем статус
                                        mDatabase.child("status").removeValue();
                                    } else {
                                        Log.w(TAG, "Failed to set connection status", task.getException());
                                    }
                                }
                            });
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

    private void switchToMainActivity4() {
        Intent intent = new Intent(CodeAuthActivity.this, SwipeTogActivity.class);
        startActivity(intent);
    }
    private void switchToActSwipeAct() {
        Intent intent = new Intent(CodeAuthActivity.this, SwipeAloneActivity.class);
        startActivity(intent);
    }
}
