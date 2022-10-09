package com.gbtouch.pdfdemo;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] pdfFiles;
    PdfiumCore pdfiumCore;
    private static final float size = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initialize();
    }

    private void initialize(){
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                myHandleOnBackPressed();
            }
        });
        pdfiumCore = new PdfiumCore(this);
        pdfFiles = this.loadPdfList();
        List<Integer> imageViewIds = Arrays.asList( R.id.image_pdf, R.id.image_pdf_sec , R.id.image_pdf_third );
        for( int i = 0;i < imageViewIds.size() ; i++ ){
            try {
                Bitmap bitmap = this.loadImage(pdfFiles[i]);
                ImageView imageView = this.findViewById(imageViewIds.get(i));
                imageView.setScaleType( ImageView.ScaleType.CENTER_CROP );
                imageView.setImageBitmap(bitmap);
                int finalI = i;
                imageView.setOnClickListener(v -> this.gotoPdfView(pdfFiles[finalI]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void myHandleOnBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("确定要退出APP?")
                .setPositiveButton("确定", (dialog, which) -> finish())
                .setNegativeButton("取消", null)
                .show();
    }

    private void gotoPdfView( String file ){
        PdfFragment pdfFragment = PdfFragment.newInstance(file);
        this.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, pdfFragment,null)
                .addToBackStack("pdf_detail")
                .commit();
    }

    private String[] loadPdfList() {
        try {
            return Arrays.stream(this.getAssets().list(""))
                    .filter(n -> n.endsWith(".pdf"))
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap loadImage(String fileName) throws IOException {
        File f = Utils.fileFromAsset(this,fileName);
        ParcelFileDescriptor pfd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfDocument pdfDocument = pdfiumCore.newDocument(pfd);

        pdfiumCore.openPage(pdfDocument, 0);
        int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
        int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);

        float scaleValue = size / Math.max(width,height);

        // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
        // RGB_565 - little worse quality, twice less memory usage

        int targetWidth = (int) (width*scaleValue);
        int targetHeight = (int) (height*scaleValue);
        Bitmap bitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.RGB_565);
        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0,
                targetWidth , targetHeight);
        pdfiumCore.closeDocument(pdfDocument);
        return bitmap;
    }
}