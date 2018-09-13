package com.example.fabianszewczyk.test1;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lib.folderpicker.FolderPicker;


public class Activity2 extends AppCompatActivity {

    private String directoryPath;
    private String dataForSave = "";
    EditText stationName;
    EditText factureName;
    EditText sumValue;
    EditText companyName;
    EditText NIP;
    EditText date;
    EditText Vat;
    EditText Netto;
    EditText invoiceFor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        stationName = findViewById(R.id.stationName2);
        factureName = findViewById(R.id.factureName2);
        sumValue = findViewById(R.id.sumValue2);
        companyName = findViewById(R.id.companyName2);
        NIP = findViewById(R.id.NIP2);
        date = findViewById(R.id.date2);
        Vat = findViewById(R.id.VatValue2);
        Netto = findViewById(R.id.NettoValue2);
        invoiceFor = findViewById(R.id.invoiceFor2);

        stationName.setText(MainActivity.receipt[8]);
        factureName.setText(MainActivity.receipt[0]);
        sumValue.setText(MainActivity.receipt[1]);
        companyName.setText(MainActivity.receipt[2]);
        NIP.setText(MainActivity.receipt[3]);
        date.setText(MainActivity.receipt[4]);
        Vat.setText(MainActivity.receipt[5]);
        Netto.setText(MainActivity.receipt[6]);
        invoiceFor.setText(MainActivity.receipt[7]);
        String Faktura = MainActivity.receipt[0];
        String Suma = MainActivity.receipt[1];
        String Nabywca = MainActivity.receipt[2];

        Button saveToFile = findViewById(R.id.saveToFile2);
        saveToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    public boolean checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            return true;
        } else {
            getDirectory();
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDirectory();
            }
        } else {
            checkPermissions();
        }
    }

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        Intent intent = new Intent(Activity2.this , MainActivity.class);
        startActivity(intent);
    }

    public void getDirectory() {
        Intent intent = new Intent(this, FolderPicker.class);
        intent.putExtra("location", Environment.getExternalStorageDirectory().getAbsolutePath());
        startActivityForResult(intent, 999);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            writeExternalStorage(intent.getExtras().getString("data"));
        }
    }

    public void writeExternalStorage (String path)
    {
        String FILENAME = "faktury.csv";
        dataForSave = "Stacja:;Numer faktury:;Faktura za:;Netto:;VAT:;Brutto:;Nazwa firmy:;NIP:;Data/Nr wydruku:;\n";
        dataForSave += stationName.getText().toString() + ";";
        dataForSave += factureName.getText().toString() + ";";
        dataForSave += invoiceFor.getText().toString() + ";";
        dataForSave += Netto.getText().toString() + ";";
        dataForSave += Vat.getText().toString() + ";";
        dataForSave += sumValue.getText().toString() + ";";
        dataForSave += companyName.getText().toString() + ";";
        dataForSave += NIP.getText().toString() + ";";
        dataForSave += date.getText().toString() + ";";

        try
        {
            File mDirectory = new File(path);
            FileOutputStream out = new FileOutputStream(new File(mDirectory, FILENAME));
            byte[] bytes = dataForSave.getBytes("UTF-8");
            out.write(bytes);
            String text = new String(bytes, "UTF-8");
            out.close();
            Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Saved fail",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Saved fail2",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
