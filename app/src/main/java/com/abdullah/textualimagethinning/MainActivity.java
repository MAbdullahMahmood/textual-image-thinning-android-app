package com.abdullah.textualimagethinning;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.Ximgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView outputImageview, inputImageview;
    Button btn;
    TextView btnloadImage;
    Bitmap icon;
    ArrayList<Integer> list = new ArrayList();
    int count = 0;
    Bitmap outBitmap, inbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputImageview = findViewById(R.id.output_image);
        inputImageview = findViewById(R.id.input_image);
        btn = findViewById(R.id.btn_ok);
        btnloadImage = findViewById(R.id.btn_load_image);


        btnloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary(btnloadImage);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thinBinaryImageText();
            }
        });

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    try{

//                        thinBinaryImageText();
                    } catch (Exception e) {
                        Log.e("OpenCVActivity", "Error loading cascade", e);
                    }

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void thinBinaryImageText() {

        if (inbitmap != null){
            Mat src = new Mat();
            Utils.bitmapToMat(inbitmap,src);
            Imgproc.cvtColor(src,src ,Imgproc.COLOR_RGB2GRAY);
            //If text is black uncomment below line.
            Core.bitwise_not( src, src );
            Ximgproc.thinning(src,src);
            outBitmap  = Bitmap.createBitmap(src.width(),src.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(src,outBitmap);
            outputImageview.setImageBitmap(outBitmap);
            outputImageview.setVisibility(View.VISIBLE);
        }else {
            outputImageview.setVisibility(View.GONE);
        }

    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");

            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }

    }

    public void openGalary(View view) {
        if(Build.VERSION.SDK_INT>22) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode ==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                inbitmap = BitmapFactory.decodeStream(inputStream);

                inputImageview.setImageBitmap(inbitmap);
                inputImageview.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
