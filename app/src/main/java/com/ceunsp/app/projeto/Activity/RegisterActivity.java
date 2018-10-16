package com.ceunsp.app.projeto.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceunsp.app.projeto.Calendar.Activity.EventActivity;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private EditText nameEdit, lastNameEdit, nicknameEdit, dtBirthEdit,
            emailEdit, passwordEdit, pwConfirmEdit;
    private String email, password, name, lastName, nickname,
            dateOfBith, userType, userID;
    private int PICK_IMAGE_REQUEST = 1;
    private CircleImageView photoImage;
    private Spinner userTypeSpinner;
    private Button saveButton;
    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Novo usuário");

        nameEdit        = findViewById(R.id.name_edit);
        lastNameEdit    = findViewById(R.id.last_name_edit);
        nicknameEdit    = findViewById(R.id.nickname);
        dtBirthEdit     = findViewById(R.id.date_of_birth);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        emailEdit       = findViewById(R.id.email_edit);
        passwordEdit    = findViewById(R.id.password_edit);
        pwConfirmEdit   = findViewById(R.id.password_confirm_edit);
        saveButton      = findViewById(R.id.save_button);
        photoImage      = findViewById(R.id.photo_image);

        calendar.setTimeInMillis(System.currentTimeMillis());
        LoadSpinner();

        dtBirthEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                dtBirthEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        new DatePickerDialog(RegisterActivity.this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();

                    }
                });
                if (dtBirthEdit.hasFocus()) {
                    dtBirthEdit.performClick();
                    hideKeyboard();
                }
            }
            private void updateLabel(){
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                dtBirthEdit.setText(sdf.format(calendar.getTime()));
                hideKeyboard();
            }
        });


        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email      = emailEdit.getText().toString();
                password   = passwordEdit.getText().toString();
                name       = nameEdit.getText().toString();
                lastName   = lastNameEdit.getText().toString();
                nickname   = nicknameEdit.getText().toString();
                dateOfBith = dtBirthEdit.getText().toString();
                userType   = userTypeSpinner.getSelectedItem().toString();

                createUser(email, password);

            }
        });
    }

    /*public boolean AttempRegister(String name, String lastname, String nickname
                                 ,String dateOfBirth, String college, String course
                                 ,String email, String password, String pwConfirm){

            nameEdit.setError(null);
        lastNameEdit.setError(null);
        nicknameEdit.setError(null);
         dtBirthEdit.setError(null);
           emailEdit.setError(null);
        passwordEdit.setError(null);



        boolean cancel      = false;
        View focusView      = null;

        // Verifica se é um email válido.
        if (TextUtils.isEmpty(emailText)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!isValidEmail(emailText)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        }

        //Verifica se o nome é valido
        if (TextUtils.isEmpty(nameText)) {
            nameEdit.setError(getString(R.string.error_field_required));
            focusView = nameEdit;
            cancel = true;
        } else if (!isValidName(nameText)){
            nameEdit.setError(getString(R.string.error_invalid_name));
            focusView = nameEdit;
            cancel = true;
        }

        // Verifica se a senha é valida
        if (TextUtils.isEmpty(passwordText)){
            passwordEdit.setError(getString(R.string.error_field_required));
            focusView = passwordEdit;
            cancel = true;
        } else if (!isValidPassword(passwordText)){
            passwordEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordEdit;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;

        } else {
            return true;
        }
    }*/

    public final static boolean isValidPassword(String target) {
        return Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]{4,12}$").matcher(target).matches();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    public final static boolean isValidName(String target) {
        return Pattern.compile("^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣]{1,}$").matcher(target).matches();

    }

    public final static boolean isValidNickName(String target) {
        return target.length() > 4;
        //return Pattern.compile("^(?=.*[a-zA-Z\\d])[a-zA-Z0-9가-힣]{2,12}$|^[가-힣]$").matcher(target).matches();
    }

    private void LoadSpinner(){
        String []userType = getResources().getStringArray(R.array.user_type);
        ArrayAdapter<String> adapterUserType =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,userType);
        adapterUserType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapterUserType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                photoImage.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void SaveImage(StorageReference reference){

        photoImage.setDrawingCacheEnabled(true);
        photoImage.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        photoImage.layout(0, 0, photoImage.getMeasuredWidth(), photoImage.getMeasuredHeight());
        photoImage.buildDrawingCache();
        Bitmap bitmapImage = Bitmap.createBitmap(photoImage.getDrawingCache());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteData = outputStream.toByteArray();

        UploadTask uploadTask = reference.putBytes(byteData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Falha ao salvar imagem do perfil.",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createUser(String userEmail, String userPassword){
        firebaseHelper.getAuth().createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User(name, lastName, nickname
                                    ,dateOfBith, "", "", "", userType);

                                userID = firebaseHelper.getUserID();
                                firebaseHelper.getReference().child("Users")
                                        .child(userID).setValue(user);


                            SaveImage(firebaseHelper.getStorage().child("image-profile." + userID));
                            firebaseHelper.getAuth().signOut();
                            finish();
                        }
                    }
                });
    }
    public void hideKeyboard(){
        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(dtBirthEdit.getWindowToken(), 0);
    }
}
