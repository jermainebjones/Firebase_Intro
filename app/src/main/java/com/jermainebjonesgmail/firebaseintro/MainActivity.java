package com.jermainebjonesgmail.firebaseintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MainActivity";

    private EditText email;
    private EditText password;
    private Button login;
    private Button signout;
    private Button createAccount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.emailEd);
        password = (EditText) findViewById(R.id.passwordEd);
        login = (Button) findViewById(R.id.loginButton);
        signout = (Button) findViewById(R.id.signOut);
        createAccount = (Button) findViewById(R.id.createAct);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("message");

        databaseReference.setValue("Another");

        //databaseReference.addValueEventListener(new ValueEventListener() {
          //  @Override
            //public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //
//
  //              String value = dataSnapshot.getValue(Customer.class);
//
//
  //              Toast.makeText(MainActivity.this, value, Toast.LENGTH_LONG).show();
//
  //          }
//
  //          @Override
    //        public void onCancelled(@NonNull DatabaseError databaseError) {
//
  //          }
    //    });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "user signed in");
                    Log.d(TAG, "UserName: " + user.getEmail());
                }else {
                    //user is signed out.
                    Log.d(TAG, "user signed out");
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = email.getText().toString();
                String pwd = password.getText().toString();

                if (!emailString.equals("") && !pwd.equals("")) {
                    mAuth.signInWithEmailAndPassword(emailString, pwd)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this,"Failed sign in", Toast.LENGTH_LONG)
                                                .show();
                                    }else {
                                        Toast.makeText(MainActivity.this, "Signed in!!", Toast.LENGTH_LONG)
                                                .show();

                                        Customer customer = new Customer("Gina", "Machaval", emailString, 78);

                                        // We can now write to database
                                        databaseReference.setValue(customer);
                                    }
                                }
                            });
                }
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "You signed out!", Toast.LENGTH_LONG).show();

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                String pwd = password.getText().toString();

                if (!emailString.equals("") && !pwd.equals("")) {

                    mAuth.createUserWithEmailAndPassword(emailString, pwd).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Failed to create account"
                                        , Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Account Created"
                                        , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
