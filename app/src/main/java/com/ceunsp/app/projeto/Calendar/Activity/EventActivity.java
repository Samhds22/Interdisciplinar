package com.ceunsp.app.projeto.Calendar.Activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import com.ceunsp.app.projeto.Calendar.Model.EventData;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private EditText dateEventEdit, subjectEdit, titleEventEdit, annotationEdit;
    private Spinner typeSpinner;
    private Calendar calendar;
    private String userClassID;
    private String userID;

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassCalendar");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEventEdit = findViewById(R.id.title_event_edit);
        dateEventEdit  = findViewById(R.id.date_event_edit);
        subjectEdit    = findViewById(R.id.subject_edit);
        typeSpinner    = findViewById(R.id.type_spinner);
        annotationEdit = findViewById(R.id.annotation_edit);
        calendar       = Calendar.getInstance();
        userID         = firebaseHelper.getUserID();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (Objects.equals(bundle.getString("operation"), "create")){

                dateEventEdit.setText(loadDate());
                userClassID = bundle.getString("userClassID");
                setSpinner(null);

            } else if (Objects.equals(bundle.getString("operation"), "view")){

                userClassID = bundle.getString("userClassID");
                titleEventEdit.setText(bundle.getString("title"));
                dateEventEdit.setText(loadDate());
                subjectEdit.setText(bundle.getString("subject"));
                annotationEdit.setText(bundle.getString("annotation"));
                setSpinner(bundle.getString("eventType"));
            }
        }

        titleEventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEventEdit.setHint("");
            }
        });


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
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                dateEventEdit.setText(sdf.format(calendar.getTime()));
            }
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventTitle = titleEventEdit.getText().toString();
                String annotation = annotationEdit.getText().toString();
                String subject    = subjectEdit.getText().toString();
                String eventType  = typeSpinner.getSelectedItem().toString();

                assert bundle != null;
                if (Objects.equals(bundle.getString("operation"), "create")){

                    createEvent(v, eventTitle, annotation, subject, eventType);

                } else if (Objects.equals(bundle.getString("operation"), "edit")){

                    /*DatabaseReference classRef = firebaseHelper.getReference().child("user");
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("alanisawesome/nickname", "Alan The Machine");
                    userUpdates.put("gracehop/nickname", "Amazing Grace");

                    usersRef.updateChildrenAsync(userUpdates);*/
                }

            }
        });

    }

    public String loadDate(){
        Long dateTimeInMills = retrieveDate();
        calendar.setTimeInMillis(dateTimeInMills);
        Date date = calendar.getTime();
        return convertDate(date);
    }

    public Long retrieveDate(){
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        return bundle.getLong("date");
    }

    public String convertDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        return sdf.format(date);
    }

    public String getCurrentDate(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        return sdf.format(calendar.getTime());
    }

    public void setSpinner(String selected){

        Spinner typeSpinner = findViewById(R.id.type_spinner);

        String[] eventType = getResources().getStringArray(R.array.event_type);
        ArrayAdapter<String> adapterCollege = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventType);
        adapterCollege.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapterCollege);
        if (selected != null) {
            int position = adapterCollege.getPosition(selected);
            typeSpinner.setSelection(position);
        }
    }

    public void createEvent(View v, String eventTitle, String annotation,
                            String subject, String eventType){

        if (!eventTitle.isEmpty()){

            DatabaseReference pushKey = ref.child(userClassID).push();
            EventData eventData = new EventData();
            eventData.setCreationDate(getCurrentDate());
            eventData.setEventKey(pushKey.getKey());
            eventData.setAnnotation(annotation);
            eventData.setCreatorID(userID);
            eventData.setTitle(eventTitle);
            eventData.setSubject(subject);
            eventData.setEventType(eventType);

            Event event = new Event(R.color.colorAccent, calendar.getTimeInMillis(), eventData);
            pushKey.child("Event").setValue(event);
            Snackbar.make(v, "Evento criado com sucesso!", Snackbar.LENGTH_LONG).show();
            finish();

        }else {
            Snackbar.make(v, "Insira o titulo para continuar.", Snackbar.LENGTH_LONG).show();
        }
    }

}

