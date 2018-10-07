package com.ceunsp.app.projeto.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth user;
    private EditText email, password;
    private Button salvar;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        user = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_cadastro);
        password = findViewById(R.id.password_cadastro);
        salvar = findViewById(R.id.salvar_cadastro);

        /* Cadastro do usuario no Firebase Authentication */

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view = v;
                user.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                       .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()){
                                   user.signOut();
                                   finish();
                               }else{

                               }
                           }
                       });
            }
        });

        /* Fim do cadastro de usuário */

    }
}
