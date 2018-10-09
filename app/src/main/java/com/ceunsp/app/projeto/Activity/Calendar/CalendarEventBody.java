package com.ceunsp.app.projeto.Activity.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ceunsp.app.projeto.Model.Prova;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarEventBody extends AppCompatActivity {

    private EditText dateEventEdit, timeEventEdit, titleEventEdit;
    private Button saveButton;
    private Calendar calendar;
    final TimePicker timePicker = null;
    Long DateTimeInMillis;

    private final DatabaseReference classCalendarNodeRef = FirebaseDatabase.getInstance()
            .getReference("ClassCalendar");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_body);
        dateEventEdit = findViewById(R.id.date_event_edit);
        timeEventEdit = findViewById(R.id.time_event_edit);
        titleEventEdit = findViewById(R.id.title_event_edit);
        saveButton = findViewById(R.id.save_button);

        /*Carrega a data se a instancia for através do calendario*/
        final Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()){
            //dateEventEdit.setText(LoadDate(bundle));
            //DateTimeInMillis = RetrieveDate(bundle);
        }

        dateEventEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                calendar = Calendar.getInstance();
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
                        new DatePickerDialog(CalendarEventBody.this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                dateEventEdit.performClick();
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
                calendar = Calendar.getInstance();
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
                        new TimePickerDialog(CalendarEventBody.this, time, calendar
                                .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) , true).show();
                    }
                });
                timeEventEdit.performClick();
            }
            private void updateLabel(){
                String selectedTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                timeEventEdit.setText(selectedTime);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventTitle = titleEventEdit.getText().toString();

                if ((!eventTitle.isEmpty()) && (DateTimeInMillis != null)) {
                    Prova prova = new Prova();
                    prova.setType("Prova");
                    prova.setTitle(eventTitle);
                    prova.setTimeInMillis(DateTimeInMillis);
                    classCalendarNodeRef.push().setValue(prova);
                    finish();
                    Snackbar.make(v, "Evento criado com sucesso!", Snackbar.LENGTH_LONG).show();
                }else if (eventTitle.isEmpty()) {
                    Snackbar.make(v, "Insira o titulo para continuar", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    public String LoadDate(Bundle bundle){
        Long dateTimeInMills = RetrieveDate(bundle);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeInMills);
        String date = calendar.getTime().toString();
        return ConvertDate(date);
    }


    public Long RetrieveDate(Bundle bundle){
        bundle = getIntent().getExtras();
        Long timeInMillis = bundle.getLong("data");
        return timeInMillis;
    }

    public String ConvertDate(String date) {
        String formatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate, new Locale("pt", "BR"));
        String formattedDate = (simpleDateFormat.format(date));
        return formattedDate;
    }
}