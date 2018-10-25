package com.ceunsp.app.projeto.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.AnnotationAdapter;
import com.ceunsp.app.projeto.AtualizarAnotacaoActivity;
import com.ceunsp.app.projeto.Helpers.FirebaseConfig;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.RecyclerItemClickListener;
import com.ceunsp.app.projeto.Model.Annotation;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationsActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final String userID = firebaseHelper.getUserID();
    private DatabaseReference anotacoesdb= firebaseHelper.getReference();
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
    private DatabaseReference localbanco;
    private RecyclerView recyclerView;
    private AnnotationAdapter anotacoesViewAdapter;
    private List<Annotation> annotationList = new ArrayList<>();
    private Annotation anot;
    private ProgressBar progressBar;
    private TextView emptyText;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Anotações");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        emptyText    = findViewById(R.id.empty_annotation_textView);
        progressBar  = findViewById(R.id.annotation_progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        emptyText.setVisibility(View.GONE);
        swipe();

        localbanco = anotacoesdb.child("Anotacoes").child(userID);

        localbanco.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                annotationList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){

                        String titulo  = (String) postSnapshot.child("tituloAnotacao").getValue();
                        String eventdescricao = (String) postSnapshot.child("descAnotacao").getValue();
                        String usuarioAtivo = (String) postSnapshot.child("usuarioAtivo").getValue();
                        final String key = (String) postSnapshot.child("key").getValue();

                        Annotation anotacaopegadadosdb = new Annotation
                                (titulo,eventdescricao,usuarioAtivo,key);
                        annotationList.add(0, anotacaopegadadosdb);
                        fillRecyclerView();

                    }
                }
                if (annotationList.isEmpty()){
                    emptyText.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                        recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                anot = annotationList.get( position );

                String usuarioAti = anot.getUsuarioAtivo();
                String KeyUsu = anot.getKey();
                String desc = anot.getDescAnotacao();
                String titul = anot.getTituloAnotacao();
                String permissao = "permitido";

                Intent intent = new Intent(getApplicationContext(), NewAnnotationActivity.class);
                intent.putExtra("usuario", usuarioAti);
                intent.putExtra("key", KeyUsu);
                intent.putExtra("desc", desc);
                intent.putExtra("titul", titul);
                intent.putExtra("position", position);
                intent.putExtra("permissao", permissao);
                startActivity( intent );

            }
            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
    }

    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteAnnotation( viewHolder );
            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerView );

    }

    public void deleteAnnotation(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir anotação?");
        alertDialog.setMessage("Deseja realmente excluir essa Anotação?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                anot = annotationList.get( position );
                String Usuario = anot.getUsuarioAtivo();
                String key = anot.getKey();
                localbanco = firebaseRef.child("Anotacoes").child(Usuario);
                localbanco.child(key).removeValue();
                anotacoesViewAdapter.notifyItemRemoved( position );

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AnnotationsActivity.this,
                        "Cancelado",
                        Toast.LENGTH_SHORT).show();
                anotacoesViewAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void chamaanotacoes(View view){
            String alteracao = "nao";
        Intent intent = new Intent(getApplicationContext(), NewAnnotationActivity.class);
            intent.putExtra("alteracao", alteracao);
        startActivity(intent);
    }

    public void fillRecyclerView(){
        anotacoesViewAdapter = new AnnotationAdapter(annotationList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( anotacoesViewAdapter );
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
}
