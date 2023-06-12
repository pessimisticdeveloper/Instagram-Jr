package com.example.instagramjr.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
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
import com.example.instagramjr.CommentsActivity;
import com.example.instagramjr.Model.Post;
import com.example.instagramjr.Model.Users;
import com.example.instagramjr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> list;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private List<Users> usersList;
    public PostAdapter(Activity context, List<Post> list,List<Users> usersList){
        this.list = list;
        this.context = context;
        this.usersList = usersList;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  v = LayoutInflater.from(context).inflate(R.layout.each_post,parent,false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = list.get(position);
        holder.setPostPic(post.getImage());
        holder.setPostCoption(post.getCaption());

        long milliSeconds = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyyy",new Date(milliSeconds)).toString();
        holder.setPostDate(date);

        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();

        holder.setPostPic(image);
        holder.setPostUsername(username);


        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.likePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String ,Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likesMap);
                        }else {
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });
        //beğenme renkleri
        firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    holder.likePic.setImageDrawable(context.getDrawable(R.drawable.baseline_favorite_24));
                }else {
                    holder.likePic.setImageDrawable(context.getDrawable(R.drawable.baseline_favorite_border_24));
                }
            }
        });

        firestore.collection("Posts/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (!value.isEmpty()){
                        int count = value.size();
                        holder.setPostLikes(count);
                    }else {
                        holder.setPostLikes(0);
                    }
                }
            }
        });
        holder.commentPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentsIntent = new Intent(context, CommentsActivity.class);
                commentsIntent.putExtra("postid",postId);
                context.startActivity(commentsIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic, commentPic,likePic;
        CircleImageView profilePic;
        TextView postUsername, postDate, postCoption, postLikes;
        View view;
        public PostViewHolder(@NonNull View itemView){
            super(itemView);
            view = itemView;
            likePic = view.findViewById(R.id.like_btn);
            commentPic = view.findViewById(R.id.comments_post);
        }

        public void setPostLikes(int count) {
            postLikes = view.findViewById(R.id.like_count_tv);
            postLikes.setText(count + " Beğendi");
        }

        public void setPostPic(String urlpost) {
            postPic = view.findViewById(R.id.user_post);
            Glide.with(context).load(urlpost).into(postPic);
        }

        public void setProfilePic(String urlprofile) {
            profilePic = view.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlprofile).into(profilePic);
        }

        public void setPostUsername(String username) {
            postUsername = view.findViewById(R.id.username_tv);
            postUsername.setText(username);
        }

        public void setPostDate(String date) {
            postDate = view.findViewById(R.id.datetv);
            postDate.setText(date);
        }

        public void setPostCoption(String caption) {
            postCoption = view.findViewById(R.id.caption_tv);
            postCoption.setText(caption);
        }
    }

}
