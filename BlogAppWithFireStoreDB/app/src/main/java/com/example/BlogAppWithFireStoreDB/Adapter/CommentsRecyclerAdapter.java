    package com.example.BlogAppWithFireStoreDB.Adapter;
    import android.content.Context;
    import androidx.recyclerview.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.recyclerview.widget.RecyclerView;

    import com.example.BlogAppWithFireStoreDB.Model.Comments;
    import com.example.BlogAppWithFireStoreDB.R;

    import java.util.List;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

       public List<Comments> commentsList;
            public Context context;

            public CommentsRecyclerAdapter(List<Comments> commentsList){

                this.commentsList = commentsList;

            }

            @Override
            public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_lists, parent, false);
                context = parent.getContext();
                return new CommentsRecyclerAdapter.ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

                holder.setIsRecyclable(false);

                String commentMessage = commentsList.get(position).getMessage();
                holder.setComment_message(commentMessage);

            }


            @Override
            public int getItemCount() {

                if(commentsList != null) {

                    return commentsList.size();

                } else {

                    return 0;

                }

            }

            public class ViewHolder extends RecyclerView.ViewHolder {

                private View mView;

                private TextView comment_message;
                private CircleImageView img_profile;

                public ViewHolder(View itemView) {
                    super(itemView);
                    mView = itemView;
                }

                public void setComment_message(String message){

                    comment_message = mView.findViewById(R.id.comment_message);
                    comment_message.setText(message);

                }


            }

        }

