package com.example.simpleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Account extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView recyclerViewBlogList;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private Toolbar toolbarBlogLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbarBlogLists = findViewById(R.id.toolbar_blog_lists);
        setSupportActionBar(toolbarBlogLists);
        getSupportActionBar().setTitle("Blog Lists");

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

        recyclerViewBlogList = findViewById(R.id.recyclerview_blog_list);
       // recyclerViewBlogList.setHasFixedSize(true);
        recyclerViewBlogList.setLayoutManager(new LinearLayoutManager(this));
        fetch();
       // adapter.notifyDataSetChanged();


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
        finishAffinity();
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
        adapter.startListening();

    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

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

    }
    private void fetch() {

        //This will get all blogs of all users
        /*Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blog");*/
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
    }
}

