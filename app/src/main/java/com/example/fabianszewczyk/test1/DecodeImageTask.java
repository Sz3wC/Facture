package com.example.fabianszewczyk.test1;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import java.io.IOException;
import java.util.Arrays;

public class DecodeImageTask extends AsyncTask<Void, Void, Void> {

    public interface GetDecodeImageListener {
        void onImageDecoded(String result);
    }

    private byte[] image;
    private GetDecodeImageListener listener;
    private String result;
    private Vision vision;

    public DecodeImageTask(byte[] image, Vision vision, GetDecodeImageListener listener) {
        this.image = image;
        this.vision = vision;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Image inputImage = new Image();
        inputImage.encodeContent(image);

        Feature desiredFeature = new Feature();
        desiredFeature.setType("DOCUMENT_TEXT_DETECTION");

        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage);
        request.setFeatures(Arrays.asList(desiredFeature));

        BatchAnnotateImagesRequest batchRequest =
                new BatchAnnotateImagesRequest();

        batchRequest.setRequests(Arrays.asList(request));

        try {
            BatchAnnotateImagesResponse batchResponse =
                    vision.images().annotate(batchRequest).execute();

            final TextAnnotation text = batchResponse.getResponses()
                    .get(0).getFullTextAnnotation();
            result = text.getText();
        } catch (IOException e) {
            Log.e("Error",e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onImageDecoded(result);
    }
}
