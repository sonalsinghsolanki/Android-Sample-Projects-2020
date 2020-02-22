package com.example.simpleblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetup extends AppCompatActivity {

    private Toolbar toolbarAccountSetup;
    private CircleImageView imgProfile;
    private  Uri mResultUri = null;
    private EditText mEdtProfileName;
    private Button mBtSaveProfile;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
   private FirebaseFirestore mFirestoreRef;
    private DatabaseReference mDatabase;
    private ProgressBar mProgressBar;
    private String user_id ;
    private Boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        toolbarAccountSetup = findViewById(R.id.toolbar_account_setup);
        setSupportActionBar(toolbarAccountSetup);
        getSupportActionBar().setTitle("Account Setup");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage .getInstance().getReference();
        mFirestoreRef = FirebaseFirestore.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);


        mProgressBar = findViewById(R.id.progressBar_account_setup);
        mEdtProfileName = findViewById(R.id.edt_profile_name);
        mBtSaveProfile = findViewById(R.id.bt_save_profile);

        //Retreive user account details
        mProgressBar.setVisibility(View.VISIBLE);
        mBtSaveProfile.setEnabled(false);

        mFirestoreRef.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                       // mProgressBar.setVisibility(View.VISIBLE);
                        String userName = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        imgProfile.setImageURI(Uri.parse(image));

                        mEdtProfileName.setText(userName);
                        mResultUri = Uri.parse(image);

                        RequestOptions imagePlaceHolder = new RequestOptions();
                        imagePlaceHolder.placeholder(R.drawable.img_profile);

                        Glide.with(AccountSetup.this).setDefaultRequestOptions(imagePlaceHolder).load(image).into(imgProfile);

                    }else{
                      //  Toast.makeText(AccountSetup.this,"No data exists",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    String error = task.getException().toString();
                    Toast.makeText(AccountSetup.this,"Firestore error: "+error,Toast.LENGTH_LONG).show();
                }
                mProgressBar.setVisibility(View.INVISIBLE);
                mBtSaveProfile.setEnabled(true);
            }
        });

        mBtSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = mEdtProfileName.getText().toString();
                if(!TextUtils.isEmpty(userName) && mResultUri !=null){
                    mProgressBar.setVisibility(View.VISIBLE);

                if(isChanged){

                    StorageReference imageRef= mStorageRef.child("profileImage").child(user_id+".jpg");
                    imageRef.putFile(mResultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storeFirestoreData(taskSnapshot,userName);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountSetup.this,"Image Upload Error: "+e.toString(),Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.INVISIBLE);

                        }
                    });

                }else{

                    storeFirestoreData(null,userName);

                }


            }else{
                    Toast.makeText(AccountSetup.this,"Profile Name or Image is empty!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgProfile = findViewById(R.id.profile_img);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(AccountSetup.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(AccountSetup.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AccountSetup.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                       // Toast.makeText(AccountSetup.this,"You already have permission!",Toast.LENGTH_SHORT).show();
                       imagePicker();

                    }
                }
                else{
                    imagePicker();
                }
            }
        });


    }

    private void storeFirestoreData(UploadTask.TaskSnapshot taskSnapshot, final String uName) {

        if(taskSnapshot != null){
        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                                  /*code to store in realtime database
                                   String imgUri = uri.toString();
                                   DatabaseReference newPost = mDatabase.push();
                                   newPost.child("profileName").setValue(userName);
                                   newPost.child("imageurl").setValue(imgUri);
                                   newPost.child("userid").setValue(mAuth.getCurrentUser().getUid());*/

                /*This is a step to store user data in firestore db*/
                Map<String,String> userMap = new HashMap<>();
                userMap.put("name",uName);
                userMap.put("image",uri.toString());

                mFirestoreRef.collection("Users").document(user_id).
                        set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AccountSetup.this,"Account Updated!!",Toast.LENGTH_SHORT).show();

                           // finishAffinity();
                            startActivity(new Intent(AccountSetup.this,Account.class));
                            finish();
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(AccountSetup.this,"Firestore error: "+error,Toast.LENGTH_LONG).show();
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);


                    }

                });
            }
        });
        }else{

            Uri uri = mResultUri;
            /*This is a step to store user data in firestore db*/
            Map<String,String> userMap = new HashMap<>();
            userMap.put("name",uName);
            userMap.put("image",uri.toString());

            mFirestoreRef.collection("Users").document(user_id).
                    set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AccountSetup.this,"Account Updated!!",Toast.LENGTH_LONG).show();

                      //  finishAffinity();
                        startActivity(new Intent(AccountSetup.this,Account.class));
                        finish();
                    }else{
                        String error = task.getException().toString();
                        Toast.makeText(AccountSetup.this,"Firestore error: "+error,Toast.LENGTH_LONG).show();
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);

                }

            });

        }

    }

    private void imagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSetup.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mResultUri = result.getUri();
                imgProfile.setImageURI(mResultUri);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
