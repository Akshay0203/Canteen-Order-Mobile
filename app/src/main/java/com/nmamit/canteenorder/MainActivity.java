package com.nmamit.canteenorder;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private Button btSignIn, btRegister;
    private FirebaseAuth mAuth;
    private String email;
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final  String PREFERENCE_FILE_KEY = "com.nmamit.canteenorder.PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btSignIn = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_register);


        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        mAuth = FirebaseAuth.getInstance();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = etEmail.getText().toString();
                String password = etPassword.getText().toString();

//                FirebaseAuthException exception;

                if(validateForm(mail, password))
                {
                    showProgressDialog();
                    mAuth.signInWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        editor.putString("user_email", mail);
                                        editor.commit();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    }
                                    else if(task.getException() instanceof FirebaseAuthInvalidUserException)
                                    {
                                        String toastMessage = "User account not found for "+ etEmail.getText().toString();
                                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                                    }
                                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                                    hideProgressDialog();
                                }
                            });
                }
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null)
        {
            email = currentUser.getEmail();
            editor.putString("user_email", email);
            editor.commit();
//            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }
}

