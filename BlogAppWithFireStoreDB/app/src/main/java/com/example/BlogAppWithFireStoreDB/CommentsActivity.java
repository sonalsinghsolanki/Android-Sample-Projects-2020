package com.example.BlogAppWithFireStoreDB;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.BlogAppWithFireStoreDB.Adapter.CommentsRecyclerAdapter;
import com.example.BlogAppWithFireStoreDB.Model.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



    public class CommentsActivity extends AppCompatActivity {

        private Toolbar toolbarComments;
        private EditText edt_comments;
        private ImageView img_comment_btn;
        private String blog_post_id;

        private FirebaseFirestore mFireStoreRef;
        private FirebaseAuth mAuth;
        private String currentUserId;
        private RecyclerView recyclerView_comments;
        //private CommentsRecyclerAdapter commentsRecyclerAdapter;
        private List<Comments> commentsList;
        private CommentsRecyclerAdapter commentsRecyclerAdapter;

        private FirebaseFirestore mFirebaseRef;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_comments);
            toolbarComments = findViewById(R.id.toolbar_comments);
            setSupportActionBar(toolbarComments);
            getSupportActionBar().setTitle("Comments");


            mFireStoreRef = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUserId = mAuth.getCurrentUser().getUid();
            blog_post_id = getIntent().getStringExtra("blog_post_id");

            mFirebaseRef = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUserId = mAuth.getCurrentUser().getUid();
            blog_post_id = getIntent().getStringExtra("blog_post_id");

            edt_comments = findViewById(R.id.comment_field);
            img_comment_btn = findViewById(R.id.comment_post_btn);

            recyclerView_comments = findViewById(R.id.recyclerview_comment_lists);

            //recyclerview firestore lists
            commentsList = new ArrayList<>();
            commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
            recyclerView_comments.setHasFixedSize(true);
            recyclerView_comments.setLayoutManager(new LinearLayoutManager(this));
            recyclerView_comments.setAdapter(commentsRecyclerAdapter);

            //retrive comments realtime..
            //we add activity name in addsnapshotlistener so that if user press back button or closes the activity
            //the listener stops immediately as activity is killed..

            mFireStoreRef.collection("BlogPosts/" + blog_post_id + "/Comments")
                    .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (!queryDocumentSnapshots.isEmpty()) {

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String commentId = doc.getDocument().getId();
                                        Comments comments = doc.getDocument().toObject(Comments.class);
                                        commentsList.add(comments);
                                        commentsRecyclerAdapter.notifyDataSetChanged();


                                    }
                                }

                            }
                        }
                    });




            img_comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment = edt_comments.getText().toString();
                    if (!comment.isEmpty()) {
                        Map<String, Object> commentsMap = new HashMap<>();
                        commentsMap.put("message", comment);
                        commentsMap.put("user_id", currentUserId);
                        commentsMap.put("timesstamp", FieldValue.serverTimestamp());


                        mFirebaseRef.collection("BlogPosts/" + blog_post_id + "/Comments").add(commentsMap).

                                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(CommentsActivity.this, "Error posting comments: "
                                                    + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            edt_comments.setText("");
                                        }
                                    }
                                });
                    }
                }
            });

        }
    }
