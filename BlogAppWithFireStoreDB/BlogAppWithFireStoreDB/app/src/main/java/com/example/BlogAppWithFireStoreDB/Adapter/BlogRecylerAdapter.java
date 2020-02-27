package com.example.BlogAppWithFireStoreDB.Adapter;

import android.content.Context;
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
import com.example.BlogAppWithFireStoreDB.Model.Blog;
import com.example.BlogAppWithFireStoreDB.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecylerAdapter extends RecyclerView.Adapter<BlogRecylerAdapter.ViewHolder> {

    private List<Blog>blogLists;
    private Context context;
    private FirebaseFirestore mFirestoreRef;


    public BlogRecylerAdapter(List<Blog>blog_lists){

        this.blogLists = blog_lists;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_list, parent, false);
        context = parent.getContext();
        mFirestoreRef  = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String blog_desp = blogLists.get(position).getDescription();
        String blog_title = blogLists.get(position).getTitle();
        String blog_user_id = blogLists.get(position).getUserid();

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
        });

        long milliseconds = blogLists.get(position).getTimesstamp().getTime();

        String blog_date = new SimpleDateFormat("MM/dd/YY").format(milliseconds);
            String blog_img =  blogLists.get(position).getImageUri();
      //  String blog_img_user =  blogLists.get(position).getImage_user();
        //holder.setUserImage(blog_img_user);
       // holder.setUserNa(blog_user_id);
        holder.setBlogPostDate(blog_date);
        holder.setBlogImage(blog_img);
        holder.setTitle(blog_title);
        holder.setDescp(blog_desp);
    }

    @Override
    public int getItemCount() {
        return blogLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView =itemView ;
        }
       /* public void setUserId(String username){
            TextView post_username = mView.findViewById(R.id.txt_username);
            post_username.setText(username);
        }*/
        public void setBlogPostDate(String date){
            TextView post_date = mView.findViewById(R.id.txt_blog_date);
            post_date.setText(date);
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.txt_blog_title);
            post_title.setText(title);
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
