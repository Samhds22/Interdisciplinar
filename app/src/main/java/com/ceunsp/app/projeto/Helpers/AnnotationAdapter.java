package com.ceunsp.app.projeto.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceunsp.app.projeto.Model.Annotation;
import com.ceunsp.app.projeto.R;

import java.util.List;

public class AnnotationAdapter extends RecyclerView.Adapter<AnnotationAdapter.MyViewHolder> {

    private List<Annotation> listaAnotacaolistas;

    public AnnotationAdapter(List<Annotation> lista) {

        this.listaAnotacaolistas = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_annotation, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Annotation anotacaolista = listaAnotacaolistas.get( position );
        holder.titulo.setText( anotacaolista.getTituloAnotacao() );
        holder.desc.setText(anotacaolista.getDescAnotacao());

    }

    @Override
    public int getItemCount() {

        return listaAnotacaolistas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView titulo;
        private TextView desc;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            desc = itemView.findViewById(R.id.AnotacaoDesc);

        }
    }

}
