package com.aransmith.app.receiptlogger.services;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Aran on 2/19/2016.
 */
public class PerformOCR {

    private Bitmap bitmap;
    private String language;
    private String dataPath;

    public PerformOCR(Bitmap bitmap, String language, String dataPath){
        this.bitmap = bitmap;
        this.language = language;
        this.dataPath = dataPath;

    }

    public String performOCR(){

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(dataPath, language);
        baseApi.setImage(bitmap);

        String expenseText = baseApi.getUTF8Text();
        baseApi.end();

        return expenseText;
    }
}
