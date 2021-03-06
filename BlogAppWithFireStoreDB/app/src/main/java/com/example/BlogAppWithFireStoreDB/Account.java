package com.example.BlogAppWithFireStoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.BlogAppWithFireStoreDB.Fragments.AccountFragment;
import com.example.BlogAppWithFireStoreDB.Fragments.HomeFragment;
import com.example.BlogAppWithFireStoreDB.Fragments.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Account extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

   // private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private Toolbar toolbarBlogLists;
    private String currentUser;
    private FirebaseFirestore mFirestoreRef;
    private BottomNavigationView mainBottomNav;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbarBlogLists = findViewById(R.id.toolbar_blog_lists);
        setSupportActionBar(toolbarBlogLists);
        getSupportActionBar().setTitle("Blog Lists");

        mFirestoreRef = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blog");
        mDatabaseRef.keepSynced(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()== null){
                    startActivity(new Intent(Account.this,MainActivity.class));
                }
            }
        };


        //Fragments and bottom view intialization..
        mainBottomNav = findViewById(R.id.main_Bottom_Nav);
        homeFragment = new HomeFragment();
        accountFragment = new AccountFragment();
        notificationFragment = new NotificationFragment();
        replaceFragment(homeFragment);


        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottom_action_notifications:
                        replaceFragment(notificationFragment);
                        return true;
                    case R.id.bottom_action_account:
                        replaceFragment(accountFragment);
                        return true;

                        default:
                            return false;

                }

            }
        });






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem addBlogItem = menu.findItem(R.id.action_add_blog);
        addBlogItem.setVisible(true);
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

        if(id == R.id.action_add_blog){
            addBlog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void accountSetup() {
        Intent i = new Intent(Account.this,AccountSetup.class);
        startActivity(i);
        //finish();
    }
    private void addBlog() {
        Intent i = new Intent(Account.this,AddBlog.class);
        startActivity(i);
    }

    private void logout() {
        mAuth.signOut();
        finish();
        Intent i = new Intent(Account.this,Login.class);
        //If set, this activity will become the start of a new task on this history stack.
       // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //If set in an Intent passed to Context#startActivity, this flag will cause any
        // existing task that would be associated with the activity to be cleared before the activity is started.
       // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      //  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        adapter.startListening();

       currentUser = mAuth.getCurrentUser().getUid();
       if(currentUser != null) {
           mFirestoreRef.collection("Users").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().exists()){
                        Intent setUpIntent = new Intent(Account.this,AccountSetup.class);
                        startActivity(setUpIntent);
                        finish();
                    }/*else{
                        String error = String.valueOf(task.getException());
                        Toast.makeText(Account.this,"Firestore error: "+error,Toast.LENGTH_LONG).show();
                    }*/
                }
               }
           });
       }
    }
    @Override
    protected void onStop() {
        super.onStop();
     //   adapter.stopListening();
    }
   /* public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView =itemView ;
        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.txt_blog_title);
            post_title.setText(title);
        }

        public void setDescp(String descp){
            TextView post_descp = (TextView) mView.findViewById(R.id.txt_blog_descp);
            post_descp.setText(descp);
        }
        public void setImage(String imgurl){
            ImageView post_img =  mView.findViewById(R.id.img_blog);
            Picasso.get().load(imgurl).into(post_img);
        }

    }*/
  /*  private void fetch() {

        //This will get all blogs of all users
        *//*Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blog");*//*
        String currentUser = mAuth.getCurrentUser().getUid();
        //Users specific blogs
       Query query = mDatabaseRef.orderByChild("userid").equalTo(currentUser);

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(query, new SnapshotParser<Blog>() {
                            @NonNull
                            @Override
                            public Blog parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Blog(
                                        snapshot.child("title").getValue().toString(),
                                        snapshot.child("imageurl").getValue().toString(),
                                        snapshot.child("description").getValue().toString()

                                        );


                            }
                        })
                        .build();
        adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_list, parent, false);

                return new BlogViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(BlogViewHolder holder, final int position, Blog blog) {
                holder.setTitle(blog.getTitle());
                holder.setDescp(blog.getDescription());
                holder.setImage(blog.getImageurl());
            }

        };
        recyclerViewBlogList.setAdapter(adapter);
    }*/


    //Method to replace fragment inside container...
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }
}

