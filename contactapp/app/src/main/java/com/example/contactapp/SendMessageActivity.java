package com.example.contactapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

public class SendMessageActivity extends AppCompatActivity {
    private EditText mMessageDetails;
    private Button mSendOtp;

   private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Reference to the root of the db
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        //Adds data the child under root,where child is user1.
       // mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user1");

        mMessageDetails = findViewById(R.id.edt_message_details);
        mSendOtp = findViewById(R.id.bt_send_otp);

        final String otpStr = getRandomNumberString();
        mMessageDetails.setText("“Hi. Your OTP is: " +
                otpStr);

        mSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mDatabaseRef.child("contactName").setValue(getIntent().getStringExtra("FULL_NAME"));
                //Adds child in the main root of the db
                //mDatabaseRef.child("otp").setValue(otpStr);

                //Adds child in randomly generated key under root of db ,this random key contains timestamp
                //ie time when data was added the db.

               // mDatabaseRef.push().setValue(otpStr);

                //To store multiple data we use hasmap

                HashMap<String,String> userData = new HashMap<>();
                userData.put("contactName",getIntent().getStringExtra("FULL_NAME"));
                userData.put("otp",otpStr);
                mDatabaseRef.push().setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SendMessageActivity.this, "Otp Sent successfull!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SendMessageActivity.this,"Some error occured,please try again!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

               //To Navigate to first activity of the app ,add this code and in manifest file as well

                Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity( intent );

            }
        });


       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
