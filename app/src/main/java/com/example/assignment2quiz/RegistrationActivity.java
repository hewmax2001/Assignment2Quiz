package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

public class RegistrationActivity extends AppCompatActivity {
    private EditText txtUsername, txtPassword;
    private Button btnRegister, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setElements();
    }

    protected void onResume() {
        super.onResume();
        clearTextElements();
    }

    private void setElements() {
        txtUsername = findViewById(R.id.txt_register_username);
        txtPassword = findViewById(R.id.txt_register_password);

        btnRegister = findViewById(R.id.btn_register_register);
        btnClear = findViewById(R.id.btn_clear);

        setButtonEvents();
    }

    private void setButtonEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {return;}

                btnRegister.setEnabled(false);

                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                Handler.insertUser(new Callback() {
                    @Override
                    public void onCallback(DataSnapshot snap) {
                        Toast.makeText(getApplicationContext(), "User has been registered successfully.", Toast.LENGTH_SHORT).show();
                        returnToLoginActivity();
                        btnRegister.setEnabled(true);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getApplicationContext(), "User with the same name and password already exists!", Toast.LENGTH_SHORT).show();
                        btnRegister.setEnabled(true);
                    }
                }, username, password);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextElements();
            }
        });
    }

    private boolean validation() {
        return !txtUsername.getText().equals("") && !txtPassword.getText().equals("");
    }

    private void clearTextElements() {
        txtUsername.setText("");
        txtPassword.setText("");
    }

    private void returnToLoginActivity() {
        finish();
    }
}