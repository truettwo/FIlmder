package com.example.vv1;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.UUID;


public class MainActivity3 extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button syncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        textView = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextText);
        syncButton = findViewById(R.id.button2);

        FirebaseHelper helper = new FirebaseHelper();

        // Очистка всех данных перед использованием приложения
        helper.clearAllData();

        // Пользователь A генерирует код и создает соединение
        String code = helper.generateCode();
        helper.createConnection(code);
        textView.append("Generated code: " + code + "\n");

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Пользователь B вводит код и подключается к соединению
                String enteredCode = editText.getText().toString();
                DatabaseReference connectionRef = helper.getConnectionRef(enteredCode);
                connectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (code.equals(enteredCode)) {
                            textView.append("Connected!\n");
                            connectionRef.removeValue(); // Удаляем код после успешного подключения
                        } else {
                            textView.append("Failed to connect. The code does not match.\n");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        textView.append("Failed to connect.\n");
                    }
                });
            }
        });
    }
}