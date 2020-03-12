package com.example.BlogAppWithFireStoreDB.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.BlogAppWithFireStoreDB.CommentsActivity;
import com.example.BlogAppWithFireStoreDB.Model.Blog;
import com.example.BlogAppWithFireStoreDB.Model.BlogUsers;
import com.example.BlogAppWithFireStoreDB.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecylerAdapter extends RecyclerView.Adapter<BlogRecylerAdapter.ViewHolder> {

    private List<Blog>blogLists;
    private List<BlogUsers>userLists;
    private Context context;
    private FirebaseFirestore mFirestoreRef;
    private FirebaseAuth mAuth;


    public BlogRecylerAdapter(List<Blog> blog_lists, List<BlogUsers> users_lists){

        this.blogLists = blog_lists;
        this.userLists = users_lists;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_list, parent, false);
        context = parent.getContext();
        mFirestoreRef  = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //for some performance issue we used this code..
        holder.setIsRecyclable(false);

        String blog_desp = blogLists.get(position).getDescription();
        String blog_title = blogLists.get(position).getTitle();
        String blog_user_id = blogLists.get(position).getUserid();
        String blog_id = blogLists.get(position).blogPostId ;
        String current_user_id = mAuth.getCurrentUser().getUid();
        String blog_user_name = userLists.get(position).getName();
        String  blog_user_img= userLists.get(position).getImage();
        holder.setUserDate(blog_user_img,blog_user_name);

       /* //user data will be retreived..
        mFirestoreRef.collection("Users").document(blog_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String blog_user_name = task.getResult().getString("name");
                    String  blog_user_img= task.getResult().getString("image");
                    holder.setUserDate(blog_user_img,blog_user_name);
                }else{
                    String error = String.valueOf(task.getException());
                    Toast.makeText(context,"Firestore error: "+error, Toast.LENGTH_LONG).show();
                }
            }
        });*/

       try {
           long milliseconds = blogLists.get(position).getTimesstamp().getTime();

           String blog_date = new SimpleDateFormat("MM/dd/YY").format(milliseconds);
           holder.setBlogPostDate(blog_date);
       }catch (Exception e){
           Toast.makeText(context,"Firestore error: "+e.toString(), Toast.LENGTH_SHORT).show();
       }
            String blog_img =  blogLists.get(position).getImageuri();


      //  String blog_img_user =  blogLists.get(position).getImage_user();
        //holder.setUserImage(blog_img_user);
       // holder.setUserNa(blog_user_id);

        holder.setBlogImage(blog_img);
        holder.setTitle(blog_title);
        holder.setDescp(blog_desp);

        //get likes count
        mFirestoreRef.collection("BlogPosts/" + blog_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshot.isEmpty()){
                    int count = documentSnapshot.size();
                    holder.setLikesCount(count);
                }else{
                    holder.setLikesCount(0);
                }
            }
        });


    //change likes icon...
        mFirestoreRef.collection("BlogPosts/" + blog_id + "/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.post_like_img.setImageDrawable(context.getDrawable(R.drawable.action_like_ascent));
                    }
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.post_like_img.setImageDrawable(context.getDrawable(R.drawable.action_like_gray));
                    }

                }
            }
        });


                holder.post_like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestoreRef.collection("BlogPosts/" + blog_id + "/Likes").document(current_user_id).get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            //if likes doesnt exists with current user then add else delete existing likes..
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String , Object> likesMap = new HashMap<>();
                            likesMap.put("timesstamp", FieldValue.serverTimestamp());

                            mFirestoreRef.collection("BlogPosts/" + blog_id + "/Likes").document(current_user_id).set(likesMap);
                        }else{
                            mFirestoreRef.collection("BlogPosts/" + blog_id + "/Likes").document(current_user_id).delete();

                        }
                    }
                });

            }
        });

                holder.img_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent commentIntent = new Intent(context, CommentsActivity.class);
                        commentIntent.putExtra("blog_post_id", blog_id);
                        context.startActivity(commentIntent);
                    }
                });



    }

    @Override
    public int getItemCount() {
        return blogLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView post_like_img ,post_img_blog_comments;
        TextView post_like_counts;
        ImageView img_post_comments,img_comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView =itemView ;
            post_like_img= mView.findViewById(R.id.blog_like_btn);

            post_like_counts = mView.findViewById(R.id.txt_blog_like_count);

            img_comments = mView.findViewById(R.id.blog_comment_icon);
            img_post_comments = mView.findViewById(R.id.comment_post_btn);


        }
       /* public void setUserId(String username){
            TextView post_username = mView.findViewById(R.id.txt_username);
            post_username.setText(username);
        }
       public void setBlogLikeImage(String likeImage){
           ImageView post_like_img = mView.findViewById(R.id.blog_like_btn);
          // post_like_img.setImageURI(likeImage);
       }
        public void setBlogLikeCounts(String counts){
            TextView post_like_counts = mView.findViewById(R.id.txt_blog_like_count);
            post_like_counts.setText(counts);
        }*/
        public void setBlogPostDate(String date){
            TextView post_date = mView.findViewById(R.id.txt_blog_date);
            post_date.setText(date);
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.txt_blog_title);
            post_title.setText(title);
        }
        public void setLikesCount(int count){
            post_like_counts.setText(count+ " Likes");
        }

        public void setDescp(String descp){
            TextView post_descp =  mView.findViewById(R.id.txt_blog_description);
            post_descp.setText(descp);
        }
        public void setBlogImage(String imgurl){
            ImageView post_blog_img =  mView.findViewById(R.id.img_blog);
            Glide.with(context).load(imgurl).into(post_blog_img);
          //  Picasso.get().load(imgurl).into(post_blog_img);
        }
       public void setUserDate(String userimgurl,String username){
            CircleImageView post_user_img =  mView.findViewById(R.id.img_profile);
            TextView post_user_name = mView.findViewById(R.id.txt_username);
           RequestOptions placeHolderOptions = new RequestOptions();
           placeHolderOptions.placeholder(R.drawable.img_profile);
           Glide.with(context).applyDefaultRequestOptions(placeHolderOptions).load(userimgurl).into(post_user_img);
            //Picasso.get().load(userimgurl).into(post_user_img);
            post_user_name.setText(username);
        }
    }
}
