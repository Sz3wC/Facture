package com.example.fabianszewczyk.test1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SurfaceView cameraViev;
    TextView textView;
    TextView textNumberStatus;
    TextView NazwaStacjiNo;
    TextView NrFakturyNo;
    TextView SumaNo;
    TextView FirmaNo;
    TextView NIPNo;
    TextView DataNrWydrukuNo;
    TextView VATNoFound;
    TextView NettoNoFound;
    TextView FakturaZa;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    public static String[] receipt = new String[9];
    private boolean stationFound = false;
    private String typeOfStation = "";
    private boolean[] elementsFound = new boolean[9];
    private int howManyFound = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                try {
                    cameraSource.start(cameraViev.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textNumberStatus = findViewById(R.id.status);
        cameraViev = findViewById(R.id.surface_viev);
        textView = findViewById(R.id.text_view);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraViev.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraViev.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); i++) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    //stringBuilder.append("\n");
                                }
                                if (!stationFound) {
                                    stationFound = whichStation(stringBuilder.toString());
                                } else if (howManyFound < 9){
                                    findText(stringBuilder.toString());
                                }
                                textView.setText(stringBuilder.toString());
                                String x = String.valueOf(howManyFound) + "/9";
                                textNumberStatus.setText(x);
                            }
                        });

                    }

                }
            });
        }
    }

    public void Make_Photo(View view) {
        String textToA2 = textView.getText().toString();
        Intent myIntent = new Intent(MainActivity.this, Activity2.class);
        myIntent.putExtra("key", textToA2);
        startActivity(myIntent);
    }


    private boolean whichStation(String text) {
        String textLowerCase = text.replace(" ", "").toLowerCase();
        if (textLowerCase.contains("bpeuropa")) {
            typeOfStation = "bpeuropa";
            receipt[8] = "BP Europa";
            String x = receipt[8];
            howManyFound++;
            NazwaStacjiNo = findViewById(R.id.NazwaStacjiNoFound);
            NazwaStacjiNo.setText("");
            return true;
        } else if (textLowerCase.contains("orlen")) {
            typeOfStation = "orlen";
            receipt[8] = "Orlen";
            String x = receipt[8];
            howManyFound++;
            NazwaStacjiNo = findViewById(R.id.NazwaStacjiNoFound);
            NazwaStacjiNo.setText("");
            return true;
        }
        return false;
    }

    private void findText(String text) {
        text = text.replace("\n", "");
        String textLowerCase = text.replace(" ", "").toLowerCase();
        switch (typeOfStation) {
            case "bpeuropa":
                if (textLowerCase.contains("fakturavatnr") && !elementsFound[0]) {
                    int i = textLowerCase.indexOf("fakturavatnr");
                    i = i + 13;
                    if (textLowerCase.length() > i + 15) {
                        receipt[0] = textLowerCase.substring(i, i + 15);
                        String x = receipt[0];
                        elementsFound[0] = true;
                        howManyFound++;
                        NrFakturyNo = findViewById(R.id.NumerFakturyNoFound);
                        NrFakturyNo.setText("");
                    }
                } else if (textLowerCase.contains("brutto") && !elementsFound[1]) {
                    int i = textLowerCase.indexOf("brutto");
                    i = i + 6;
                    if (textLowerCase.length() > i + 5) {
                        receipt[1] = textLowerCase.substring(i, i + 5);
                        String x = receipt[1];
                        String y = x;
                        elementsFound[1] = true;
                        howManyFound++;
                        SumaNo = findViewById(R.id.SumaNoFound);
                        SumaNo.setText("");
                    }
                } else if (text.toUpperCase().contains("ATOS GLOBAL") && !elementsFound[2]) {
                    int i = text.toUpperCase().indexOf("ATOS GLOBAL");
                    if (text.length() > i + 45) {
                        receipt[2] = text.substring(i, i + 45);
                        String x = receipt[2];
                        elementsFound[2] = true;
                        howManyFound++;
                        FirmaNo = findViewById(R.id.FirmaNoFound);
                        FirmaNo.setText("");
                    }
                } else if (text.toUpperCase().contains("NIP") && !elementsFound[3]) {
                    int i = text.toUpperCase().indexOf("NIP");
                    i = i + 3;
                    if (text.length() > i + 14) {
                        receipt[3] = text.substring(i, i + 14);
                        String x = receipt[3];
                        elementsFound[3] = true;
                        howManyFound++;
                        NIPNo = findViewById(R.id.NIPnoFound);
                        NIPNo.setText("");
                    }
                } else if (text.toLowerCase().contains("nr wydr.") && !elementsFound[4]) {
                    int i = text.toLowerCase().indexOf("nr wydr.");
                    i = i + 8;
                    if (text.length() > i + 6) {
                        receipt[4] = text.substring(i, i + 6);
                        String x = receipt[4];
                        elementsFound[4] = true;
                        howManyFound++;
                        DataNrWydrukuNo = findViewById(R.id.DataNrWydrukuNoFound);
                        DataNrWydrukuNo.setText("");
                    }


                } else if (text.toLowerCase().contains("vat") && !elementsFound[5]) {
                    int i = text.toLowerCase().indexOf("vat");
                    i = i + 3;
                    if (text.length() > i + 5) {
                        receipt[5] = text.substring(i, i + 5);
                        String x = receipt[5];
                        elementsFound[5] = true;
                        howManyFound++;
                        VATNoFound = findViewById(R.id.VATNoFound);
                        VATNoFound.setText("");
                    }
                } else if (text.toLowerCase().contains("netto") && !elementsFound[6]) {
                    int i = text.toLowerCase().indexOf("netto");
                    i = i +5;

                    if (text.length() > i+5) {
                        receipt[6] = text.substring(i, i + 5);
                        String x = receipt[6];
                        elementsFound[6] = true;
                        howManyFound++;
                       NettoNoFound = findViewById(R.id.NettoNoFound);
                       NettoNoFound.setText("");
                    }

                } else if (text.toLowerCase().contains("act")&& !elementsFound[7]) {
                    String y ;
                    if(text.toLowerCase().contains("myjnia"))
                    {

                        y = " I MYJNIA";

                    }else
                    {
                        y="";
                    }

                        receipt[7] = "PALIWO" +y;
                        String x = receipt[7];
                        elementsFound[7] = true;
                        howManyFound++;
                        FakturaZa = findViewById(R.id.ZaCoNoFound);
                        FakturaZa.setText("");




                }else if (text.toLowerCase().contains("myjnia")&& !elementsFound[7]) {
                    String y;
                    if (text.toLowerCase().contains("act")) {

                        y = " I PALIWO";

                    } else {
                        y = "";
                    }
                    int i = text.toLowerCase().indexOf("myjnia");
                    if (text.length() > i + 6) {
                        receipt[7] = text.substring(i, i + 6) + y;
                        String x = receipt[7];
                        elementsFound[7] = true;
                        howManyFound++;
                        FakturaZa = findViewById(R.id.ZaCoNoFound);
                        FakturaZa.setText("");
                    }
                }

                break;

            case "orlen":
                if (textLowerCase.contains("numer:") && !elementsFound[0]) {
                    int i = textLowerCase.indexOf("numer:");
                    i = i + 14;
                    if (textLowerCase.length() > i + 13) {
                        receipt[0] = textLowerCase.substring(i, i + 13);
                        String x = receipt[0];
                        elementsFound[0] = true;
                        howManyFound++;
                        NrFakturyNo = findViewById(R.id.NumerFakturyNoFound);
                        NrFakturyNo.setText("");
                    }
                } else if (text.toLowerCase().contains("wart.brutto") && !elementsFound[1]) {
                    int i = text.toLowerCase().indexOf("wart.brutto");
                    i = i + 12;
                    if (text.length() > i + 5) {
                        receipt[1] = text.substring(i, i + 5);
                        String x = receipt[1];
                        String y = x;
                        elementsFound[1] = true;
                        howManyFound++;
                        SumaNo = findViewById(R.id.SumaNoFound);
                        SumaNo.setText("");
                    }
                } else if (text.toUpperCase().contains("ATOS GDC") && !elementsFound[2]) {
                    int i = text.toUpperCase().indexOf("ATOS GDC");
                    if (text.length() > i + 45) {
                        receipt[2] = text.substring(i, i + 45);
                        String x = receipt[2];
                        elementsFound[2] = true;
                        howManyFound++;
                        FirmaNo = findViewById(R.id.FirmaNoFound);
                        FirmaNo.setText("");
                    }
                } else if (text.toUpperCase().contains("NIP") && !elementsFound[3]) {
                    int i = text.toUpperCase().indexOf("NIP");
                    i = i + 3;
                    if (text.length() > i + 14) {
                        receipt[3] = text.substring(i, i + 14);
                        String x = receipt[3];
                        elementsFound[3] = true;
                        howManyFound++;
                        NIPNo = findViewById(R.id.NIPnoFound);
                        NIPNo.setText("");
                    }
                } else if (text.toLowerCase().contains("wienia:") && !elementsFound[4]) {
                    int i = text.toLowerCase().indexOf("wienia:");
                    i = i + 8;
                    if (text.length() > i + 10) {
                        receipt[4] = text.substring(i, i + 10);
                        String x = receipt[4];
                        elementsFound[4] = true;
                        howManyFound++;
                        DataNrWydrukuNo = findViewById(R.id.DataNrWydrukuNoFound);
                        DataNrWydrukuNo.setText("");
                    }
                } else if (text.toLowerCase().contains("suma") && !elementsFound[5]) {
                    int i = text.toLowerCase().indexOf("suma");
                    i = i - 4;
                    if (i>0) {
                        receipt[5] = text.substring(i, i + 4);
                        String x = receipt[5];
                        elementsFound[5] = true;
                        howManyFound++;
                        VATNoFound = findViewById(R.id.VATNoFound);
                        VATNoFound.setText("");
                    }
                } else if (text.toLowerCase().contains("23.00") && !elementsFound[6]) {
            int i = text.toLowerCase().indexOf("23.00");
            i = i -5;
            if (i>0) {
                receipt[6] = text.substring(i, i + 5);
                String x = receipt[6];
                elementsFound[6] = true;
                howManyFound++;
                NettoNoFound = findViewById(R.id.NettoNoFound);
                NettoNoFound.setText("");
            }

        } else if (text.toLowerCase().contains("act")&& !elementsFound[7]) {
                    String y ;
                    if(text.toLowerCase().contains("myjnia"))
                    {

                        y = " I MYJNIA";

                    }else
                    {
                        y="";
                    }

                    receipt[7] = "PALIWO" +y;
                    String x = receipt[7];
                    elementsFound[7] = true;
                    howManyFound++;
                    FakturaZa = findViewById(R.id.ZaCoNoFound);
                    FakturaZa.setText("");




                }else if (text.toLowerCase().contains("myjnia")&& !elementsFound[7]) {
                    String y;
                    if (text.toLowerCase().contains("act")) {

                        y = " I PALIWO";

                    } else {
                        y = "";
                    }
                    int i = text.toLowerCase().indexOf("myjnia");
                    if (text.length() > i + 6) {
                        receipt[7] = text.substring(i, i + 6) + y;
                        String x = receipt[7];
                        elementsFound[7] = true;
                        howManyFound++;
                        FakturaZa = findViewById(R.id.ZaCoNoFound);
                        FakturaZa.setText("");
                    }
                }

                break;
        }
        for (int i = 0;



             i < elementsFound.length; i++) {
            if (!elementsFound[i]) {
                break;
            } else if (elementsFound[i] && i == 7) {
                Intent myIntent = new Intent(MainActivity.this, Activity2.class);
                startActivity(myIntent);
            }
        }
    }

}
