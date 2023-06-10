package com.example.assignment2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername, txtPassword;
    private Button btnLogin, btnRegister, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler.start();
        setElements();
    }

    protected void onResume() {
        super.onResume();
        clearTextElements();
    }

    private void setElements() {
        txtUsername = findViewById(R.id.txt_login_username);
        txtPassword = findViewById(R.id.txt_login_password);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_login_register);
        btnClear = findViewById(R.id.btn_clear);

        setButtonEvents();
    }

    private void setButtonEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {return;}

                btnLogin.setEnabled(false);

                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();



                CloudDatabase.readDataOnce(new Callback() {
                    @Override
                    public void onCallback(DataSnapshot snap) {
                        Iterable<DataSnapshot> userSnaps = snap.getChildren();
                        for (DataSnapshot userSnap: userSnaps) {
                            User user = userSnap.getValue(User.class);
                            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                                // TODO add current user functionality
                                login();
                            }
                        }
                    }

                    @Override
                    public void onFailure() {

                    }
                }, CloudDatabase.getRef("users"));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegistration();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextElements();
            }
        });
    }

    private void login() {
        // TODO add login implementation
    }

    private void loadRegistration() {
        Intent registerIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registerIntent);
    }

    private boolean validation() {
        return !txtUsername.getText().equals("") && !txtPassword.getText().equals("");
    }

    private void clearTextElements() {
        txtUsername.setText("");
        txtPassword.setText("");
    }
}