package com.example.anuswa.letssplit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class newgrp extends AppCompatActivity {

    private EditText grpname;
    private Button addPer, save;
    private final int Pick_contact = 1;
    private ImageView grpimg;
    private DatabaseReference myref;
    FirebaseUser firebaseUser;
    String ConName, grpName,uid;
    private TextView grp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgrp);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        grpname = (EditText) findViewById(R.id.grpnm_id);
        addPer = (Button) findViewById(R.id.addP_id);
        save = (Button) findViewById(R.id.save_id);
        grpimg = findViewById(R.id.grpimg);
        grp = findViewById(R.id.grp1_id);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                uid = firebaseUser.getUid();
                grpName = grpname.getText().toString();
                myref = FirebaseDatabase.getInstance().getReference("users").child(uid);
                myref.push().setValue(grpName);


                grp.setText(grpName);
            }
        });


        addPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callContact(v);

            }
        });
    }





    public void callContact(View v) {
        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(in, Pick_contact);
    }


    @Override
    protected void onActivityResult(int reqCode, int ResultCode, Intent data) {
        super.onActivityResult(reqCode, ResultCode, data);

        if (reqCode == Pick_contact) {
            if (ResultCode == AppCompatActivity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c !=null && c.moveToFirst()) {
                   String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                   int columnindex  = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                   String con_no = c.getString(columnindex);

                    Toast.makeText(newgrp.this, "You have picked : " + name + con_no, Toast.LENGTH_SHORT).show();
                        setCon(name,con_no);
                    //return name;
                }
                c.close();
            }
        }

    }
    void setCon(String name,String Contact){
         String contactnm = name;
         String connum = Contact;
        //this.ConName = name;
        myref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Group").child(grpName);
        myref.push().setValue(contactnm,connum);
     //   myref.child("Group").child(grpName).setValue(grpName);


    }

}




/*
 public void callContact(View v){
            Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(in,Pick_contact);
        }


    @Override
    protected void onActivityResult(int reqCode,int ResultCode, Intent data) {
        super.onActivityResult(reqCode,ResultCode,data);

        if (reqCode==Pick_contact)
        {
            if (ResultCode==AppCompatActivity.RESULT_OK)
            {
                Uri contactData=data.getData();
                Cursor c=getContentResolver().query(contactData,null,null,null,null);

                if (c.moveToFirst())
                {
                    String name=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    Toast.makeText(this,"You have picked" + name,Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/
