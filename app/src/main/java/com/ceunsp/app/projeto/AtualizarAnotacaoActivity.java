package com.ceunsp.app.projeto;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ceunsp.app.projeto.Activity.AnnotationsActivity;
import com.ceunsp.app.projeto.Helpers.FirebaseConfig;
import com.ceunsp.app.projeto.Model.Annotation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtualizarAnotacaoActivity extends AppCompatActivity {

    private DatabaseReference localbanco;
    private List<Annotation> listaAnotacaolistas = new ArrayList<>();
    private Annotation anotacaoselecionada;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private TextView Usuario, Key, titulo, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_anotacao);

        titulo = findViewById(R.id.TituloAlt_EditText);
        desc =findViewById(R.id.DescAlt_EditText);



        final Bundle dados= getIntent().getExtras();
        final String Usuario = dados.getString("usuario");
        final String Key = dados.getString("key");
        String anotacaodesc = dados.getString("desc");
        String anotacaotitulo = dados.getString("titul");
        int position = dados.getInt("position");

        titulo.setText(anotacaotitulo);
        desc.setText(anotacaodesc);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizar(dados);
            }
        });

    }

    public void atualizar(Bundle dados){

        String a = titulo.getText().toString();
        String b = desc.getText().toString();
        String Usuario = dados.getString("usuario");
        String Key = dados.getString("key");

        DatabaseReference firebaseref = FirebaseConfig.getFirebase();
        DatabaseReference anotref = firebaseref.child("Anotacoes").child(Usuario).child(Key);

        Map<String, Object> AnotacaoUpdate = new HashMap<>();
        AnotacaoUpdate.put("tituloAnotacao", a);
        AnotacaoUpdate.put("descAnotacao", b);


        anotref.updateChildren(AnotacaoUpdate);

        Intent intent = new Intent(getApplicationContext(), AnnotationsActivity.class);


    }

}
