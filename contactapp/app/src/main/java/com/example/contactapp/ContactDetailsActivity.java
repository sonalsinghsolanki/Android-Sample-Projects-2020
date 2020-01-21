package com.example.contactapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView mContactName,mPhoneNo;
    private Button mSendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContactName = findViewById(R.id.txt_contact_name);
        mPhoneNo = findViewById(R.id.txt_phone_no);
        mSendMessage = findViewById(R.id.bt_send_message);

        mContactName.setText(getIntent().getStringExtra("FULL_NAME"));
        mPhoneNo.setText(getIntent().getStringExtra("PHONE_NO"));


        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(ContactDetailsActivity.this,SendMessageActivity.class);
                i.putExtra("FULL_NAME", getIntent().getStringExtra("FULL_NAME"));
                i.putExtra("PHONE_NO",getIntent().getStringExtra("PHONE_NO"));
                startActivity(i);
            }
        });
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
