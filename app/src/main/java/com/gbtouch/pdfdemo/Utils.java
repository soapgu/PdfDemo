package com.gbtouch.pdfdemo;

import android.content.Context;

import com.github.barteksc.pdfviewer.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static File fileFromAsset(Context context, String assetName) throws IOException {
        File outFile = new File(context.getCacheDir(), assetName + "-pdfview.pdf");
        if( outFile.exists() ){
            return outFile;
        }
        return FileUtils.fileFromAsset(context, assetName);
    }
}
