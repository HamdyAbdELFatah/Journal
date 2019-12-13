package com.coders.journal;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coders.journal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class CreateAccountActivity extends AppCompatActivity {
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        firebaseAuth=FirebaseAuth.getInstance();
        createAcctButton = findViewById(R.id.create_acct_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);
    }
    public void create(View view) {
        if(!emailEditText.getText().toString().trim().equals("") && !passwordEditText.getText().toString().trim().equals("")
                && !userNameEditText.getText().toString().trim().equals("")){
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                Map<String, String> map = new HashMap<>();
                                map.put("userId", currentUser.getUid());
                                map.put("username", userNameEditText.getText().toString());
                                collectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                JournalApi journalApi = JournalApi.getInstance(); //Global API
                                                journalApi.setUserId( currentUser.getUid());
                                                journalApi.setUsername(task.getResult().getString("username"));
                                                Intent i = new Intent(CreateAccountActivity.this, JournalListActivity.class);
                                                //i.putExtra("userid", currentUser.getUid());
                                                //i.putExtra("username", task.getResult().getString("username"));
                                                startActivity(i);
                                                Toast.makeText(CreateAccountActivity.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }else
                                Toast.makeText(CreateAccountActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        }else
            Toast.makeText(CreateAccountActivity.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this,CreateAccountActivity.class));
    }
}