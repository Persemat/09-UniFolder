package com.example.unifolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public Future<Bitmap> extractFirstPageImageFromPdf(String pdfUrl, Context context) {
        Log.d(TAG,"url is: " + pdfUrl);
        return executorService.submit(() -> {
            Bitmap firstPageBitmap = null;
            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                InputStream inputStream;
                if (isLocalRoomReference(pdfUrl)) {
                    // Se l'URL è un riferimento locale a Room, ottieni l'InputStream direttamente
                    inputStream = getInputStreamFromLocalRoomReference(pdfUrl,context);
                } else {
                    // Altrimenti, scarica il PDF dall'URL remoto
                    inputStream = downloadFile(pdfUrl);
                }

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

    private boolean isLocalRoomReference(String pdfUrl) {
        // Implementa la logica per verificare se l'URL del PDF è un riferimento locale a Room
        // Ad esempio, potresti controllare se l'URL inizia con un certo prefisso o se corrisponde a un percorso specifico
        // Restituisci true se l'URL è un riferimento locale a Room, altrimenti false
        boolean result = pdfUrl.startsWith("room://") || pdfUrl.startsWith("/storage") || pdfUrl.startsWith("content://");
        return result;
    }

    private InputStream getInputStreamFromLocalRoomReference(String pdfUrl, Context context) throws IOException {
        // Implementa la logica per ottenere l'InputStream corrispondente al riferimento locale a Room
        // Ad esempio, potresti estrarre il percorso dal riferimento e aprire un file locale
        // Restituisci l'InputStream ottenuto
        if (pdfUrl.startsWith("room://")) {
            // Se l'URL inizia con "room://", estrai il percorso e apri il file locale
            String filePath = extractFilePathFromRoomReference(pdfUrl);
            if (filePath != null) {
                return new FileInputStream(filePath);
            } else {
                throw new FileNotFoundException("File path not found for Room reference: " + pdfUrl);
            }
        } else if (pdfUrl.contains("content://com.android.providers.downloads.documents/")) {
            // Se l'URL contiene "content://com.android.providers.downloads.documents/", gestisci il riferimento ai documenti di download
            // Implementa la logica per ottenere l'InputStream dal riferimento ai documenti di download
            // Per esempio, potresti utilizzare un ContentResolver per aprire l'InputStream


            Uri contentUri = Uri.parse(pdfUrl);
            return context.getContentResolver().openInputStream(contentUri);
        } else if (pdfUrl.startsWith("/storage")) {
            return new FileInputStream(pdfUrl);
        } else {
            throw new IllegalArgumentException("Unsupported local Room reference format: " + pdfUrl);
        }
    }

    private String extractFilePathFromRoomReference(String pdfUrl) {
        // Implementa la logica per estrarre il percorso dal riferimento locale a Room
        // Ad esempio, potresti rimuovere il prefisso "room://" dall'URL
        // Restituisci il percorso estratto
        return pdfUrl.substring("room://".length());
    }

}

