package com.ceunsp.app.projeto.Calendar.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import com.ceunsp.app.projeto.Calendar.Model.EventData;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private EditText dateEventEdit, timeEventEdit, titleEventEdit, annotationEdit;
    final TimePicker timePicker = null;
    private Button saveButton;
    private Calendar calendar;
    private String userClassID;
    private String userID;

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassCalendar");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateEventEdit  = findViewById(R.id.date_event_edit);
        timeEventEdit  = findViewById(R.id.time_event_edit);
        titleEventEdit = findViewById(R.id.title_event_edit);
        annotationEdit = findViewById(R.id.annotation_edit);
        saveButton     = findViewById(R.id.save_button);
        calendar       = Calendar.getInstance();

        final Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()){
            dateEventEdit.setText(LoadDate(bundle));
            RetrieveDate(bundle);
            userClassID = bundle.getString("userClassID");
            userID = bundle.getString("userID");
        }

        dateEventEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
                dateEventEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(EventActivity.this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                if (dateEventEdit.hasFocus()) {
                    dateEventEdit.performClick();
                }
            }
            private void updateLabel(){
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                dateEventEdit.setText(sdf.format(calendar.getTime()));
            }
        });


        timeEventEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        updateLabel();
                    }
                };
                timeEventEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(EventActivity.this, time, calendar
                                .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) , true).show();
                    }
                });
                if (timeEventEdit.hasFocus()) {
                    timeEventEdit.performClick();
                }
            }
            private void updateLabel(){
                String selectedTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                timeEventEdit.setText(selectedTime);
            }
        });
        timeEventEdit.performClick();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventTitle = titleEventEdit.getText().toString();
                String annotation = annotationEdit.getText().toString();

                if (!eventTitle.isEmpty()){

                    DatabaseReference pushKey = ref.child(userClassID).push();

                    EventData eventData = new EventData();
                    eventData.setType("Prova");
                    eventData.setTitle(eventTitle);
                    eventData.setAnnotation(annotation);
                    eventData.setCreatorID(userID);
                    eventData.setCreationDate(GetCurrentDate());

                    Event event = new Event(R.color.colorAccent, calendar.getTimeInMillis(), eventData);
                    pushKey.child("Event").setValue(event);
                    Snackbar.make(v, "Evento criado com sucesso!", Snackbar.LENGTH_LONG).show();
                    finish();

                }else if (eventTitle.isEmpty()) {
                    Snackbar.make(v, "Insira um titulo para continuar", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    public String LoadDate(Bundle bundle){
        Long dateTimeInMills = RetrieveDate(bundle);
        calendar.setTimeInMillis(dateTimeInMills);
        Date date = calendar.getTime();
        return ConvertDate(date);
    }

    public Long RetrieveDate(Bundle bundle){
        bundle = getIntent().getExtras();
        Long timeInMillis = bundle.getLong("date");
        return timeInMillis;
    }

    public String ConvertDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
            String convertedDate = sdf.format(date);
            return convertedDate;
    }

    public String GetCurrentDate(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        String date = sdf.format(calendar.getTime());
        return date;
    }
}

