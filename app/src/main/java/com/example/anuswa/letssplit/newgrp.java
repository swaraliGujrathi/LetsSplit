package com.example.anuswa.letssplit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.firebase.database.Query;


public class newgrp extends AppCompatActivity {

    private EditText grpname_edit;
    private Button addPer, save, back, next;
    private final int Pick_contact = 1;
    private ImageView grpimg;
    private DatabaseReference myref;
    FirebaseUser firebaseUser;
    String ConName, grpName, t,name,uid, con;
    private TextView grp;

    String grpname, sresult, key;
    String[] personname = new String[10];
    String[] persondebt = new String[10];
    private int count = 1, x = 0, cost, mtotbill;
    private String mcat, contactnm;


    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> listg = new ArrayList<>();
    private ListView listgrp;
    FirebaseAuth mAuth;
    DatabaseReference grpref;
    String namep;
    MyList list = new MyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgrp);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        grpname_edit = (EditText) findViewById(R.id.grpnm_id);
        addPer = (Button) findViewById(R.id.addP_id);
        save = (Button) findViewById(R.id.save_id);
        grpimg = findViewById(R.id.grpimg);
        grp = findViewById(R.id.grp1_id);
        back = findViewById(R.id.back_id);
        next = findViewById(R.id.next_id);

        listgrp = findViewById(R.id.list_id);

        grpref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        arrayAdapter = new ArrayAdapter<String>(newgrp.this, android.R.layout.simple_list_item_1, listg);
        listgrp.setAdapter(arrayAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(newgrp.this, crtgrp.class);
                startActivity(in);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            public void onClick(View v) {

                Intent in1 = new Intent(newgrp.this, ingrp.class);
                in1.putExtra("gnm", grp.getText());
                startActivity(in1);

            }

        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   Toast.makeText(newgrp.this,"You pressed save ",Toast.LENGTH_SHORT).show();
                uid = firebaseUser.getUid();
                grpName = grpname_edit.getText().toString();
                myref = FirebaseDatabase.getInstance().getReference("users").child(uid);
                myref.child("Group").child(grpName).setValue(grpName);

                grp.setText(grpName);
                if (!grpName.isEmpty()) {
                    grpname_edit.setVisibility(View.INVISIBLE);
                    grpimg.setVisibility(View.INVISIBLE);
                }
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
        t = null;

        if (reqCode == Pick_contact) {
            if (ResultCode == AppCompatActivity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) { name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    // String Conc=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String hasPhone =
                            c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));


                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        // Toast.makeText(getApplicationContext(), cNumber, Toast.LENGTH_SHORT).show();
                        t = cNumber;
                    }
                    Toast.makeText(this, "You have picked " + name, Toast.LENGTH_LONG).show();
                    DialogAmt();

                }
            }
        }
    }

    void setCon(String name, String t, int mtotbill) {
        String contactnms = name;
        cost=mtotbill;

        MyList list = new MyList(contactnms, cost, t);
        //list.setPerson(contactnm);
        getLayoutInflater();
        //this.con = Contact;
        namep = name;
        myref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Group");

        myref.child(grpName).child(name).setValue(list);


    }

    private void Retrive() {
        grpref.child("users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    // List list =dataSnapshot.getValue();
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }

                listg.clear();
                listg.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DialogAmt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(newgrp.this);
        View mview = getLayoutInflater().inflate(R.layout.category_dialog, null);
        final EditText totalbill = mview.findViewById(R.id.idtotbill);
        final EditText category = mview.findViewById(R.id.idcat);
        //   mtotbill =totalbill.getText().toString();
       // mcat = category.getText().toString();



        builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mtotbill = Integer.parseInt(totalbill.getText().toString());
                mcat = category.getText().toString();


                if (!mcat.isEmpty()) {
                    Toast.makeText(newgrp.this, "Amount added", Toast.LENGTH_SHORT).show();
                    add();

                } else {
                    Toast.makeText(newgrp.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });


        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setView(mview);
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void add(){
        setCon(name, t, mtotbill);
        Retrive();
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