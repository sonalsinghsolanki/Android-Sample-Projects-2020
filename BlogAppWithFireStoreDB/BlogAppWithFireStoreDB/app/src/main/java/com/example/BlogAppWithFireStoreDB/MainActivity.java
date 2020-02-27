package com.example.BlogAppWithFireStoreDB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView txtMainLogin,txtMainSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtMainLogin = findViewById(R.id.txt_main_login);
        txtMainSignup = findViewById(R.id.txt_main_signup);


        txtMainSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finishAffinity();
                Intent intent = new Intent(MainActivity.this,Register.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        txtMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // finishAffinity();
                Intent intent = new Intent(MainActivity.this,Login.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //If current user is logged in then take the user to Account page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
           sendToAccountActivity();
        }
    }
    private void sendToAccountActivity(){
        Intent i = new Intent(MainActivity.this,Account.class);
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
