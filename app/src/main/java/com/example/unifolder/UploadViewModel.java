package com.example.unifolder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.unifolder.Util.CourseUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadViewModel extends ViewModel {
    private static DocumentRepository repository;

    public UploadViewModel() {}

    public UploadViewModel(Context context) {
        repository = new DocumentRepository(context);
    }

    public boolean checkInputValuesAndUpload(String title, String username, String course, String tag, Uri selectedFileUri, View v, Context context, SavedDocumentCallback callback) {
        if (title == null || title.isEmpty()) {
            Snackbar.make(v, R.string.title_error, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (course == null || course.isEmpty()) {
            Snackbar.make(v, R.string.course_error, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (selectedFileUri == null || selectedFileUri.toString().isEmpty()) {
            Snackbar.make(v, R.string.file_error, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        Document document = new Document(title, username, course, tag, selectedFileUri.toString());

        repository.uploadDocument(document, context, callback);

        return true;
    }

    public String getDocumentNameFromUri(ContentResolver contentResolver, Uri documentUri) {
        String documentName = "Nome sconosciuto";
        Cursor cursor = contentResolver.query(documentUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (displayNameIndex != -1) {
                documentName = cursor.getString(displayNameIndex);
            }
            cursor.close();
        }
        return documentName;
    }

    public long getDocumentSize(ContentResolver contentResolver, Uri documentUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(documentUri, "r");
            if (parcelFileDescriptor != null) {
                return parcelFileDescriptor.getStatSize();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0; // Ritorna 0 se non è possibile ottenere la dimensione del file
    }

    protected String getFilePathFromUri(ContentResolver contentResolver, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    public String getDocumentCreationDate(ContentResolver contentResolver, Uri documentUri) {
        String creationDate = "unknown date";
        try {
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(documentUri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                String filePath = getFilePathFromUri(contentResolver, documentUri);
                if (filePath != null) {
                    File file = new File(filePath);
                    long creationTime = file.lastModified();
                    Date creationDateObj = new Date(creationTime);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    creationDate = dateFormat.format(creationDateObj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return creationDate;
    }

    public String getFileSizeString(long fileSizeBytes) {
        // Converte la dimensione del file in KB o MB a seconda delle dimensioni
        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.2f", fileSizeBytes / 1024.0) + " KB";
        } else {
            return String.format("%.2f", fileSizeBytes / (1024.0 * 1024.0)) + " MB";
        }
    }

    public String[] getAvailableCourses(Context c,String macroArea) {
        return CourseUtil.getAvailableCourses(c, macroArea);
    }

    public String[] initMacroAreas(Context context) {
        return CourseUtil.initMacroAreas(context);
    }
}
