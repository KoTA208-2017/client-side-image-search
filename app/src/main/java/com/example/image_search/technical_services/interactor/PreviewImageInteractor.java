package com.example.image_search.technical_services.interactor;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.example.image_search.contract.PreviewImageContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewImageInteractor implements PreviewImageContract.Interactor {
    public Bitmap rotate(Bitmap sourceBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
        sourceBitmap.recycle();

        return rotatedBitmap;
    }

    public File replaceImageFile(String imagePath, Bitmap bitmap) {
        File file = new File(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

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
