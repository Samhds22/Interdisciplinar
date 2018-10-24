package com.ceunsp.app.projeto.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ceunsp.app.projeto.Model.Annotation;
import com.ceunsp.app.projeto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("Registered")
public class NewAnnotationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText titleEdit, bodyEdit;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);
        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);


        titleEdit = findViewById(R.id.title_annotation_edit);
        bodyEdit  = findViewById(R.id.AnotacaoDesc_editText);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleEdit.getText().toString();
                String body = bodyEdit.getText().toString();

                if (validAction(view, title, body)){
                    saveAnnotation(title, body);
                }

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("AUTH", "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveAnnotation(String title, String body) {

        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
        String creationDate = GetCurrentDate();
        String UserAtivo  = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        DatabaseReference anotacoesdb = referencia.child("Anotacoes").child(UserAtivo);
        DatabaseReference pushKey = anotacoesdb.push();
        String anotID = pushKey.getKey();

        Annotation novaAnotacao = new Annotation(title, body,UserAtivo,anotID);
        pushKey.setValue(novaAnotacao);

        Intent intent = new Intent(getApplicationContext(), AnnotationsActivity.class);
        startActivity(intent);

    }

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public String GetCurrentDate(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        return sdf.format(calendar.getTime());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    public boolean validAction(View view, String title, String body){

        if (title.isEmpty()){
            Snackbar.make(view, "Preencha o título para continuar", Toast.LENGTH_LONG).show();
            return false;

        } else if (body.isEmpty()){
            Snackbar.make(view, "Faça sua anotação!", Toast.LENGTH_LONG).show();
            return false;

        } else {
            return true;
        }
    }
}
