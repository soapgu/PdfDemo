package com.gbtouch.pdfdemo;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] pdfFiles;
    PdfiumCore pdfiumCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdfiumCore = new PdfiumCore(this);
        //FileUtils.fileFromAsset(this, assetName);
        pdfFiles = this.loadPdfList();
        List<Integer> imageViewIds = Arrays.asList( R.id.image_pdf, R.id.image_pdf_sec , R.id.image_pdf_third );
        for( int i = 0;i < imageViewIds.size() ; i++ ){
            try {
                Bitmap bitmap = this.loadImage(pdfFiles[i],200);
                ImageView imageView = this.findViewById(imageViewIds.get(i));
                imageView.setScaleType( ImageView.ScaleType.CENTER_CROP );
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


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

    private Bitmap loadImage(String fileName,float size) throws IOException {
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



    /*
    private void displayFromAsset(String assetFileName) {
        //pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(0)
                //.onPageChange(this)
                .enableAnnotationRendering(true)
                //.onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                //.onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }
    */
}