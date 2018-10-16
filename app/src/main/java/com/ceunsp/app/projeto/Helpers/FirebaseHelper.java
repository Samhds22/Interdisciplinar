package com.ceunsp.app.projeto.Helpers;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReferenceFromUrl("gs://projeto-c263f.appspot.com");

    public DatabaseReference getReference(){
        return ref;
    }

    public String getUserID(){
       return auth.getCurrentUser().getUid();
    }

    public FirebaseAuth getAuth(){
        return auth;
    }

    public StorageReference getStorage() {
        return storageReference;
    }


}

