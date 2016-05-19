package com.aransmith.app.receiptlogger.services;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Aran on 2/19/2016.
 * Invokes the TessBaseAPI to perform OCR on an image specified by a bitmap.
 */
public class PerformOCR {

    private Bitmap bitmap;
    private String language;
    private String dataPath;

    /**
     *
     * @param bitmap Representing the image to perform OCR on.
     * @param language The language corresponding to the traineddata file in tessdata/
     * @param dataPath Path ending in tessdata/
     */
    public PerformOCR(Bitmap bitmap, String language, String dataPath){
        this.bitmap = bitmap;
        this.language = language;
        this.dataPath = dataPath;

    }

    /**
     * Performs OCR on the variables set by constructor.
     * @return String containing the OCRd Text from the image.
     */
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
