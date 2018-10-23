package com.ceunsp.app.projeto.Helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceunsp.app.projeto.Model.Historic;
import com.ceunsp.app.projeto.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.MyViewHolder> {

    private List<Historic> historicList;

    public HistoricAdapter(List<Historic> list){
        this.historicList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_historic, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Historic historic   = historicList.get(position);
        Bitmap profileImage = historic.getImgProfile();
        String fullName     = historic.getUserFullName();
        String eventTitle   = historic.getEventTitle();
        String eventType    = historic.getEventType();
        String usertype     = historic.getUserType();
        String date         = historic.getDate() + " ás " + historic.getHour();
        String message      = "";

        if (historic.getAction().equals("create")){

            message = fullName + " adicionou o compromisso " + eventTitle + " á agenda";

        }else if (historic.getAction().equals("update")){

            message = fullName + " alterou o compromisso " + eventTitle + " na agenda";

        }else if (historic.getAction().equals("enterTheClass")){

            message = fullName + " acaba de entrar para a turma!";
        }

        holder.actionText.setText(message);
        holder.dateText.setText(date);
        holder.profileImage.setImageBitmap(profileImage);
    }

    @Override
    public int getItemCount() {
        return historicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView actionText;
        private TextView dateText;


        public MyViewHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.historic_profileImage);
            actionText = itemView.findViewById(R.id.action_TextView);
            dateText = itemView.findViewById(R.id.date_textView);

        }
    }

}

