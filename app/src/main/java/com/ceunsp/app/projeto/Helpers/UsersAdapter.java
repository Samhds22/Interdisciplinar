package com.ceunsp.app.projeto.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private List<User> usersList;

    public UsersAdapter(List<User> list){
            this.usersList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_students, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User users = usersList.get(position);
        String fullName = users.getName() + " " + users.getLastName();
        holder.fullNameTextView.setText(fullName);
        holder.userTypeTextView.setText(users.getUserType());
        holder.profileImage.setImageBitmap(users.getImgProfile());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView fullNameTextView;
        private TextView userTypeTextView;

        private MyViewHolder(View itemView) {
            super(itemView);

            profileImage     = itemView.findViewById(R.id.student_profileImage);
            fullNameTextView = itemView.findViewById(R.id.user_fullName_TextView);
            userTypeTextView = itemView.findViewById(R.id.userType_textView);

        }
    }
}

