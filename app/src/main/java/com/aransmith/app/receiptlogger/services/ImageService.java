package com.aransmith.app.receiptlogger.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by Aran on 4/19/2016.
 */
public class ImageService {
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() +
            "/ImageService/";
    private Bitmap bitmap;
    FileOutputStream out;
    String pngPath;

    public ImageService(){
        out = null;
        pngPath = DATA_PATH + "converted.png";
    }

    public byte[] getPNGDataFromJPEG(String path){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFile(path, options);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            return stream.toByteArray();

        } catch ( Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
