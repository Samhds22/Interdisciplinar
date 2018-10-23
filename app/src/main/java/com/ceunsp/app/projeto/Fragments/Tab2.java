package com.ceunsp.app.projeto.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.HistoricAdapter;
import com.ceunsp.app.projeto.Model.Historic;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab2 extends Fragment {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private final String userID = firebaseHelper.getUserID();
    private List<Historic> historicList = new ArrayList<>();
    private final Bitmap[] bitmap = new Bitmap[1];
    private CircleImageView defaultProfile;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    protected String classID;

    @SuppressLint("ValidFragment")
    public Tab2(String classID) {
        this.classID = classID;
    }

    public Tab2() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historicTab = inflater.inflate(R.layout.tab_2,container,false);

        progressBar      = historicTab.findViewById(R.id.historic_progressBar);
        recyclerView     = historicTab.findViewById(R.id.historic_Recyclerview);
        defaultProfile   = historicTab.findViewById(R.id.default_image);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        loadHistoric();

        return historicTab;
    }

    public void loadHistoric(){
        DatabaseReference userRef = ref.child("Users");
        userRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userType = (String) dataSnapshot.child("userType").getValue();

                assert userType != null;
                if (userType.equals("Aluno")){
                    classID = (String) dataSnapshot.child("Student").child("classID").getValue();
                }

                loadData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadData(){
        DatabaseReference historicRef = firebaseHelper.getReference().child("Historic");
        Query queryHistoric = historicRef.child(classID).orderByKey();

        queryHistoric.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                    historicList.clear();
                    final Historic historic = postSnapShot.getValue(Historic.class);

                    assert historic != null;
                    StorageReference storageRef = firebaseHelper.getStorage();
                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.child("profilePicture."+ historic.getUserID()).getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>(){
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    historic.setImgProfile(bitmap[0]);
                                    historicList.add(historic);
                                    fillRecyclerView();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            bitmap[0] = retrieveDefaultImage();
                            historic.setImgProfile(bitmap[0]);
                            historicList.add(historic);
                            fillRecyclerView();
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fillRecyclerView(){
        HistoricAdapter adapter = new HistoricAdapter(historicList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public Bitmap retrieveDefaultImage(){
        defaultProfile.setImageResource(R.drawable.default_profile_image);
        defaultProfile.setDrawingCacheEnabled(true);
        defaultProfile.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        defaultProfile.layout(0, 0, defaultProfile.getMeasuredWidth(), defaultProfile.getMeasuredHeight());
        defaultProfile.buildDrawingCache();
        return Bitmap.createBitmap(defaultProfile.getDrawingCache());
    }

}
