package com.example.unifolder;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Data.User.UserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Util.ServiceLocator;
import com.example.unifolder.Welcome.UserViewModel;
import com.example.unifolder.Welcome.UserViewModelFactory;
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

    public boolean checkInputValuesAndUpload(String title, String username, String course, String tag, Uri selectedFileUri, View v, Context context) {
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

        repository.uploadDocument(document, context, new SavedDocumentCallback() {
            @Override
            public void onDocumentSaved(Document savedDocument) {
                Snackbar.make(v,"inserted doc with id: " + savedDocument.getId(), Snackbar.LENGTH_SHORT).show();
                // todo: navigate to document details fragment

            }

            @Override
            public void onSaveFailed(String errorMessage) {
                Snackbar.make(v,"doc not saved", Snackbar.LENGTH_SHORT).show();
            }
        });

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
        // Simula il recupero dei corsi disponibili per la macroarea selezionata
        // Questo può essere un'implementazione reale che interroga un backend o un'altra fonte di dati
        // Qui, per semplicità, viene restituito un array fisso di esempio
        if (macroArea.equals(c.getString(R.string.economics)))
            return new String[]{"ECONOMIA DELLE BANCHE, DELLE ASSICURAZIONI E DEGLI INTERMEDIARI FINANZIARI [E1803M]",
                    "ECONOMIA E AMMINISTRAZIONE DELLE IMPRESE [E1802M]", "ECONOMIA E COMMERCIO [E3301M]",
                    "ECONOMIA, ANALISI DEI DATI E MANAGEMENT [E3303M]",
                    "MARKETING, COMUNICAZIONE AZIENDALE E MERCATI GLOBALI [E1801M]",
                    "SCIENZE STATISTICHE ED ECONOMICHE [E4101B]", "STATISTICA E GESTIONE DELLE INFORMAZIONI [E4102B]",
                    "ECONOMIA AZIENDALE E MANAGEMENT [E1805M]",
                    "BIOSTATISTICA [F8203B]", "ECONOMIA DEL TURISMO [F7601M]", "ECONOMIA E FINANZA [F1601M]",
                    "INTERNATIONAL ECONOMICS - ECONOMIA INTERNAZIONALE [F5602M]",
                    "MANAGEMENT E DESIGN DEI SERVIZI [F6302N]", "MARKETING E MERCATI GLOBALI [F7702M]",
                    "SCIENZE ECONOMICO-AZIENDALI [F7701M]", "SCIENZE STATISTICHE ED ECONOMICHE [F8204B]"};
        else if (macroArea.equals(c.getString(R.string.law)))
            return new String[]{"SCIENZE DEI SERVIZI GIURIDICI [E1401A]","DIRITTO DELLE ORGANIZZAZIONI PUBBLICHE E PRIVATE [FSG01A]",
                    "HUMAN-CENTERED ARTIFICIAL INTELLIGENCE [F551MI]"};
        else if (macroArea.equals(c.getString(R.string.medicine)))
            return new String[]{"FISIOTERAPIA [I0201D]", "IGIENE DENTALE [I0301D]", "INFERMIERISTICA [I0101D]",
                    "OSTETRICIA [I0102D]", "TECNICHE DI LABORATORIO BIOMEDICO [I0302D]",
                    "TECNICHE DI RADIOLOGIA MEDICA, PER IMMAGINI E RADIOTERAPIA [I0303D]",
                    "TERAPIA DELLA NEURO E PSICOMOTRICITÀ DELL'ETÀ EVOLUTIVA [I0202D]",
                    "BIOTECNOLOGIE MEDICHE [F0901D]", "SCIENZE INFERMIERISTICHE E OSTETRICHE [K0101D]"};
        else if (macroArea.equals(c.getString(R.string.psychology)))
            return new String[]{"SCIENZE E TECNICHE PSICOLOGICHE [E2401P]", "SCIENZE PSICOSOCIALI DELLA COMUNICAZIONE [E2004P]",
                    "INTERPRETARIATO E TRADUZIONE IN LINGUA DEI SEGNI ITALIANA (LIS) E LINGUA DEI SEGNI ITALIANA TATTILE (LIST) [E2005P]",
                    "APPLIED EXPERIMENTAL PSYCHOLOGICAL SCIENCES [F5105P]", "NEUROPSICOLOGIA E NEUROSCIENZE COGNITIVE [F5108P]",
                    "PSICOLOGIA CLINICA [F5107P]", "PSICOLOGIA CLINICA E NEUROPSICOLOGIA NEL CICLO DI VITA [F5104P]",
                    "PSICOLOGIA DELLO SVILUPPO E DEI PROCESSI EDUCATIVI [F5103P]",
                    "PSICOLOGIA SOCIALE, ECONOMICA E DELLE DECISIONI [F5106P]",
                    "TEORIA E TECNOLOGIA DELLA COMUNICAZIONE [F9201P] (INTERDIPARTIMENTALE CON INFORMATICA)"};
        else if (macroArea.equals(c.getString(R.string.education)))
            return new String[]{"COMUNICAZIONE INTERCULTURALE [E2001R]",
                    "SCIENZE DELL'EDUCAZIONE [E1901R]", "FORMAZIONE E SVILUPPO DELLE RISORSE UMANE [F5701R]",
                    "SCIENZE ANTROPOLOGICHE ED ETNOLOGICHE [F0101R]", "SCIENZE PEDAGOGICHE [F8501R]",
                    "LINGUAGGI ARTISTICI PER LA FORMAZIONE [F5702R]"};
        else if (macroArea.equals(c.getString(R.string.science)))
            return new String[]{"ARTIFICIAL INTELLIGENCE [E311PV]",
                    "BIOTECNOLOGIE [E0201Q]","FISICA [E3001Q]","INFORMATICA [E3101Q]",
                    "MATEMATICA [E3501Q]", "OTTICA E OPTOMETRIA [E3002Q]", "SCIENZA DEI MATERIALI [E2701Q]",
                    "SCIENZA E NANOTECNOLOGIA DEI MATERIALI [ESM01Q]", "SCIENZE BIOLOGICHE [E1301Q]",
                    "SCIENZE E TECNOLOGIE CHIMICHE [E2702Q]", "SCIENZE E TECNOLOGIE GEOLOGICHE [E3401Q]",
                    "SCIENZE E TECNOLOGIE PER L'AMBIENTE [E3201Q]",
                    "ARTIFICIAL INTELLIGENCE FOR SCIENCE AND TECHNOLOGY [F9102Q]",
                    "ASTROFISICA E FISICA DELLO SPAZIO [F5801Q]", "ASTROPHYSICS AND SPACE PHYSICS [F5802Q]",
                    "BIOLOGIA [F0601Q]", "BIOTECNOLOGIE INDUSTRIALI [F0802Q]", "DATA SCIENCE [F9101Q]",
                    "DATA SCIENCE [FDS01Q]", "FISICA [F1701Q]", "INFORMATICA [F1801Q]",
                    "MARINE SCIENCES [F7502Q]", "MATEMATICA [F4001Q]", "MATERIALS SCIENCE [F5302Q]",
                    "MATERIALS SCIENCE AND NANOTECHNOLOGY [FSM01Q]", "SCIENZA DEI MATERIALI [F5301Q]",
                    "SCIENZE E TECNOLOGIE CHIMICHE [F5401Q]", "SCIENZE E TECNOLOGIE GEOLOGICHE [F7401Q]",
                    "SCIENZE E TECNOLOGIE PER L'AMBIENTE E IL TERRITORIO [F7501Q]",
                    "TEORIA E TECNOLOGIA DELLA COMUNICAZIONE [F9201P]"};
        else if (macroArea.equals(c.getString(R.string.sociology)))
            return new String[]{"SCIENZE DEL TURISMO E COMUNITÀ LOCALE [E1501N]",
                    "SCIENZE DELL'ORGANIZZAZIONE [E1601N]", "SERVIZIO SOCIALE [E3901N]",
                    "SOCIOLOGIA [E4001N]", "ANALISI DEI PROCESSI SOCIALI [F8802N]",
                    "MANAGEMENT E DESIGN DEI SERVIZI [F6302N]", "PROGRAMMAZIONE E GESTIONE DELLE POLITICHE E DEI SERVIZI SOCIALI [F8701N]",
                    "SICUREZZA, DEVIANZA E GESTIONE DEI RISCHI [F8803N]", "TURISMO, TERRITORIO E SVILUPPO LOCALE [F4901N]"};
        else return new String[]{};
    }

    public String[] initMacroAreas(Context context) {
        return new String[]{context.getString(R.string.economics), context.getString(R.string.law),
                context.getString(R.string.medicine), context.getString(R.string.psychology),
                context.getString(R.string.education), context.getString(R.string.science),
                context.getString(R.string.sociology)};
    }
}
