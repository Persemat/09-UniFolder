package com.example.unifolder;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Ui.RenderDocumentViewModel;
import com.example.unifolder.Util.ServiceLocator;
import com.example.unifolder.Welcome.UserViewModel;
import com.example.unifolder.Welcome.UserViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UploadViewModel uploadViewModel;
    private String TAG = UploadFragment.class.getSimpleName();
    private ActivityResultLauncher<String> pickPdfFileLauncher;
    private String[] macroAreas;
    private EditText courseEditText, titleEditText;
    private Spinner tagSpinner;
    private Button attachButton;
    private View documentDetailsLayout;
    private Uri selectedFileUri;
    private ProgressBar progressBar;
    private Button submitButton;
    private String username;
    private RenderDocumentViewModel renderDocumentViewModel;


    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * 
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance() {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        // Inizializza l'ActivityResultLauncher
        pickPdfFileLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // Gestisci il risultato qui
                // "result" è l'URI del file selezionato
                // Esegui qui il caricamento del file
                selectedFileUri = result;
                updateDocumentDetails(view,result);
            }
        });

        titleEditText = view.findViewById(R.id.title_editText);
        courseEditText = view.findViewById(R.id.course_editText);
        tagSpinner = view.findViewById(R.id.tag_spinner);
        attachButton = view.findViewById(R.id.attach_button);
        documentDetailsLayout = view.findViewById(R.id.uploadedDocDetails);
            documentDetailsLayout.setVisibility(View.GONE);
        submitButton = view.findViewById(R.id.submit_button);
        progressBar = view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

        uploadViewModel = new ViewModelProvider(this,
                new UploadViewModelFactory(requireContext())).get(UploadViewModel.class);
        getUsername();

        renderDocumentViewModel = new ViewModelProvider(this,
                new RenderDocumentViewModelFactory(requireContext())).get(RenderDocumentViewModel.class);

        //Inizializza il NavController ottenendolo dal NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        NavController navController = navHostFragment.getNavController();

        courseEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseSelectionDialog(v);
            }
        });

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString(),
                    course = courseEditText.getText().toString(),
                    tag = tagSpinner.getSelectedItem().toString();
                progressBar.setVisibility(View.VISIBLE);
                uploadViewModel.checkInputValuesAndUpload(title, username, course, tag, selectedFileUri, requireView(), requireContext(),
                        new SavedDocumentCallback() {
                            @Override
                            public void onDocumentSaved(Document savedDocument) {
                                Log.d(TAG,"docId: " + savedDocument.getId() +
                                        "docTitle: " + savedDocument.getTitle() +
                                        "author: " + savedDocument.getAuthor() +
                                        "url: " + savedDocument.getFileUrl());
                                /*Log.d(TAG,"onDocSaved()");
                                navController.navigate(R.id.detailFragment);
                                Log.d(TAG,"after navigate");
                                renderDocumentViewModel.renderDocument(savedDocument, requireContext());
                                Log.d(TAG,"after renderDocument()");*/
                                Snackbar.make(v,"inserted doc with id: " + savedDocument.getId(), Snackbar.LENGTH_SHORT).show();
                                // todo: navigate to document details fragment
                            }

                            @Override
                            public void onSaveFailed(String errorMessage) {
                                Snackbar.make(v,"doc not saved", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    private void getUsername() {
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        // Ottieni una istanza del tuo UserViewModel
        UserViewModel userViewModel = new ViewModelProvider(this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        userViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess()) {
                Log.d(TAG,"result ok");
                User user = ((Result.UserResponseSuccess) result).getData();
                if (user != null) {
                    Log.d(TAG,"user not null");
                    username = user.getUsername();
                }
            }
        });
    }

    // updates document' view by delegating specific actions to UploadViewModel
    private void updateDocumentDetails(View view,Uri result) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        // Ottieni il nome del documento
        String documentName = uploadViewModel.getDocumentNameFromUri(contentResolver,result);
        // Aggiorna la TextView del titolo del documento
        documentDetailsLayout.setVisibility(View.VISIBLE);
        TextView textViewTitle = view.findViewById(R.id.textview_document_title);
        textViewTitle.setText(documentName);

        String filePath = result.getPath();
        File file = new File(filePath);

        // Ottieni la dimensione del file in bytes e converti in KB o MB a seconda delle dimensioni
        long fileSizeBytes = uploadViewModel.getDocumentSize(contentResolver,result);
        String fileSizeString = uploadViewModel.getFileSizeString(fileSizeBytes);

        TextView size = view.findViewById(R.id.docuSize_textView);
        size.setText(fileSizeString);

        // Ottieni la data di creazione del file
        String fileCreationTime = uploadViewModel.getDocumentCreationDate(contentResolver,result);
       // String fileCreationTimeString = getFileCreationTimeString(fileCreationTime);

        TextView date = view.findViewById(R.id.docuDate_textView);
        date.setText(fileCreationTime);

        ImageButton cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFileUri = null;
                documentDetailsLayout.setVisibility(View.GONE);
            }
        });
    }

    // chooser and dialogs
    private void openFileChooser() {
        pickPdfFileLauncher.launch("application/pdf");
    }

    public void showCourseSelectionDialog(View view) {
        Log.d(TAG,"showcourse started");
        macroAreas = uploadViewModel.initMacroAreas(requireActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.select_macroarea);

        builder.setItems(macroAreas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implementa la logica per mostrare i corsi disponibili nella macroarea selezionata
                String selectedMacroArea = macroAreas[which];
                showCoursesDialog(selectedMacroArea);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCoursesDialog(String selectedMacroArea) {
        // Mostra la lista dei corsi disponibili nella macroarea selezionata
        String[] courses = uploadViewModel.getAvailableCourses(requireContext(),selectedMacroArea);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.select_course);

        builder.setItems(courses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCourse = courses[which];
                // Visualizza il corso selezionato nel TextInputLayout
                TextInputLayout textInputLayout = requireView().findViewById(R.id.course_textInput);
                textInputLayout.getEditText().setText(selectedCourse);
                //Toast.makeText(requireContext(), "Hai selezionato: " + selectedCourse, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}