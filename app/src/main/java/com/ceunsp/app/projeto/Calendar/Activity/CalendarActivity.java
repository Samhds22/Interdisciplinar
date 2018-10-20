package com.ceunsp.app.projeto.Calendar.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.ceunsp.app.projeto.Calendar.Model.EventData;
import com.ceunsp.app.projeto.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarActivity extends Fragment {

    private static final String TAG = "MainActivity";
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getCurrentUser().getUid();
    final List<String> mutableBookings = new ArrayList<>();
    private List<EventData> eventDataList = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();
    private CompactCalendarView compactCalendarView;
    private Long timeInMilliseconds;
    private boolean shouldShow = false;
    private ActionBar toolbar;
    private Date selectedDate;
    private String classID;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainTabView = inflater.inflate(R.layout.main_tab,container,false);
        final FloatingActionButton fab = mainTabView.findViewById(R.id.fab_calendar_activity);
        final ListView bookingsListView = mainTabView.findViewById(R.id.bookings_listview);
        final Button showPreviousMonthBut = mainTabView.findViewById(R.id.prev_button);
        final Button showNextMonthBut = mainTabView.findViewById(R.id.next_button);

        final ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext())
                , android.R.layout.simple_list_item_1, mutableBookings);
        final Button showCalendarWithAnimationBut = mainTabView.findViewById
                (R.id.show_with_animation_calendar);

        bookingsListView.setAdapter(adapter);

        compactCalendarView = mainTabView.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        compactCalendarView.invalidate();

        final View.OnClickListener exposeCalendarListener = getCalendarExposeLis();
        showCalendarWithAnimationBut.setOnClickListener(exposeCalendarListener);

        DatabaseReference userRef = ref.child("Users");
        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userType = (String) dataSnapshot.child("userType").getValue();

                assert userType != null;
                if (userType.equals("Aluno")){
                    classID = (String) dataSnapshot.child("Student").child("classID").getValue();

                } else if (userType.equals("Professor")){


                }

                DatabaseReference classRef = ref.child("ClassCalendar").child(classID);
                classRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            eventDataList.clear();
                            compactCalendarView.removeAllEvents();

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                Long time = (Long) postSnapshot.child("Event").child("timeInMillis").getValue();
                                EventData eventData = postSnapshot.child("Event").child("data").getValue(EventData.class);
                                Event event = new Event(R.color.colorAccent, time, eventData);
                                loadEvents(event);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        compactCalendarView.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
                showNextMonthBut.setVisibility(View.VISIBLE);
                showPreviousMonthBut.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClosed() {
                showNextMonthBut.setVisibility(View.INVISIBLE);
                showPreviousMonthBut.setVisibility(View.INVISIBLE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedDate);
                    timeInMilliseconds = calendar.getTimeInMillis();
                }else{
                    timeInMilliseconds = System.currentTimeMillis();
                }
                Intent intentAddEvent = new Intent(mainTabView.getContext(), EventActivity.class);
                intentAddEvent.putExtra("operation", "create");
                intentAddEvent.putExtra("date", timeInMilliseconds);
                intentAddEvent.putExtra("userClassID", classID);
                intentAddEvent.putExtra("userID", userID);
                startActivity(intentAddEvent);
            }
        });

        toolbar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;
                RefreshListView(dateClicked);
            }
            void RefreshListView(Date dateClicked){
                toolbar.setTitle(dateFormatForMonth.format(dateClicked));
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);

                eventDataList.clear();
                for (int index = 0; index <bookingsFromMap.size(); index++){
                    Event event = bookingsFromMap.get(index);
                    EventData eventData = (EventData) event.getData();
                    eventList.add(event);
                    eventDataList.add(eventData);
                }

                mutableBookings.clear();

                for (EventData eventData : eventDataList) {
                    mutableBookings.add(eventData.getTitle());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event event = eventList.get(position);
                EventData eventData = eventDataList.get(position);

                Intent openEvent = new Intent(getContext(), EventActivity.class);
                openEvent.putExtra("userClassID", classID);
                openEvent.putExtra("operation", "view");
                openEvent.putExtra("title", eventData.getTitle());
                openEvent.putExtra( "date", event.getTimeInMillis());
                openEvent.putExtra("subject", eventData.getSubject());
                openEvent.putExtra("eventType", eventData.getEventType());
                openEvent.putExtra("annotation", eventData.getAnnotation());
                startActivity(openEvent);
            }
        });

        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollLeft();
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollRight();
            }
        });

        return mainTabView;
    }

    @NonNull
    private View.OnClickListener getCalendarExposeLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compactCalendarView.isAnimating()) {
                    if (shouldShow) {
                        compactCalendarView.showCalendarWithAnimation();
                    } else {
                        compactCalendarView.hideCalendarWithAnimation();
                    }
                    shouldShow = !shouldShow;
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
    }

    private void loadEvents(Event event) {
        compactCalendarView.addEvent(event);
    }

}