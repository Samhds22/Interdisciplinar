package com.ceunsp.app.projeto.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.R;


import java.util.List;

public class CollegeClassAdapter extends RecyclerView.Adapter<CollegeClassAdapter.MyViewHolder> {

    private List<CollegeClass> collegeClassesList;
    public CollegeClassAdapter(List<CollegeClass> list){
        this.collegeClassesList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_class, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CollegeClass collegeClass = collegeClassesList.get(position);

        holder.collegeClassNameEdit.setText(collegeClass.getClassName());
        String creator = "Criado por: " + collegeClass.getCreator() + " em " + collegeClass.getCreationDate();
        holder.creatorEdit.setText(creator);
    }

    @Override
    public int getItemCount() {
        return collegeClassesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView collegeClassNameEdit;
        private TextView creatorEdit;

        public MyViewHolder(View itemView) {
            super(itemView);

            collegeClassNameEdit = itemView.findViewById(R.id.class_name_card_edit);
            creatorEdit = itemView.findViewById(R.id.creator_text);

        }
    }

}
