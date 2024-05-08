package com.example.unifolder;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PdfProcessor {
    private static final String TAG = PdfProcessor.class.getSimpleName();
    private ExecutorService executorService;

    public PdfProcessor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public Future<Bitmap> extractFirstPageImageFromPdf(String pdfUrl) {
        return executorService.submit(() -> {
            Bitmap firstPageBitmap = null;
            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                // Download PDF from Firebase URL
                InputStream inputStream = downloadFile(pdfUrl);

                // Save PDF to a temporary file
                File pdfFile = savePdfToTemporaryFile(inputStream);

                // Open PDF for rendering
                parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                // Render the first page of PDF
                PdfRenderer.Page page = pdfRenderer.openPage(0);
                firstPageBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(firstPageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // Close the PDF renderer and the file descriptor
                page.close();
                pdfRenderer.close();
            } catch (IOException e) {
                Log.e(TAG, "Error extracting first page image from PDF", e);
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing ParcelFileDescriptor", e);
                    }
                }
            }

            return firstPageBitmap;
        });
    }
    public Future<List<Bitmap>> extractAllPagesImagesFromPdf(String pdfUrl) {
        return executorService.submit(() -> {
            List<Bitmap> pagesBitmaps = new ArrayList<>();
            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                // Download PDF from Firebase URL
                InputStream inputStream = downloadFile(pdfUrl);

                // Save PDF to a temporary file
                File pdfFile = savePdfToTemporaryFile(inputStream);

                // Open PDF for rendering
                parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                // Render all pages of PDF
                int pageCount = pdfRenderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = pdfRenderer.openPage(i);
                    Bitmap pageBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    pagesBitmaps.add(pageBitmap);
                    page.close();
                }

                // Close the PDF renderer and the file descriptor
                pdfRenderer.close();
            } catch (IOException e) {
                Log.e(TAG, "Error extracting all pages images from PDF", e);
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing ParcelFileDescriptor", e);
                    }
                }
            }

            return pagesBitmaps;
        });
    }

    private InputStream downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        connection.connect();
        return connection.getInputStream();
    }

    private File savePdfToTemporaryFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("temp_pdf", ".pdf");
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return tempFile;
    }
}

