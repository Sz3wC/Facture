package com.example.fabianszewczyk.test1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button scaner = findViewById(R.id.scanerButton);
        scaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goScaner();
            }
        });

        Button photo = findViewById(R.id.photoButton);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPhoto();
            }
        });



    }

    private void goScaner()
    {
        Intent intent =  new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void goPhoto()
    {
        Intent intent =  new Intent(this, PhotoActivity.class);
        startActivity(intent);

    }



}
