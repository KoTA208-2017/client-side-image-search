package com.example.image_search.crop;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropImageInteractor implements CropImageContract.Interactor{

    @Override
    public File createTempFile(long milis, Bitmap bitmap, android.content.Context context){
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , milis +"_image.jpg");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70, bos);

        //write the bytes in file
        byte[] bitmapdata = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
