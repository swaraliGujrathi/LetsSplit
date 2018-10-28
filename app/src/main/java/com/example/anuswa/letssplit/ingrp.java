package com.example.anuswa.letssplit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
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

                String phoneNumber = "7350399877";
                String phoneNumber1 = "8237469759";
                String phoneNumber2 = "8237163437";
                String smsBody = "Hello guys!! Our total expenses are :"+total+" & per person split is: "+result+". This message is to remind you to pay your fare.";

                String SMS_SENT = "SMS_SENT";
                String SMS_DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPendingIntent = PendingIntent.getBroadcast(ingrp.this, 0, new Intent(SMS_SENT), 0);
                PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(ingrp.this, 0, new Intent(SMS_DELIVERED), 0);

               registerReceiver(new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                break;
                            /*case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                break;*/
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                break;
                            /*case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                break;*/
                        }
                    }
                }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_DELIVERED));
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
                smsManager.sendTextMessage(phoneNumber1, null, smsBody, sentPendingIntent, deliveredPendingIntent);
                smsManager.sendTextMessage(phoneNumber2, null, smsBody, sentPendingIntent, deliveredPendingIntent);


            }
        });

        total_text.setText("Total Amt: Rs"+Integer.toString(total));
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