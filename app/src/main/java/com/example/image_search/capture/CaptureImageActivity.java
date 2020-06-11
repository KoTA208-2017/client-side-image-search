package com.example.image_search.capture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.image_search.preview.PreviewImageActivity;
import com.example.image_search.R;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaptureImageActivity extends AppCompatActivity implements CaptureImageContract.View, ImageButton.OnClickListener {
    private CaptureImagePresenter capturePresenter;

    ImageButton captureBtn, galleryBtn;
    TextureView textureView;
    private String imagePath;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest.Builder captureRequestBuilder;

    private int previewWidth;
    private int previewHeight;
    private File file;
    Handler nBackgroundHandler;
    HandlerThread nBackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_capture_image);

        capturePresenter = new CaptureImagePresenter(this);
        initViews();
    }

    private void initViews(){
        textureView = findViewById(R.id.textureView);
        captureBtn = findViewById(R.id.captureBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        textureView.setSurfaceTextureListener(textureListener);

        captureBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.captureBtn:
                try {
                    takePicture();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.galleryBtn:
                //Intent to gallery
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    //From Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();

            // open preview activity
            intentToPreviewActivity();
        }
    }

    // open preview activity
    @Override
    public void intentToPreviewActivity() {
        Intent mIntent = new Intent(CaptureImageActivity.this, PreviewImageActivity.class);
        mIntent.putExtra("IMAGE_PATH", imagePath);
        startActivity(mIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "Sorry, camera permission is necessary", Toast.LENGTH_LONG).show();
            }
        }
    }

    TextureView.SurfaceTextureListener textureListener =  new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            //open camera
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
    };

    private final CameraDevice.StateCallback stateCallback =  new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;

            //camera preview
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    @Override
    public void createCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(previewHeight, previewWidth);
        Surface surface = new Surface(texture);

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
            @Override
            public void onConfigured(CameraCaptureSession session) {
                if(cameraDevice==null) {
                    return;
                }

                cameraCaptureSession = session;

                // update preview
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                Toast.makeText(getApplicationContext(), "Confoguration Changed", Toast.LENGTH_LONG).show();
            }
        }, null);

    }

    private void updatePreview() throws CameraAccessException {
        if(cameraDevice == null) {
            return;
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, nBackgroundHandler);
    }

    @Override
    public void openCamera() throws CameraAccessException {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        cameraId = manager.getCameraIdList()[0];

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        // get screen size of device
        getPreferredPreviewSize();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CaptureImageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return;
        }

        //manager openCamera
        manager.openCamera(cameraId, stateCallback, null);
    }

    private void getPreferredPreviewSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        previewWidth = size.x;
        previewHeight = size.y;
    }

    @Override
    public void takePicture() throws CameraAccessException {
        if(cameraDevice == null) {
            return;
        }

        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSizes = null;

        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        final int img_width = previewHeight;
        final int img_height = previewWidth;

        ImageReader reader = ImageReader.newInstance(img_width,img_height,ImageFormat.JPEG, 1);

        List<Surface> outputSurfaces = new ArrayList<>(2);
        outputSurfaces.add(reader.getSurface());
        outputSurfaces.add(new Surface(textureView.getSurfaceTexture()) );

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , ts +".jpg");
        imagePath = file.getAbsolutePath();

        ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Image image = null;

                image = imageReader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                try {
                    //save the image
                    capturePresenter.saveImage(bytes, file);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(image != null) {
                        image.close();
                    }
                }
            }
        };

        reader.setOnImageAvailableListener(readerListener, nBackgroundHandler);

        final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                try {
                    createCameraPreview();
                    // open preview activity
                    intentToPreviewActivity();

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };

        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                try {
                    cameraCaptureSession.capture(captureBuilder.build(), captureListener, nBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) { }

        }, nBackgroundHandler);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();

        if(textureView.isAvailable()) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    private void startBackgroundThread() {
        nBackgroundThread = new HandlerThread("Camera Background");
        nBackgroundThread.start();
        nBackgroundHandler = new Handler(nBackgroundThread.getLooper());
    }

    @Override
    protected void onPause() {
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    protected void stopBackgroundThread() throws InterruptedException {
        nBackgroundThread.quitSafely();

        nBackgroundThread.join();
        nBackgroundThread = null;
        nBackgroundHandler = null;
    }

}
