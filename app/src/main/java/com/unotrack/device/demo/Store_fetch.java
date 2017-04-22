package com.unotrack.device.demo;

/**
 * Created by aditya on 9/4/17.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Store_fetch extends AppCompatActivity {
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_item);
        Intent intent = getIntent();
        String item = intent.getStringExtra("selected-item");
    }
    public void fetch()
    {
        db=new DatabaseHandler(this);
        TextView key =(TextView)findViewById(R.id.name);
        //EditText value =(EditText)findViewById(R.id.devicename);
        StoredData sd=db.getEntry(key.getText().toString());

        //value.setText(sd.getvalue());
        //return sd.getvalue();

    }
    public void store(View view)
    {
        db=new DatabaseHandler(this);
        TextView key =(TextView) findViewById(R.id.name);
        //EditText value =(EditText)findViewById(R.id.devicename);
        //db.addEntry(new StoredData(key.getText().toString(), value.getText().toString()));

    }
}
