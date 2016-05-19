package com.aransmith.app.receiptlogger.services;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.aransmith.app.receiptlogger.dao.WebServiceAccess;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;

import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;

/**
 * Created by Aran on 2/20/2016.
 *
 */
public class FileSystemService {

    private WebServiceAccess webServiceAccess;
    private static final String TAG = "FileSystemService";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() +
            "/AutoLogExpense/";
    public static final String lang = "eng";


    public boolean createDirectories(String[] paths){
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return false;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }
        return true;
    }

    public Bitmap retrieveImage(String email, String password, int id){

        File file = new File(DATA_PATH + id + ".png");

        if (!file.exists()) {
            // obtain image from webservice
            webServiceAccess = new WebServiceAccess();
            ExpenseRetrievalResponse expenseRetrievalResponse  =
                    webServiceAccess.retreiveExpenseByID(email, password, id);

            if(expenseRetrievalResponse.isSuccess()){
                String base64 = expenseRetrievalResponse.getCompressedImageData().element();
                byte[] compressedBytes = Base64Utils.decode(base64);

                byte[] imageData = null;

                try {
                    imageData = CompressionUtils.decompress(compressedBytes);

                    FileOutputStream out = new FileOutputStream(file);

                    out.write(imageData);
                    out.flush();
                    out.close();

                } catch (DataFormatException e){
                    e.printStackTrace();
                    return null;
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

        if(file.exists()) {
            return BitmapFactory.decodeFile(DATA_PATH + id + ".png");
        }

        return null;
    }

    public boolean storeTrainedData(AssetManager assetManager) throws IOException{
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
            OutputStream out = new FileOutputStream(DATA_PATH
                    + "tessdata/" + lang + ".traineddata");

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            Log.v(TAG, "Copied " + lang + " traineddata");
        }

        return true;
    }

    public static String getStorageDirectory(){
        return Environment.getExternalStorageDirectory().toString() +
                "/AutoLogExpense/";
    }

    public static int requestWriteStorageCode(){
        return 112;
    }
}
