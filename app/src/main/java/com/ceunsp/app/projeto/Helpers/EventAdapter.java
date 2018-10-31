package com.ceunsp.app.projeto.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceunsp.app.projeto.Model.EventData;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Event> eventList;


    public EventAdapter(List<Event> list){
        this.eventList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_event, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Event event = eventList.get(position);
        EventData eventData = (EventData) event.getData();

        holder.titleText.setText(eventData.getTitle());

        String eventType = eventData.getEventType();

        if (eventType.equals("Prova")){
            holder.typeText.setBackgroundResource(R.drawable.border_rounded_yellow);
        } else if (eventType.equals("Trabalho")){
            holder.typeText.setBackgroundResource(R.drawable.border_rounded_blue);
        } else if (eventType.equals("Projeto")){
            holder.typeText.setBackgroundResource(R.drawable.border_rounded_green);
        } else if (eventType.equals("Outros")){
            holder.typeText.setBackgroundResource(R.drawable.border_rounded_skyblue);
        }

        holder.typeText.setText(eventData.getEventType());

        holder.classNameText.setText(eventData.getClassName());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView titleText;
        private TextView typeText;
        private TextView classNameText;

        public MyViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.event_title_text);
            typeText = itemView.findViewById(R.id.event_type_text);
            classNameText = itemView.findViewById(R.id.className_edit);
        }
    }

}

