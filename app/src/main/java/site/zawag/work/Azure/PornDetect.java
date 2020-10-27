package site.zawag.work.Azure;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

public class PornDetect{
    public static final String ADULT = "Adult";
    public static final String RACY = "Racy";
    public static final String GOOD = "Good";
    VisionServiceClient visionServiceClient =
            new VisionServiceRestClient("e96d35837a9f4401adca43982c671977",
                    "https://zawag.cognitiveservices.azure.com/vision/v2.0");

    private OnPornListener onPornListener;

    public PornDetect(OnPornListener onPornListener) {
        this.onPornListener = onPornListener;
    }

    public void startScan(File file)  {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pornDetectAsync.execute(inputStream);
    }


    AsyncTask<InputStream, String, String> pornDetectAsync = new AsyncTask<InputStream, String, String>() {
        @Override
        protected String doInBackground(InputStream... byteArrayInputStreams) {

            String[] features = {"Adult"};
            String[] details = {};

            try {
                AnalysisResult analysisResult = visionServiceClient.analyzeImage(byteArrayInputStreams[0], features, details);
                String  jsonResult = new Gson().toJson(analysisResult);

                return jsonResult;
            } catch (VisionServiceException e) {
                e.printStackTrace();
                return "(IO) "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "(ERROR) "+e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("IO") || s.contains("ERROR")){
                onPornListener.onDetectionFailed(s);
            }else {
                AnalysisResult analysisResult = new Gson().fromJson(s, AnalysisResult.class);
                if (analysisResult.adult.isAdultContent){
                    onPornListener.OnDetectionSuccess(analysisResult.adult.adultScore, PornDetect.ADULT);
                }else if(analysisResult.adult.isRacyContent){
                    onPornListener.OnDetectionSuccess(analysisResult.adult.adultScore, PornDetect.RACY);
                }else {
                    onPornListener.OnDetectionSuccess(1, PornDetect.GOOD);

                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
    };
}
