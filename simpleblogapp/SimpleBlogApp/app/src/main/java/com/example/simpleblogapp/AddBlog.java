package com.example.simpleblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddBlog extends AppCompatActivity {

    private ImageButton imgBlogImage;
    private EditText edtPostTitle,edtPostDescription;
    private Button btSubmitPost;
    private static final int GALLERY_REQUEST = 2;
    private Uri mImgUri = null;
    private StorageReference mStorageRef;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar toolbarAddBlogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        toolbarAddBlogs = findViewById(R.id.toolbar_add_blog);
        setSupportActionBar(toolbarAddBlogs);
        getSupportActionBar().setTitle("Add Blogs");


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseRef.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    if(firebaseAuth.getCurrentUser()!=null){
                        Intent intent = new Intent(AddBlog.this,MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);

                    }
                }
            }
        };


        mProgressBar = findViewById(R.id.progress_horizontal_add_blog);

        edtPostTitle = findViewById(R.id.edt_title);
        edtPostDescription = findViewById(R.id.edt_description);
        btSubmitPost = findViewById(R.id.bt_post);
        btSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBlogPost();
            }
        });


        imgBlogImage = findViewById(R.id.img_blog_image);
        imgBlogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void submitBlogPost() {

        mProgressBar.setVisibility(View.VISIBLE);
        final String title = edtPostTitle.getText().toString().trim();
        final String descp = edtPostDescription.getText().toString().trim();
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(descp) && mImgUri != null){
            StorageReference  filePath = mStorageRef.child("Blog_Images").child(mImgUri.getLastPathSegment());

            filePath.putFile(mImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUri = uri.toString();
                            DatabaseReference newPost = mDatabaseRef.push();
                            newPost.child("title").setValue(title);
                            newPost.child("description").setValue(descp);
                            newPost.child("imageurl").setValue(imgUri);
                            newPost.child("userid").setValue(mAuth.getCurrentUser().getUid());
                        }
                    });

                    mProgressBar.setVisibility(View.INVISIBLE);
                    finishAffinity();
                    startActivity(new Intent(AddBlog.this,Account.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("AddBlog",e.getMessage().toString());
                }
            });
        }
        else {
            Toast.makeText(AddBlog.this,"All fields are required",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode ==RESULT_OK){
            mImgUri = data.getData();
            //imgBlogImage.setImageURI(imageUri);
            Picasso.get().load(mImgUri).into(imgBlogImage);
        }
    }
    @Override
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
        if (id == R.id.action_search) {
            return true;
        }
        if(id == R.id.action_logout){
            logout();
        }


        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
       // finishAffinity();
        Intent i = new Intent(AddBlog.this,Login.class);
        startActivity(i);
        finish();

    }
}
