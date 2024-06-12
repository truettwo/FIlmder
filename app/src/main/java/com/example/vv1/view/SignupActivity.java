package com.example.vv1.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vv1.R;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.et_name);
        emailEditText = findViewById(R.id.et_signup_email);
        passwordEditText = findViewById(R.id.et_signup_password);
        signupButton = findViewById(R.id.btn_signup);
        loginTextView = findViewById(R.id.tv_login);

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, CodeAuthActivity.class);
            startActivity(intent);
        });

        loginTextView.setOnClickListener(v -> {

            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}