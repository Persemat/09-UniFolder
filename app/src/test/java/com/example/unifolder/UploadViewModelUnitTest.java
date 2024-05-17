package com.example.unifolder;

import static com.ibm.icu.text.PluralRules.Operand.v;
import static org.junit.Assert.assertEquals;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.google.firebase.FirebaseApp;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Robolectric;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
public class UploadViewModelUnitTest {
    private UploadViewModel uploadViewModel;
    private Context context;

    @Before
    public void setUploadViewModel() {
        context = ApplicationProvider.getApplicationContext();
        uploadViewModel = new UploadViewModel();
    }

    @Test
    public void testGetDocumentNameFromUri_ReturnsOk() {
        String mockFileName = "fooName.pdf";

        // Crea un mock dell'oggetto Uri
        Uri uri = mock(Uri.class);

        // Crea un mock dell'oggetto ContentResolver
        ContentResolver contentResolver = mock(ContentResolver.class);

        // Crea un mock dell'oggetto Cursor
        Cursor cursor = mock(Cursor.class);

        // Specifica il comportamento desiderato quando il metodo query() viene chiamato sull'oggetto ContentResolver
        // In questo esempio, stiamo simulando la restituzione di un cursore non nullo e valido
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(cursor);

        // Specifica il comportamento desiderato quando il metodo moveToFirst() viene chiamato sul cursore
        when(cursor.moveToFirst()).thenReturn(true);

        // Specifica il comportamento desiderato quando il metodo getColumnIndex() viene chiamato sul cursore
        // In questo esempio, stiamo simulando che il nome del file sia presente nella colonna DISPLAY_NAME
        when(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).thenReturn(0);

        // Specifica il comportamento desiderato quando il metodo getString() viene chiamato sul cursore
        // In questo esempio, stiamo simulando il recupero del nome del file dalla colonna DISPLAY_NAME
        when(cursor.getString(0)).thenReturn(mockFileName);


        String result = uploadViewModel.getDocumentNameFromUri(contentResolver,uri);

        assertEquals(result,mockFileName);
    }

    @Test
    public void testGetDocumentSize_ReturnsOk() throws FileNotFoundException {
        long mockSize = 100L;

        // Mock dell'oggetto Uri
        Uri documentUri = mock(Uri.class);

        // Mock dell'oggetto ContentResolver
        ContentResolver contentResolver = mock(ContentResolver.class);

        // Mock del metodo openFileDescriptor() del ContentResolver
        ParcelFileDescriptor parcelFileDescriptor = mock(ParcelFileDescriptor.class);
        // Imposta il comportamento desiderato per il metodo mock openFileDescriptor()
        // In questo caso, restituisci un ParcelFileDescriptor mockato
        when(contentResolver.openFileDescriptor(eq(documentUri), eq("r"))).thenReturn(parcelFileDescriptor);

        // Imposta il comportamento del ParcelFileDescriptor mockato
        // In questo caso, restituisci un valore di dimensione fittizio
        when(parcelFileDescriptor.getStatSize()).thenReturn(mockSize); // dimensione fittizia

        // Esegui il metodo da testare con i mock creati
        long fileSize = uploadViewModel.getDocumentSize(contentResolver,documentUri);

        // Verifica che il metodo abbia restituito la dimensione corretta
        assertEquals(fileSize, mockSize);
    }

    @Test
    public void testGetFilePathFromUri_ReturnsOk() {
        String mockFilePath = "/sample/foo/fileName.pdf";

        // Crea un mock dell'oggetto Uri
        Uri uri = mock(Uri.class);

        // Crea un mock dell'oggetto ContentResolver
        ContentResolver contentResolver = mock(ContentResolver.class);

        // Crea un mock dell'oggetto Cursor
        Cursor cursor = mock(Cursor.class);

        // Specifica il comportamento desiderato quando il metodo query() viene chiamato sull'oggetto ContentResolver
        // In questo esempio, stiamo simulando la restituzione di un cursore non nullo e valido
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(cursor);

        // Specifica il comportamento desiderato quando il metodo moveToFirst() viene chiamato sul cursore
        when(cursor.moveToFirst()).thenReturn(true);

        // Specifica il comportamento desiderato quando il metodo getColumnIndex() viene chiamato sul cursore
        // In questo esempio, stiamo simulando che il nome del file sia presente nella colonna DISPLAY_NAME
        when(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)).thenReturn(0);

        // Specifica il comportamento desiderato quando il metodo getString() viene chiamato sul cursore
        // In questo esempio, stiamo simulando il recupero del nome del file dalla colonna DISPLAY_NAME
        when(cursor.getString(0)).thenReturn(mockFilePath);

        String result = uploadViewModel.getDocumentNameFromUri(contentResolver,uri);

        assertEquals(result,mockFilePath);
    }

    @Test
    public void testGetDocumentCreationDate_ReturnsOk() throws Exception {
        // Mock Uri e oggetti correlati
        Uri uri = mock(Uri.class);
        ParcelFileDescriptor parcelFileDescriptorMock = mock(ParcelFileDescriptor.class);
        // Crea un mock dell'oggetto ContentResolver
        ContentResolver contentResolver = mock(ContentResolver.class);
        when(contentResolver.openFileDescriptor(eq(uri), eq("r"))).thenReturn(parcelFileDescriptorMock);

        // Creare un oggetto FileDescriptor reale
        FileDescriptor realFileDescriptor = new FileDescriptor();

        // Creare un oggetto FileDescriptorWrapper con l'oggetto FileDescriptor reale
        FileDescriptorWrapper fileDescriptorWrapperMock = new FileDescriptorWrapper(realFileDescriptor);

        // Imposta il comportamento desiderato quando viene chiamato getFileDescriptor() su FileDescriptorWrapper
        when(parcelFileDescriptorMock.getFileDescriptor()).thenReturn(fileDescriptorWrapperMock.getFileDescriptor());

        // Esegui il metodo che vuoi testare
        String result = uploadViewModel.getDocumentCreationDate(contentResolver, uri);

        // Verifica il risultato
        assertEquals("unknown date", result);
    }

    @Test
    public void testGetFileSizeString_ReturnsOk() {
        String zero = uploadViewModel.getFileSizeString(0);
        String oneKByte = uploadViewModel.getFileSizeString(1024);
        String oneMByte = uploadViewModel.getFileSizeString(1024*1024);

        assertEquals("0 B",zero);
        assertEquals("1.00 KB",oneKByte);
        assertEquals("1.00 MB",oneMByte);
    }

    @Test
    public void testCheckInputValuesAndUpload_Errors(){
        /// Creazione dell'activity per ospitare il fragment
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();

        // Creazione e aggiunta del fragment
        UploadFragment uploadFragment = UploadFragment.newInstance();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(uploadFragment,null);
        fragmentTransaction.commitNow();

        // Ottenere la view associata al fragment
        View fragmentView = uploadFragment.getView();

        // Assicurarsi che la view non sia nulla e che abbia i componenti desiderati
        assertNotNull(fragmentView);

        assertFalse(uploadViewModel.checkInputValuesAndUpload(null,"foo","course","tag",null,fragmentView,context));
        assertFalse(uploadViewModel.checkInputValuesAndUpload("title",null,"course","tag",null,new View(context),context));
        assertFalse(uploadViewModel.checkInputValuesAndUpload("title","foo","course","tag",null,new View(context),context));
        assertFalse(uploadViewModel.checkInputValuesAndUpload("title","foo","course","tag",null,new View(context),context));
    }

    private class FileDescriptorWrapper {
        private final FileDescriptor fileDescriptor;

        public FileDescriptorWrapper(FileDescriptor fileDescriptor) {
            this.fileDescriptor = fileDescriptor;
        }

        public FileDescriptor getFileDescriptor() {
            return fileDescriptor;
        }
    }
}