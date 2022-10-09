package com.gbtouch.pdfdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PdfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PdfFragment extends Fragment {


    private static final String ARG_URL = "URL";
    private PDFView pdfView;
    private String url;

    public PdfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment PdfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PdfFragment newInstance(String url) {
        PdfFragment fragment = new PdfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.pdfView = view.findViewById(R.id.pdfView);
        this.displayFromFile(this.url);
    }

    private void displayFromFile(String assetFileName) {
        try {
            File pdfFile = Utils.fileFromAsset(requireContext(),assetFileName);
            pdfView.fromFile(pdfFile)
                    .defaultPage(0)
                    //.onPageChange(this)
                    .enableAnnotationRendering(true)
                    //.onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(requireContext()))
                    .spacing(10) // in dp
                    //.onPageError(this)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}