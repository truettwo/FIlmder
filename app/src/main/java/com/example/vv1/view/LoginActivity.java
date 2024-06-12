package com.example.vv1.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vv1.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        signupTextView = findViewById(R.id.tv_signup);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CodeAuthActivity.class);
            startActivity(intent);
        });

        signupTextView.setOnClickListener(v -> {
            // Handle sign up logic here
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}