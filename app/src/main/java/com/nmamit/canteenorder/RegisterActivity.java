package com.nmamit.canteenorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText etName, etMail, etPassword;
    private Button btRegister;
    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";
    private static final String USER_TYPE = "Customer";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final  String PREFERENCE_FILE_KEY = "com.nmamit.canteenorder.PREFERENCE_FILE_KEY";

//    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("User");
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mCollRef = FirebaseFirestore.getInstance().collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.register_et_name);
        etMail = findViewById(R.id.register_et_mail);
        etPassword = findViewById(R.id.register_et_password);
        btRegister = findViewById(R.id.register_bt_register);

        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mAuth = FirebaseAuth.getInstance();

        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = etName.getText().toString();
        String mail = etMail.getText().toString();
        String password = etPassword.getText().toString();

        if(validateForm())
        {
            showProgressDialog();
            mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.i(TAG, "onComplete: ");
//                                updateUI(user);
                                addUser(user);
                            }
                        else
                        {
                            String exceptionMessage = task.getException().getLocalizedMessage();
                            Toast.makeText(RegisterActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Failed register: " + exceptionMessage);
                        }

//                            else {
//                                // If sign in fails, display a message to the user.
//                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
//                                updateUI(null);
//                            }
                            hideProgressDialog();
                        }
                    });
        }
    }

    private void addUser(final FirebaseUser user) {
        String name = etName.getText().toString();
        final String email = etMail.getText().toString();
        String userId = user.getUid();

        Map<String,Object> userData = new HashMap<String, Object>();
        userData.put("name", name);
        userData.put("type", USER_TYPE);
        userData.put("userId", userId);

        mCollRef.document(email)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Userdata added successfully");
                        editor.putString("user_email", email);
                        editor.commit();
                        updateUI(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document");
                    }
                });
    }

    public void updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            etName.setError("Required.");
            valid = false;
        } else {
            etName.setError(null);
        }

        String email = etMail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etMail.setError("Required.");
            valid = false;
        } else {
            etMail.setError(null);
        }


        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }
}
