package com.example.anuswa.letssplit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_pass extends AppCompatActivity {

    private EditText resetMail;
    private Button resendMain;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        mAuth = FirebaseAuth.getInstance();
        resetMail = findViewById(R.id.ResetEm_id);
        resendMain = findViewById(R.id.Resend_id);

        resendMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = resetMail.getText().toString();
                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(Reset_pass.this,"Please Enter a valid E.mail Address!!",Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 Toast.makeText(Reset_pass.this,"Please Check your mail box!!",Toast.LENGTH_SHORT).show();
                                 Intent in = new Intent(Reset_pass.this,login.class);
                                 startActivity(in);
                             }
                             else{
                                 String message = task.getException().getMessage();
                                 Toast.makeText(Reset_pass.this,"Error Occured!!" +message,Toast.LENGTH_SHORT).show();
                             }
                        }
                    });
                }
            }
        });
    }
}
