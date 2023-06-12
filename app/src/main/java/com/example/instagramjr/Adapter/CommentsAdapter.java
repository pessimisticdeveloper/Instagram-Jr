package com.example.instagramjr.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramjr.Model.Comments;
import com.example.instagramjr.Model.Users;
import com.example.instagramjr.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
    private Activity context;
    private List<Comments> commentsList;
    private List<Users> usersList;

    public CommentsAdapter(Activity context,List<Comments> commentsList,List<Users> usersList){
        this.context = context;
        this.commentsList = commentsList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_comments,parent,false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comments comments = commentsList.get(position);
        holder.setComment(comments.getComment());

        Users users = usersList.get(position);
        holder.setUsername(users.getName());
        holder.setCircleImageView(users.getImage());

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{
        TextView commentx, usernamex;
        CircleImageView circleImageView;
        View view;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
        public void setComment(String comment){
            commentx = view.findViewById(R.id.comment_tv);
            commentx.setText(comment);
        }

        public void setCircleImageView(String profilepic) {
            circleImageView = view.findViewById(R.id.profile_pic_comment);
            Glide.with(context).load(profilepic).into(circleImageView);

        }

        public void setUsername(String username) {
            usernamex = view.findViewById(R.id.username_tv_comment);
            usernamex.setText(username);
        }
    }
}
