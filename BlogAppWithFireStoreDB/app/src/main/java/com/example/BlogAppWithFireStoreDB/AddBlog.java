package com.example.BlogAppWithFireStoreDB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AddBlog extends AppCompatActivity {

   // private static final int MAX_LENGTH = 200;
    // private ImageButton imgBlogImage;
   private ImageView imgBlogImage;
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
    private FirebaseFirestore mFirestoreRef;
    private String currentUser;
    private Bitmap compressedImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        toolbarAddBlogs = findViewById(R.id.toolbar_add_blog);
        setSupportActionBar(toolbarAddBlogs);
        getSupportActionBar().setTitle("Add Blogs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFirestoreRef = FirebaseFirestore.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseRef.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

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

                imagePicker();
            }
        });
    }
    private void imagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(1,1)
                .start(AddBlog.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
   /* public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/
    private void submitBlogPost() {

  //      mProgressBar.setVisibility(View.VISIBLE);
        final String title = edtPostTitle.getText().toString().trim();
        final String descp = edtPostDescription.getText().toString().trim();
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(descp) && mImgUri != null){
            mProgressBar.setVisibility(View.VISIBLE);
           // StorageReference  filePath = mStorageRef.child("Blog_Images").child(mImgUri.getLastPathSegment());


            final String randomName = UUID.randomUUID().toString();
            StorageReference  filePath = mStorageRef.child("Blog_Images").child(randomName + ".jpg");

            filePath.putFile(mImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUri = uri.toString();
                            Map<String,Object> postMap = new HashMap<>();
                            postMap.put("imageuri",imgUri);
                            postMap.put("title",title);
                            postMap.put("description",descp);
                            postMap.put("userid",currentUser);
                            postMap.put("timesstamp", FieldValue.serverTimestamp());
                           mFirestoreRef.collection("BlogPosts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddBlog.this,"Blog posts added successfully!!",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddBlog.this,Account.class));
                                    finish();
                                }else{
                                    String error = task.getException().toString();
                                    Toast.makeText(AddBlog.this,"Firestore error: "+error,Toast.LENGTH_LONG).show();

                                }
                                   mProgressBar.setVisibility(View.INVISIBLE);

                               }
                           });
                        }
                    });

                   /* mProgressBar.setVisibility(View.INVISIBLE);
                    finishAffinity();
                    startActivity(new Intent(AddBlog.this,Account.class));*/

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.INVISIBLE);
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImgUri = result.getUri();
                imgBlogImage.setImageURI(mImgUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

            accountSetup();
        }
        if (id == R.id.action_search) {
            return true;
        }
        if(id == R.id.action_logout){
            logout();
        }


        return super.onOptionsItemSelected(item);
    }

    private void accountSetup() {
        Intent i = new Intent(AddBlog.this,AccountSetup.class);
        startActivity(i);
        //finish();
    }

    private void logout() {
        mAuth.signOut();
       // finishAffinity();
        Intent i = new Intent(AddBlog.this,Login.class);
        startActivity(i);
        finish();

    }
}
