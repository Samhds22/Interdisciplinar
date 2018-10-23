package com.ceunsp.app.projeto.Helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceunsp.app.projeto.Fragments.Tab2;
import com.ceunsp.app.projeto.Model.CollegeClass;
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

        Historic historic = historicList.get(position);
        Bitmap profileImage = null;
        String message = "";

      /*  if (position == (historicList.size()-1)){
            Tab2 tab2 = new Tab2();
            tab2.loadData();
        }*/
        if (historic.getAction().equals("create")){

            profileImage      = historic.getImgProfile();
            String usertype   = historic.getUserType();
            String fullName   = historic.getUserFullName();
            String eventTitle = historic.getEventTitle();
            String eventType  = historic.getEventType();

            message = fullName + " adicionou o compromisso " + eventTitle + " á agenda";
        }

        holder.actionText.setText(message);
        holder.dateText.setText(historic.getDate());
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

