package com.example.simpleblogapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private Button btLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressBar;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.keepSynced(true);

        mProgressBar = findViewById(R.id.progress_horizontal_login);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btLogin = findViewById(R.id.bt_login);

       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent intent = new Intent(Login.this,Account.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   // finish();
                    startActivity(intent);

                }
            }
        };*/

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginIn();
            }
        });
/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        //If current user is logged in then take the user to Account page
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
           sendToAccountActivity();
        }
    }

    private void startLoginIn(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();


        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Email/Password are empty!", Toast.LENGTH_SHORT).show();
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                   if (!task.isSuccessful()) {
                       mProgressBar.setVisibility(View.INVISIBLE);
                        //Toast.makeText(Login.this, "User doesnt exists.Set up new account", Toast.LENGTH_SHORT).show();
                       if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                           Toast.makeText(Login.this, "Incorrect email address", Toast.LENGTH_SHORT).show();
                       }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                           Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(Login.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                       }

                   }else{
                       mProgressBar.setVisibility(View.INVISIBLE);
                       sendToAccountActivity();
                   }
                }
            });
            }
    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
   private void sendToAccountActivity(){
       Intent i = new Intent(Login.this,Account.class);
       //If set, this activity will become the start of a new task on this history stack.
       // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

       //If set in an Intent passed to Context#startActivity, this flag will cause any
       // existing task that would be associated with the activity to be cleared before the activity is started.
       // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                   /*If set, and the activity being launched is already running in the current task,
                   then instead of launching a new instance of that activity, all of the other activities
                   on top of it will be closed and this Intent will be delivered to the (now on top) old activity as a new Intent.
                   * */
       // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(i);
       finish();
   }
}
