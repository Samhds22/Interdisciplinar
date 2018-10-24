package com.ceunsp.app.projeto.Fragments;


import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ceunsp.app.projeto.Activity.JoinClassActivity;
import com.ceunsp.app.projeto.Helpers.EventAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.UsersAdapter;
import com.ceunsp.app.projeto.Model.EventData;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private DatabaseReference ref = firebaseHelper.getReference().child("ClassCalendar");
    private List<Event> eventList = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private ProgressBar progressBar;
    private Date today;
    private RecyclerView todayRecyclerView;
    private TextView dateTextView;
    String cMonth, classID;

    @SuppressLint("ValidFragment")
    public HomeFragment(String classID) {

        this.classID = classID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View overview =  inflater.inflate(R.layout.fragment_home, container, false);

        todayRecyclerView = overview.findViewById(R.id.today_recyclerView);
        progressBar       = overview.findViewById(R.id.home_progressBar);
        dateTextView      = overview.findViewById(R.id.date_textView);

        calendar.setTimeInMillis(System.currentTimeMillis());
        today = calendar.getTime();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        findMonth(month);

        String dateText = day + " de " + cMonth + ".";
        dateTextView.setText(dateText);

        return overview;

    }

    @Override
    public void onStart() {

        progressBar.setVisibility(View.VISIBLE);
        todayRecyclerView.setVisibility(View.GONE);

        if (!classID.equals("") && classID != null){
            DatabaseReference eventRef = ref.child(classID);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    eventList.clear();
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                        Long dateInMillis = (Long) postSnapShot.child("Event").child("timeInMillis").getValue();
                        calendar.setTimeInMillis(dateInMillis);
                        Date eventDate = calendar.getTime();

                        if (convertDate(eventDate).equals(convertDate(today))){

                            DataSnapshot eventDataSnapshot = postSnapShot.child("Event").child("data");

                            EventData eventData = new EventData();
                            eventData.setEventKey((String) eventDataSnapshot.child("eventKey").getValue());
                            eventData.setTitle((String) eventDataSnapshot.child("title").getValue());
                            eventData.setEventType((String) eventDataSnapshot.child("eventType").getValue());

                            Event event = new Event( 1, dateInMillis, eventData);
                            eventList.add(event);
                            fillEventRecyclerView();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    todayRecyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }


        super.onStart();
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

}
