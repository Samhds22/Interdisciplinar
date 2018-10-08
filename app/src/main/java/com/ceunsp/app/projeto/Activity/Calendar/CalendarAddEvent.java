package com.ceunsp.app.projeto.Activity.Calendar;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ceunsp.app.projeto.Model.Prova;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAddEvent extends AppCompatActivity {

    private EditText dateEventEdit, titleEventEdit;
    private Button saveButton;
    private CompactCalendarView compactCalendarView;
    private Event event;
    private Long timeInMillis;
    private List<Event> eventList = new ArrayList<Event>();
    private final String NODE_TITLE = "title";

    private final DatabaseReference classCalendarNodeRef = FirebaseDatabase.getInstance()
            .getReference("ClassCalendar");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add_event);
        dateEventEdit = findViewById(R.id.date_event_edit);
        titleEventEdit = findViewById(R.id.title_event_edit);
        saveButton = findViewById(R.id.save_button);

        CarregaData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventTitle = titleEventEdit.getText().toString();

                if ((!eventTitle.isEmpty()) && (timeInMillis != null)) {
                    Prova prova = new Prova();
                    prova.setType("Prova");
                    prova.setTitle(eventTitle);
                    prova.setTimeInMillis(timeInMillis);
                    classCalendarNodeRef.push().setValue(prova);
                    finish();
                    Snackbar.make(v, "Evento criado com sucesso!", Snackbar.LENGTH_LONG).show();
                }else if (eventTitle.isEmpty()) {
                    Snackbar.make(v, "Insira o titulo para continuar", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    public void CarregaData(){

        Bundle bundle = getIntent().getExtras();
        timeInMillis = bundle.getLong("data");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        String date = calendar.getTime().toString();
        dateEventEdit.setText(date);
    }
}