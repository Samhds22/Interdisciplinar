package com.ceunsp.app.projeto.Activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private EditText nameEdit, lastNameEdit, nicknameEdit, dtBirthEdit;
    private EditText emailEdit, passwordEdit, pwConfirmEdit;
    private static final String PREFERENCES = "Preferences";
    private Calendar calendar = Calendar.getInstance();
    private final int PICK_IMAGE_REQUEST = 71;
    private ProgressBar progressBar;
    private CircleImageView photoImage;
    private Spinner userTypeSpinner;
    private FloatingActionButton saveButton;
    private String  userID;
    private Uri filePath;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Novo usuário");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);

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
        progressBar     = findViewById(R.id.register_progressBar);

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
                String myFormat = "dd/MM/yyyy";
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

                String email       = emailEdit.getText().toString();
                String password    = passwordEdit.getText().toString();
                String name        = nameEdit.getText().toString();
                String lastName    = lastNameEdit.getText().toString();
                String nickname    = nicknameEdit.getText().toString();
                String dateOfBirth = dtBirthEdit.getText().toString();
                String userType    = userTypeSpinner.getSelectedItem().toString();
                String pwConfirm   = pwConfirmEdit.getText().toString();

                if (checkConnection()){

                    if (tryToRegister(name, lastName, nickname, dateOfBirth, email, password, pwConfirm)) {
                        showProgressBar();
                        createUser( v ,email, password, name, lastName, nickname, dateOfBirth, userType);
                    } else {
                        hideProgressBar();
                    }

                } else{
                    Toast.makeText(getApplicationContext(), "Sem conexão", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onResume() {

        hideProgressBar();
        super.onResume();
    }

    public boolean tryToRegister(String name, String lastname, String nickname, String dateOfBirth,
                                 String email, String password, String pwConfirm){

            nameEdit.setError(null);
        lastNameEdit.setError(null);
        nicknameEdit.setError(null);
         dtBirthEdit.setError(null);
           emailEdit.setError(null);
        passwordEdit.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!isValidEmail(email)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            nameEdit.setError(getString(R.string.error_field_required));
            focusView = nameEdit;
            cancel = true;
        } else if (!isValidName(name)){
            nameEdit.setError(getString(R.string.error_invalid_name));
            focusView = nameEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            passwordEdit.setError(getString(R.string.error_field_required));
            focusView = passwordEdit;
            cancel = true;
        } else if (!isValidPassword(password)){
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
    }

    public static boolean isValidPassword(String target) {
        return target.length() >= 6;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isValidName(String target) {
        return Pattern.compile("^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣]{1,}$").matcher(target).matches();

    }

    public static boolean isValidNickName(String target) {
        return target.length() > 4;
        //return Pattern.compile("^(?=.*[a-zA-Z\\d])[a-zA-Z0-9가-힣]{2,12}$|^[가-힣]$").matcher(target).matches();
    }

    private void LoadSpinner(){
        String []userType = getResources().getStringArray(R.array.user_type);
        ArrayAdapter<String> adapterUserType =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,userType);
        adapterUserType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapterUserType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                photoImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void createUser(final View v, final String email, String password , final String name, final String lastName,
                           final String nickname, final String dateOfBirth, final String userType){

        firebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userID = firebaseHelper.getUserID();
                            User user = new User(name, lastName, nickname,dateOfBirth, userType);
                            firebaseHelper.getReference().child("Users").child(userID).setValue(user);
                            saveInPreferences(userID, name, lastName, nickname, dateOfBirth, userType, email);
                            uploadImage();

                        } else {
                            Snackbar.make(v,"Falha ao criar usuário", Snackbar.LENGTH_LONG ).show();
                            hideProgressBar();
                        }
                    }
                });
    }

    private void uploadImage() {

        if(filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            photoImage.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            photoImage.layout(0, 0, photoImage.getMeasuredWidth(), photoImage.getMeasuredHeight());
            photoImage.buildDrawingCache();
            Bitmap bitmapImage = Bitmap.createBitmap(photoImage.getDrawingCache());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] byteData = outputStream.toByteArray();

            StorageReference ref = storageReference.child("profilePicture."+ userID);
            ref.putBytes(byteData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage()
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        finish();
    }



    public void saveInPreferences(String userID, String name, String lastName, String nickname,
                                  String dateOfBirth, String userType, String email ){

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("userID" , userID);
        editor.putString("name", name);
        editor.putString("lastName", lastName);
        editor.putString("nickname", nickname);
        editor.putString("dateOfBirth", dateOfBirth);
        editor.putString("userType", userType);
        editor.putString("email", email);
        editor.apply();
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void hideKeyboard(){
        ((InputMethodManager) Objects.requireNonNull(getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)))
                .hideSoftInputFromWindow(dtBirthEdit.getWindowToken(), 0);
    }

    public  boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void showProgressBar(){
        saveButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        saveButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
