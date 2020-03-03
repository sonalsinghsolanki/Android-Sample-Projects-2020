package com.example.BlogAppWithFireStoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    private Toolbar toolbarComments;
    private EditText edt_comments;
    private ImageView img_comment_btn;
    private String blog_post_id;
    private FirebaseFirestore mFirebaseRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        toolbarComments = findViewById(R.id.toolbar_comments);
        setSupportActionBar(toolbarComments);
        getSupportActionBar().setTitle("Comments");

        mFirebaseRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        edt_comments = findViewById(R.id.comment_field);
        img_comment_btn = findViewById(R.id.comment_post_btn);

        blog_post_id = getIntent().getStringExtra("blog_post_id");

        img_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = edt_comments.getText().toString();
                if(!comment.isEmpty()){
                    Map<String,Object > commentsMap = new HashMap<>();
                    commentsMap.put("message",comment);
                    commentsMap.put("user_id",currentUserId);
                    commentsMap.put("timesstamp", FieldValue.serverTimestamp());

                mFirebaseRef.collection("BlogPosts/" + blog_post_id + "/Comments").add(commentsMap).
                        addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CommentsActivity.this,"Error posting comments: "
                                    +task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }else{
                            edt_comments.setText("");
                        }
                    }
                });
                }
            }
        });

    }
}
