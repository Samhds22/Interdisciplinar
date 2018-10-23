package com.ceunsp.app.projeto.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.ceunsp.app.projeto.Model.EventData;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.Historic;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventBodyActivity extends AppCompatActivity {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private EditText dateEventEdit, subjectEdit, titleEventEdit, annotationEdit;
    private String userClassID, userID, eventKey;
    private SharedPreferences preferences;
    private Spinner typeSpinner;
    private Calendar calendar;


    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassCalendar");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String PREFERENCES = "Preferences";
        preferences = getSharedPreferences(PREFERENCES, 0);
        Button deleteButtom;

        deleteButtom   = findViewById(R.id.delete_button);
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

                deleteButtom.setVisibility(View.GONE);
                dateEventEdit.setText(loadDate());
                userClassID = bundle.getString("userClassID");
                setSpinner(null);

            } else if (Objects.equals(bundle.getString("operation"), "View&Edit")){

                deleteButtom.setVisibility(View.VISIBLE);
                eventKey = bundle.getString("eventKey");
                userClassID = bundle.getString("userClassID");
                titleEventEdit.setText(bundle.getString("title"));
                dateEventEdit.setText(loadDate());
                subjectEdit.setText(bundle.getString("subject"));
                annotationEdit.setText(bundle.getString("annotation"));
                setSpinner(bundle.getString("eventType"));
                titleEventEdit.setEnabled(false);
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
                        new DatePickerDialog(EventBodyActivity.this, date, calendar
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

                } else if (Objects.equals(bundle.getString("operation"), "View&Edit")){

                    attempUpdate(v, eventTitle, annotation, subject, eventType);

                }
            }
        });

        deleteButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempDelete(v);
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

    public String convertHour(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("pt","BR"));
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
            createHistoric(eventTitle, eventType, pushKey.getKey(), "create");
            finish();

        }else {
            Snackbar.make(v, "Insira o titulo para continuar.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void createHistoric(String eventTitle, String eventType, String eventKey, String action){

        DatabaseReference ref = firebaseHelper.getReference().child("Historic");
        DatabaseReference historicRef = ref.child(userClassID).child(eventKey);
        Calendar calendarAux = Calendar.getInstance();

        String name     = preferences.getString("name", "");
        String lastName = preferences.getString("lastName", "");
        String userType = preferences.getString("userType", "");
        String date     = convertDate(calendarAux.getTime());
        String hour     = convertHour(calendarAux.getTime());
        String fullName = name + " " + lastName;

        Historic historic = new Historic(fullName, userType, userID,
                action, eventType, eventTitle, date, hour);

        historicRef.setValue(historic);
    }

    public void updateEvent(String eventTitle, String annotation,
                            String subject, String eventType){

        DatabaseReference classRef = ref.child(userClassID).child(eventKey);

        Map<String, Object> eventUpdate = new HashMap<>();
        eventUpdate.put("timeInMillis", calendar.getTimeInMillis());
        eventUpdate.put("data/title", eventTitle);
        eventUpdate.put("data/subject", subject);
        eventUpdate.put("data/eventType", eventType);
        eventUpdate.put("data/annotation", annotation);
        classRef.child("Event").updateChildren(eventUpdate);

        createHistoric(eventTitle, eventType, eventKey, "update");

        Toast.makeText(getApplicationContext(), "Evento atualizado com sucesso!", Snackbar.LENGTH_LONG).show();
        finish();

    }



    public void attempDelete(final View v){

        DatabaseReference userClass = ref.child(userClassID).child(eventKey).child("Event");
        userClass.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (checkPermission(dataSnapshot)){
                    showDeleteDialog();
                } else {
                    Snackbar.make(v,"Você não tem permissão para apagar este evento",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void attempUpdate(final View v, final String eventTitle,
                             final String annotation, final String subject, final String eventType){

        DatabaseReference userClass = ref.child(userClassID).child(eventKey).child("Event");
        userClass.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (checkPermission(dataSnapshot)){
                    showUpdateDialog(eventTitle, annotation, subject, eventType);
                } else {
                    Snackbar.make(v,"Você não tem permissão para alterar este evento",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean checkPermission(DataSnapshot dataSnapshot){

        String cretorID = (String) dataSnapshot.child("data").child("creatorID").getValue();

        if (userID.equals(cretorID)){
            return true;
        } else {
            return false;
        }
    }

    public void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EventBodyActivity.this);
        builder.setTitle(R.string.title2);
        builder.setMessage(R.string.message2);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteEvent();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                closeContextMenu();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUpdateDialog(final String eventTitle, final String annotation,
                                 final String subject, final String eventType){

        AlertDialog.Builder builder = new AlertDialog.Builder(EventBodyActivity.this);
        builder.setTitle(R.string.title3);
        builder.setMessage(R.string.message3);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateEvent(eventTitle, annotation, subject, eventType);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                closeContextMenu();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteEvent(){


        DatabaseReference userClass = ref.child(userClassID).child(eventKey);
        userClass.setValue(null);
        DatabaseReference ref = firebaseHelper.getReference().child("Historic");
        DatabaseReference historicRef = ref.child(userClassID).child(eventKey);
        historicRef.setValue(null);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
}

