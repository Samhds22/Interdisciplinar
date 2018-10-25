package com.ceunsp.app.projeto.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ceunsp.app.projeto.Activity.EventBodyActivity;
import com.ceunsp.app.projeto.Helpers.EventAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.RecyclerItemClickListener;
import com.ceunsp.app.projeto.Model.EventData;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private DatabaseReference ref = firebaseHelper.getReference().child("ClassCalendar");
    private Calendar calendar = Calendar.getInstance();
    private List<Event> eventList = new ArrayList<>();
    private String cMonth, classID, userType;
    private RecyclerView todayRecyclerView;
    private TextView emptyEventText;
    private ProgressBar progressBar;
    private Date today;

    @SuppressLint("ValidFragment")
    public HomeFragment(String classID, String userType) {

        this.classID = classID;
        this.userType = userType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View overview =  inflater.inflate(R.layout.fragment_home, container, false);

        todayRecyclerView = overview.findViewById(R.id.today_recyclerView);
        progressBar = overview.findViewById(R.id.home_progressBar);
        emptyEventText = overview.findViewById(R.id.empty_event);
        TextView dateTextView = overview.findViewById(R.id.date_textView);

        calendar.setTimeInMillis(System.currentTimeMillis());
        today = calendar.getTime();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        findMonth(month);

        String dateText = day + " de " + cMonth + ".";
        dateTextView.setText(dateText);

        eventList.clear();
        progressBar.setVisibility(View.VISIBLE);
        todayRecyclerView.setVisibility(View.GONE);

        loadTodayEvents();

        todayRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                todayRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Event event = eventList.get(position);
                        EventData eventData = (EventData) event.getData();

                        Intent intent = new Intent(getActivity(), EventBodyActivity.class);
                        intent.putExtra("operation", "View&Edit");
                        intent.putExtra("eventKey", eventData.getEventKey());
                        intent.putExtra("userClassID", classID);
                        intent.putExtra("title", eventData.getTitle());
                        intent.putExtra("date", event.getTimeInMillis());
                        intent.putExtra("subject", eventData.getSubject());
                        intent.putExtra("annotation", eventData.getAnnotation());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        return overview;

    }

    public void loadTodayEvents(){
        eventList.clear();
        if (classID.equals("") || classID == null){
            DatabaseReference usersRef = firebaseHelper.getReference().child("Users");
            DatabaseReference teacherRef = usersRef.child(firebaseHelper.getUserID());
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Classes").getChildren()){
                            classID = postSnapshot.getKey();
                            loadData();
                        }
                        if (classID.equals("") || classID.isEmpty() || (classID) == null){
                            emptyEvents();
                        }

                    } else {
                        emptyEvents();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    failure();
                }
            });

        } else if (!classID.equals("") && classID != null) {
            loadData();
        }
    }

    public void loadData(){

        DatabaseReference eventRef = ref.child(classID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (final DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                        Long dateInMillis = (Long) postSnapShot.child("Event").child("timeInMillis").getValue();
                        calendar.setTimeInMillis(dateInMillis);
                        Date eventDate = calendar.getTime();

                        if (convertDate(eventDate).equals(convertDate(today))){
                            DataSnapshot eventDataSnapshot = postSnapShot.child("Event").child("data");

                            EventData eventData = new EventData();
                            eventData.setEventKey((String) eventDataSnapshot.child("eventKey").getValue());
                            eventData.setTitle((String) eventDataSnapshot.child("title").getValue());
                            eventData.setEventType((String) eventDataSnapshot.child("eventType").getValue());
                            eventData.setSubject((String) eventDataSnapshot.child("subject").getValue());
                            eventData.setAnnotation((String) eventDataSnapshot.child("annotation").getValue());

                            if (userType.equals("Professor")) {
                                eventData.setClassName((String) eventDataSnapshot.child("className").getValue());
                            }

                            Event event = new Event( 1, dateInMillis, eventData);
                            eventList.add(event);
                            fillEventRecyclerView();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    todayRecyclerView.setVisibility(View.VISIBLE);
                }

                if (eventList.isEmpty()){
                    emptyEvents();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failure();
            }
        });
    }

    public void fillEventRecyclerView(){

        EventAdapter adapter = new EventAdapter(eventList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        todayRecyclerView.setLayoutManager(layoutManager);
        todayRecyclerView.setHasFixedSize(true);

        if (eventList.size() > 1){
            todayRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        }

        todayRecyclerView.setAdapter(adapter);
    }

    public String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        return sdf.format(date);
    }

    public void findMonth(int month){
        switch(month){
            case 0:{
                cMonth = "Janeiro";
                break;
            }case 1:{
                cMonth = "Fevereiro";
                break;
            }case 2:{
                cMonth = "Março";
                break;
            }case 3:{
                cMonth = "Abril";
                break;
            }case 4:{
                cMonth = "Maio";
                break;
            }case 5:{
                cMonth = "Junho";
                break;
            }case 6:{
                cMonth = "Julho";
                break;
            }case 7:{
                cMonth = "Agosto";
                break;
            }case 8:{
                cMonth = "Setembro";
                break;
            }case 9:{
                cMonth = "Outubro";
                break;
            }case 10:{
                cMonth = "Novembro";
                break;
            }case 11:{
                cMonth = "Dezembro";
                break;
            }
        }
    }

    public void failure(){
        Toast.makeText(getActivity(), "Falha ao carregar os dados, verifique sua conexão",
                Toast.LENGTH_LONG).show();
    }

    public void emptyEvents(){
        progressBar.setVisibility(View.GONE);
        todayRecyclerView.setVisibility(View.VISIBLE);
        emptyEventText.setVisibility(View.VISIBLE);
    }

}

