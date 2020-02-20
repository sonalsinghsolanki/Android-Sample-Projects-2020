package com.example.simpleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText mEdtFullName,mEdtEmail,mEdtPassword,mEdtPhoneNo,mEdtConfirmPassword;
    private Button bt_register;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef;
    private Toolbar toolbarRegister;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbarRegister = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbarRegister);
        getSupportActionBar().setTitle("Register User");



        mAuth = FirebaseAuth.getInstance();


        //Check if user is already loggind in then app will directly navigate to account page.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    if(firebaseAuth.getCurrentUser()!=null){
                        Intent intent = new Intent(Register.this,Account.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         finish();
                        startActivity(intent);

                    }
                }
            }
        };

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.keepSynced(true);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_horizontal) ;

        mEdtFullName = findViewById(R.id.edt_full_name);
        mEdtEmail = findViewById(R.id.edt_email);
        mEdtPassword = findViewById(R.id.edt_password);
        mEdtPhoneNo = findViewById(R.id.edt_phone_no);
        mEdtConfirmPassword = findViewById(R.id.edt_confirm_password);
        bt_register = findViewById(R.id.bt_register);

        //if logged in then new user creation process will start..
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }


        });
    }


    private void startRegister() {
        final String name = mEdtFullName.getText().toString().trim();
        final String email = mEdtEmail.getText().toString().trim();
        String password = mEdtPassword.getText().toString().trim();
        String confirmPassword = mEdtConfirmPassword.getText().toString().trim();
        String phoneno = mEdtPhoneNo.getText().toString().trim();

        //check if all fields values are not empty
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(confirmPassword) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneno))
        {
            if(password.equals(confirmPassword))
            {

                mProgressBar.setVisibility(View.VISIBLE);

                //User creation process starts ..
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user = mDatabaseRef.child(user_id);
                            current_user.child("name").setValue(name);
                            current_user.child("image").setValue("default");

                            mProgressBar.setVisibility(View.INVISIBLE);
                            finishAffinity();
                            Intent i = new Intent(Register.this, AccountSetup.class);

                            startActivity(i);


                        } else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(Register.this,"User already Exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(Register.this, "Mismatch Password and Confirm Password,please try again!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Register.this,
                    "Email/Password/Name/Phone number cannot be blank.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

}
