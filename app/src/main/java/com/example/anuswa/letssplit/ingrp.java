package com.example.anuswa.letssplit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuswa.letssplit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.example.anuswa.letssplit.R.layout.activity_ingrp;

public class ingrp extends AppCompatActivity {

    private TextView grptxt,total_text,result1_text;
    String grpnm;
    FirebaseAuth mAuth;
    DatabaseReference grpref;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> listg = new ArrayList<>();
    private ListView listgrp;
    String name1;
    String ConName,uid;
    private final int Pick_contact = 1;
    private Button addmem;
    private DatabaseReference myref;
    private Button back,send;
    public int var;
    String  grpname,sresult,key;
    String[] personname = new String[10];
    String[] persondebt = new String[10];
    int count=1,x=0,total,result;
    String mtotbill,mcat,contactnm;

    Button equal,next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_ingrp);
        Intent i = getIntent();
        total = i.getExtras().getInt("total");
        result = i.getExtras().getInt("result");
        grptxt = findViewById(R.id.grptxt_id);
        listgrp = findViewById(R.id.ingrplist_id);
        //equal = findViewById(R.id.equ_id);

        total_text = findViewById(R.id.total);
        result1_text = findViewById(R.id.result);
        send = findViewById(R.id.snd_id);
        back = findViewById(R.id.bk_id);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ingrp.this,crtgrp.class);
                startActivity(in);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:8237469759,smsto:7350399877");
             //   Uri uri1 = Uri.parse("smsto:8237469759");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("Text message","The SMS text ");
                startActivity(it);
            }
        });
        /*listgrp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent in = new Intent(ingrp.this,Report.class);
                in.putExtra("perName",listgrp.getItemAtPosition(position).toString());
                startActivity(in);
            }
        });*/

        //addmem = findViewById(R.id.addmem_id);

        total_text.setText("Total Amount is: Rs"+Integer.toString(total));
        result1_text.setText("Per Person: Rs"+Integer.toString(result));

        grpref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        arrayAdapter = new ArrayAdapter<String>(ingrp.this, android.R.layout.simple_list_item_1, listg);
        listgrp.setAdapter(arrayAdapter);


        Bundle b = getIntent().getExtras();
        grpnm = b.getString("gnm");
        if (b != null) {
            grptxt.setText(grpnm);

            Retrive();
        }


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

                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    //String Conc=c.getString(c.getColumnIndexOrThrow(ContactsContract))
                    Toast.makeText(this, "You have picked " + name, Toast.LENGTH_LONG).show();
                    setCon(name);
                    //Retrive();
                }
            }
        }
    }

    void setCon(String name) {
        Toast.makeText(this, "Yayyyyyy", Toast.LENGTH_LONG).show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //  String contactnm = name;
        //String connum = Contact;
        this.ConName = name;
        name1 = name;
        // MyList list = new MyList();
        //this.con = Contact;
        myref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Group");

        myref.child(grpnm).child(name);


    }

    private void Retrive() {
        grpref.child("users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpnm).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                MyList list = new MyList();
                String temp;

                while (iterator.hasNext()) {
                    list = ((DataSnapshot)iterator.next()).getValue(MyList.class);
                    temp = list.getPerson() + "     "+list.getCost();
                    var = list.getCost();
                    set.add(temp);
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

    private void DisplayMembers()
    {

        myref=FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpname).child("Members");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                AmountInfo amountInfo=new AmountInfo(contactnm,sresult);

                int i=0;
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

                    amountInfo.setPersonname(ds.getValue(AmountInfo.class).getPersonname());
                    amountInfo.setPricee(ds.getValue(AmountInfo.class).getPricee());
                    i=i+1;

                    personname[i]=amountInfo.getPersonname();
                    persondebt[i]=amountInfo.getPricee();

                    if(x>0) {

                        Query ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpname).child("Members").orderByChild("personname").equalTo(contactnm);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                                    key = childsnap.getKey();
                                    update(key, sresult);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void update (String key , String sresult)
    {
        myref=FirebaseDatabase.getInstance().getReference();
        myref=myref.child("Users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpname).child("Members");
        myref.child(key).child("pricee").setValue(sresult);

        // grpref.child("users").child(mAuth.getCurrentUser().getUid()).child("Group").child(grpName).addValueEventListener(new ValueEventListener() {


    }



}