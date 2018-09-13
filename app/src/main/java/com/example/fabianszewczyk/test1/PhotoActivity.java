package com.example.fabianszewczyk.test1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class PhotoActivity extends AppCompatActivity {
    Uri imageUri;
    Vision vision;
    String decodedText;
    TextView readFacture1;
    String typeOfStation;
   EditText stationName;
    EditText factureName;
    EditText sumValue;
    EditText companyName;
    EditText NIP;
    EditText date;
    EditText Vat;
    EditText Netto;
    EditText invoiceFor;
    Calendar year;
    boolean stationFound = false;
    boolean witchStation = false;
    private Bitmap bitmap;
    private boolean[] elementsFound = new boolean[9];
    public static String[] receipt = new String[9];
    private static final int PICK_IMAGE = 100;
    String[] resultTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null
        );

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyB3_mz_NIq35vItFYjXZNEgByBTH296VRY"));

        vision = visionBuilder.build();

        openGallery();

        stationName = findViewById(R.id.stationName);
        factureName = findViewById(R.id.factureName);
        sumValue = findViewById(R.id.sumValue);
        companyName = findViewById(R.id.companyName);
        NIP = findViewById(R.id.NIP);
        date = findViewById(R.id.tdate);
        Vat = findViewById(R.id.VatValue);
        Netto = findViewById(R.id.NettoValue);
        invoiceFor = findViewById(R.id.invoiceFor);
        year = Calendar.getInstance();


    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    public void decodeImage()
    {
        DecodeImageTask decodeImageTask = new DecodeImageTask(getBytesFromBitmap(bitmap), vision, new DecodeImageTask.GetDecodeImageListener() {
            @Override
            public void onImageDecoded(String result) {
                decodedText = result;
            if(!witchStation){
                witchStation();
            }
            if(stationFound) {
                readFacture1 = findViewById(R.id.readFacture);
                readFacture1.setText("");

                   findText();


                uzupelnij();
            }
            }
        });
        decodeImageTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if(bitmap != null) {
                    decodeImage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public void witchStation()

    {

        String textLowerCase = decodedText.replace(" ", "").toLowerCase();
        if (textLowerCase.contains("bpeuropa")&& !elementsFound[8]) {
            stationFound = true;
            typeOfStation = "BP";
            receipt[8] = "BP Europa";

            witchStation = true;
        }
        else if (textLowerCase.contains("orlen")&& !elementsFound[8])
        {
            stationFound = true;
            typeOfStation = "orlen";
            receipt[8] = "Orlen";

            witchStation = true;
        }
        else
        {
            readFacture1 = findViewById(R.id.readFacture);
            readFacture1.setText("Prosze podac fakture z BP lub Orlenu");
        }
    }

    public  void findText()
    {

        int currentYear = year.get(Calendar.YEAR);
        String yearNow = String.valueOf(currentYear);
        String[] result = decodedText.toLowerCase().split("\n");


        switch (typeOfStation){
            case "orlen":
                for (int i=0;i<result.length;i++) {
                    if (result[i].contains("numer:") && !elementsFound[0]) {
                        if (result[i].length() >= 20) {
                            receipt[0] = result[i].substring(6, 20);
                            elementsFound[0] = true;

                        }
                    } else if (result[i].contains("suma:") && !elementsFound[1] && !elementsFound[5] && !elementsFound[6]) { // wartość brutto
                        if (i >= 1) {
                            receipt[1] = result[i - 1];
                            elementsFound[1] = true;

                        }
                        if (i >= 3) {
                            receipt[6] = result[i - 2];
                            elementsFound[6] = true;

                        }
                        if (i >= 4) {
                            receipt[5] = result[i - 3];
                            elementsFound[5] = true;

                        }
                    } else if (result[i].contains("atos") && !elementsFound[2]) {
                        if (result.length >= i + 1) {
                            receipt[2] = result[i].toUpperCase();
                            elementsFound[2] = true;

                        }
                    } else if (result[i].contains("nip") && !elementsFound[3]) {
                        if (result[i].length() <= 15) {
                            receipt[3] = result[i].substring(5, 15);
                            elementsFound[3] = true;

                        }
                    } else if (result[i].contains("data") && !elementsFound[4]) {
                        if (result[i].length() >= 34) {
                            receipt[4] = result[i].substring(18, 34);
                            elementsFound[4] = true;

                        }

                    }else if (result[i].contains("myjnia") && !elementsFound[7]) {
                        if (result[i].length() >= 43) {
                            receipt[7] = result[i].substring(0, 6);
                            elementsFound[7] = true;

                        }
                    }

                }
                break;
            case "BP":

               /* for(int i=0;i<result.length ;i++) // tablica liczb
                {


                }
                */

                for (int i=0;i<result.length;i++)
            {
                if(result[i].contains("faktura vat") && !elementsFound[0])
                {
                    if(result[i].length() >= 16) {
                        receipt[0] = result[i].substring(16, 31);
                        elementsFound[0] = true;

                    }
                } else if(result[i].contains("suma pln") && !elementsFound[1]) { // wartość brutto
                    if(result.length> i+1) {
                        receipt[1] = result[i + 1];
                        elementsFound[1] = true;
                        String suma = String.valueOf(receipt[1]);
                        int brutto;

                        if(i >= 5) {
                            receipt[6] = result[i-4];   //vat
                            elementsFound[5] = true;

                            if(i >= 3) {
                                receipt[5] = result[i-5];
                                elementsFound[6] = true;    //netto

                            }
                        }

                    }

                } else if (result[i].contains("atos") && !elementsFound[2]) {
                    if(result.length >= i+1) {
                        receipt[2] = result[i].toUpperCase();
                        elementsFound[2] = true;

                    }
                } else if (result[i].contains("nip") && !elementsFound[3] && !elementsFound[4]) {
                    if(result[i].length() >= 15) {
                        receipt[3] = result[i].substring(4, 17);
                        elementsFound[3] = true;

                    }
                    }else if (result[i].contains(yearNow) && !elementsFound[4]) {
                        if (result[i].length() >= 7) {
                            receipt[4] = result[i];
                            elementsFound[4] = true;

                        }

                } else if (result[i].contains("Nabywca:") && !elementsFound[5]) { //wartosc netto

                }
                else if (result[i].contains("myjnia") && !elementsFound[7]) {
                    if (result[i].length() >= 43) {
                        receipt[7] = result[i].substring(1, 14).toUpperCase();
                        elementsFound[7] = true;

                    }
                }
                else if (result[i].contains("act") && !elementsFound[7]) {
                    if (result[i].length() >= 4) {
                        receipt[7] = result[i];
                        elementsFound[7] = true;

                    }
                }

            }
                    break;
        }
    }

    public void uzupelnij(){
        stationName.setText(receipt[8]);
        factureName.setText(receipt[0]);
        sumValue.setText(receipt[1]);
        companyName.setText(receipt[2]);
        NIP.setText(receipt[3]);
        date.setText(receipt[4]);
        Vat.setText(receipt[6]);
        Netto.setText(receipt[5]);
        invoiceFor.setText(receipt[7]);

    }
}
