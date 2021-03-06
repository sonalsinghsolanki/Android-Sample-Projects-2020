package com.example.BlogAppWithFireStoreDB.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.BlogAppWithFireStoreDB.Adapter.BlogRecylerAdapter;
import com.example.BlogAppWithFireStoreDB.Model.Blog;
import com.example.BlogAppWithFireStoreDB.Model.BlogUsers;
import com.example.BlogAppWithFireStoreDB.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment" ;
    private RecyclerView recyclerViewBlogList;
    private List<Blog> blog_list;
    FirebaseFirestore mFirestoreRef;
    private BlogRecylerAdapter blogRecylerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private FirebaseAuth mAuth;
    private List<BlogUsers> users_lists;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewBlogList = view.findViewById(R.id.recyclerview_blog_list);

        mAuth = FirebaseAuth.getInstance();

        blog_list = new ArrayList<>();
        users_lists = new ArrayList<>();

        mFirestoreRef = FirebaseFirestore.getInstance();

        blogRecylerAdapter = new BlogRecylerAdapter(blog_list,users_lists);
        recyclerViewBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewBlogList.setAdapter(blogRecylerAdapter);
        recyclerViewBlogList.setHasFixedSize(true);

        if(mAuth.getCurrentUser() != null) {
            //To restrict data per screen in recycleview
            recyclerViewBlogList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isReachedBottom = !recyclerView.canScrollVertically(1);
                    if (isReachedBottom) {
                        String desc = lastVisible.getString("description");
                        Toast.makeText(container.getContext(), "Reached : " + desc, Toast.LENGTH_LONG).show();
                        loadMorePost();
                    }
                }
            });
        }
        //shows 3 blog posts per page and order by date post is added so latest posts will come on top of page..
        Query firstQuery = mFirestoreRef.collection("BlogPosts").orderBy("timesstamp",Query.Direction.DESCENDING)
                .limit(3);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error : " + e.getMessage());
                }

                if (!queryDocumentSnapshots.isEmpty())
                {
                    if(isFirstPageFirstLoad)
                    {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        blog_list.clear();
                        users_lists.clear();
                    }
                for (DocumentChange documentChange:queryDocumentSnapshots.getDocumentChanges()){
                    //This will tell us document change if data is added..
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        String blogPostId = documentChange.getDocument().getId();


                        //get blog id and pass thats is using extended class blogPostId..
                        Blog blog = documentChange.getDocument().toObject(Blog.class).withId(blogPostId);

                        String blogUserId =documentChange.getDocument().getString("userid");
                        mFirestoreRef.collection("Users").document(blogUserId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    BlogUsers users = task.getResult().toObject(BlogUsers.class);

                                    if(isFirstPageFirstLoad)
                                    {
                                        users_lists.add(users);
                                        blog_list.add(blog);
                                    }
                                    else{
                                        users_lists.add(0,users);
                                        blog_list.add(0,blog);
                                    }
                                    blogRecylerAdapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(getContext(),"Error retreiving user data: "+
                                            task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
                isFirstPageFirstLoad = false;
            }
            }
        });


        return view;

    }
    private void loadMorePost() {
        if (mAuth.getCurrentUser() != null) {
            //shows next blog posts per page and order by date post is added so latest posts will come on top of page..
            Query nextQuery = mFirestoreRef.collection("BlogPosts").orderBy("timesstamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
            //To stop the listener if we minimize the activity to avoid crash we need tp ass getActivity so that
            // it attaches to current activity and work as per activity life cycle...in all addsnapshotListener as they
            //are real time queries

            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d("", "Error : " + e.getMessage());
                    }
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                            //This will tell us document change if data is added..
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String blogPostId = documentChange.getDocument().getId();

                                //get blog id and pass thats is using extended class blogPostId..
                                Blog blog = documentChange.getDocument().toObject(Blog.class).withId(blogPostId);

                               /* blog_list.add(blog);
                                blogRecylerAdapter.notifyDataSetChanged();*/

                                String blogUserId =documentChange.getDocument().getString("userid");
                                mFirestoreRef.collection("Users").document(blogUserId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    BlogUsers users = task.getResult().toObject(BlogUsers.class);

                                                    /*if(isFirstPageFirstLoad)
                                                    {*/
                                                        users_lists.add(users);
                                                        blog_list.add(blog);
                                                   /*  }
                                                   else{
                                                        users_lists.add(0,users);
                                                        blog_list.add(0,blog);
                                                    }*/
                                                    blogRecylerAdapter.notifyDataSetChanged();
                                                }else{
                                                    Toast.makeText(getContext(),"Error retreiving user data: "+
                                                            task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        }
    }
}
