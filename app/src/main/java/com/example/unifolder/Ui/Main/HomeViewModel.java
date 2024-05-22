package com.example.unifolder.Ui.Main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Data.Repository.Document.DocumentRepository;
import com.example.unifolder.Data.Repository.User.IUserRepository;
import com.example.unifolder.Model.Document;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Ui.Welcome.UserViewModel;
import com.example.unifolder.Ui.Welcome.UserViewModelFactory;
import com.example.unifolder.Util.CourseUtil;
import com.example.unifolder.Util.PdfProcessor;
import com.example.unifolder.Util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private static final String TYPE_LAST_OPENED = "LastOpened";
    private static final String TYPE_YOUR_UPLOADS = "YourUploads";
    private DocumentRepository documentRepository;
    private final MutableLiveData<List<Document>> lastOpenedResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Bitmap>> lastOpenedPreviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Document>> yourUploadsResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Bitmap>> yourUploadsPreviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> authUsernameLiveData = new MutableLiveData<>();
    private IUserRepository userRepository;
    private UserViewModel userViewModel;

    public LiveData<List<Document>> getLastOpenedResultsLiveData() {
        return lastOpenedResultsLiveData;
    }

    public void setLastOpenedResultsLiveData(List<Document> documentList){
        lastOpenedResultsLiveData.postValue(documentList);
        Log.d(TAG,"posted value");
    }

    public MutableLiveData<List<Bitmap>> getLastOpenedPreviewsLiveData() {
        return lastOpenedPreviewsLiveData;
    }

    public void setLastOpenedPreviewsLiveData(List<Bitmap> bitmaps) {
        lastOpenedPreviewsLiveData.postValue(bitmaps);
    }

    public LiveData<List<Document>> getYourUploadsResultsLiveData() {
        return yourUploadsResultsLiveData;
    }

    public void setYourUploadsResultsLiveData(List<Document> documentList){
        yourUploadsResultsLiveData.postValue(documentList);
        Log.d(TAG,"posted value");
    }

    public MutableLiveData<List<Bitmap>> getYourUploadsPreviewsLiveData() {
        return yourUploadsPreviewsLiveData;
    }

    public void setYourUploadsPreviewsLiveData(List<Bitmap> bitmaps) {
        yourUploadsPreviewsLiveData.postValue(bitmaps);
    }

    public LiveData<String> getAuthUsernameLiveData() {
        return authUsernameLiveData;
    }

    public void setAuthUsernameLiveData(String name) {
        authUsernameLiveData.postValue(name);
    }

    public void getLastOpened(Context context,Fragment fragment) {
        if(documentRepository==null)
            documentRepository = new DocumentRepository(context);

        getAuthUsernameLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "looking up for name: " + s);
                documentRepository.getLastOpenedDocuments(s, new SearchResultCallback() {
                    @Override
                    public void OnSearchCompleted(List<Document> documents) {
                        //setLastOpenedResultsLiveData(documents);
                        extractDocumentPreviews(documents,TYPE_LAST_OPENED, context);
                    }

                    @Override
                    public void OnSearchFailed(String error) {

                    }
                });
            }
        });
    }

    public void getYourUploads(Context context,Fragment fragment) {
        if(documentRepository==null)
            documentRepository = new DocumentRepository(context);

        getAuthUsernameLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG,"looking up for name: "+ s);
                documentRepository.getYourUploadedDocuments(s, new SearchResultCallback() {
                    @Override
                    public void OnSearchCompleted(List<Document> documents) {
                        //setYourUploadsResultsLiveData(documents);
                        extractDocumentPreviews(documents,TYPE_YOUR_UPLOADS,context);
                    }

                    @Override
                    public void OnSearchFailed(String error) {

                    }
                });
            }
        });
    }

    private void extractDocumentPreviews(List<Document> documents, String destinationType, Context context) {
        List<Future<Bitmap>> previewFutures = new ArrayList<>();
        PdfProcessor pdfProcessor = new PdfProcessor();

        // Per ogni documento, avvia il processo di estrazione dell'anteprima
        for (Document document : documents) {
            Future<Bitmap> previewFuture = pdfProcessor.extractFirstPageImageFromPdf(document.getFileUrl(), context);
            previewFutures.add(previewFuture);
        }

        // Attendi il completamento di tutti i processi di estrazione delle anteprime
        List<Bitmap> previews = new ArrayList<>();
        for (Future<Bitmap> future : previewFutures) {
            try {
                Bitmap preview = future.get();
                previews.add(preview);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error extracting preview", e);
                // Aggiungi una bitmap vuota in caso di errore
                previews.add(null);
            }
        }

        // Imposta i risultati nella LiveData
        if(destinationType.equals(TYPE_LAST_OPENED)) {
            lastOpenedResultsLiveData.postValue(documents);
            lastOpenedPreviewsLiveData.postValue(previews);
        } else if(destinationType.equals(TYPE_YOUR_UPLOADS)) {
            yourUploadsResultsLiveData.postValue(documents);
            yourUploadsPreviewsLiveData.postValue(previews);
        }

    }

    public int findIndex(String[] opts, String selectedTag) {
        for (int i=0;i<opts.length;i++)
            if (opts[i].equals(selectedTag))
                return i;
        return -1;
    }

    public String[] getAvailableCourses(Context c,String macroArea) {
        return CourseUtil.getAvailableCourses(c, macroArea);
    }

    public String[] initMacroAreas(Context context) {
        return CourseUtil.initMacroAreas(context);
    }

    public void welcomeUser(Activity activity, Fragment homeFragment, TextView welcomeTextView, Button loginRedirect) {
        userRepository = ServiceLocator.getInstance().getUserRepository(activity.getApplication());
        // Ottieni una istanza del tuo UserViewModel
        userViewModel = new ViewModelProvider(homeFragment,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        loginRedirect.setPaintFlags(loginRedirect.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginRedirect.setVisibility(View.VISIBLE);

        // Osserva i dati dell'utente
        userViewModel.getUserMutableLiveData().observe(homeFragment.getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result != null && result.isSuccess()) {
                    User user = ((Result.UserResponseSuccess) result).getData();
                    if (user != null) {
                        loginRedirect.setVisibility(View.GONE);
                        setAuthUsernameLiveData(user.getUsername());
                        // Imposta l'username dell'utente nella TextView
                        welcomeTextView.append(" " + user.getUsername());
                    }
                }
                userViewModel.getUserMutableLiveData().removeObserver(this);
            }
        });
    }
}
