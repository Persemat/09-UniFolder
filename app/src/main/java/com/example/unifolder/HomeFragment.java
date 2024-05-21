package com.example.unifolder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifolder.Adapter.DocumentAdapter;
import com.example.unifolder.Ui.RenderDocumentViewModel;
import com.example.unifolder.Ui.ResultViewModel;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private TextView welcomeTextView;
    private NavController navController;
    private HomeViewModel homeViewModel;
    private ResultViewModel resultViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private androidx.appcompat.widget.SearchView searchView;
    private ImageButton filterButton;
    private Button loginRedirectButton;
    private String selectedTag = null;
    private String selectedCourse = null;
    private RecyclerView lastOpenedRecyclerView, uploadsRecyclerView;
    private DocumentAdapter lastOpenedAdapter;
    private DocumentAdapter yourUploadsAdapter;
    private RenderDocumentViewModel renderDocumentViewModel;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        resultViewModel = new ViewModelProvider(this,
                new ResultViewModelFactory(requireContext())).get(ResultViewModel.class);

        searchView = view.findViewById(R.id.search_view);
        filterButton = view.findViewById(R.id.filter_button);
        welcomeTextView = view.findViewById(R.id.welcome_textview);
        loginRedirectButton = view.findViewById(R.id.login_button);
        lastOpenedRecyclerView = view.findViewById(R.id.lastOpened_recyclerView);
        uploadsRecyclerView = view.findViewById(R.id.uploads_recyclerView);

        //Inizializza il NavController ottenendolo dal NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        navController = navHostFragment.getNavController();

        // Setup welcome layout
        homeViewModel.welcomeUser(requireActivity(),this,welcomeTextView,loginRedirectButton);
        renderDocumentViewModel = new ViewModelProvider(this, new RenderDocumentViewModelFactory(requireContext())).get(RenderDocumentViewModel.class);

        // Login redirect button initialization
        loginRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_homeFragment_to_loginActivity);
            }
        });

        // Recupera la lista di documenti dal tuo database o da altre fonti
        lastOpenedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeViewModel.getLastOpened(requireContext(),this);
        homeViewModel.getLastOpenedResultsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Document>>() {
            @Override
            public void onChanged(List<Document> documents) {
                homeViewModel.getLastOpenedPreviewsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Bitmap>>() {
                    @Override
                    public void onChanged(List<Bitmap> bitmaps) {
                        lastOpenedAdapter = new DocumentAdapter(documents, bitmaps, new OnDocumentClickListener() {
                            @Override
                            public void onDocumentClicked(Document document) {
                                renderDocumentViewModel.renderDocument(document, requireContext());
                                // Ottieni il NavController
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
                                // Naviga al fragment dei dettagli del documento e passa il bundle come argomento
                                navController.navigate(R.id.detailFragment);
                            }
                        },DocumentAdapter.VIEW_TYPE_HOME);

                        lastOpenedRecyclerView.setAdapter(lastOpenedAdapter);
                        Log.d(TAG,"lastOpened set with num. "+documents.size()+" elements");
                    }
                });

            }
        });

        // Recupera la lista di documenti dal tuo database o da altre fonti
        uploadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        homeViewModel.getYourUploads(requireContext(),this);
        homeViewModel.getYourUploadsResultsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Document>>() {
            @Override
            public void onChanged(List<Document> documents) {
                homeViewModel.getYourUploadsPreviewsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Bitmap>>() {
                    @Override
                    public void onChanged(List<Bitmap> bitmaps) {
                        yourUploadsAdapter = new DocumentAdapter(documents, bitmaps, new OnDocumentClickListener() {
                            @Override
                            public void onDocumentClicked(Document document) {
                                renderDocumentViewModel.renderDocument(document, requireContext());
                                // Ottieni il NavController
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
                                // Naviga al fragment dei dettagli del documento e passa il bundle come argomento
                                navController.navigate(R.id.detailFragment);
                            }
                        }, DocumentAdapter.VIEW_TYPE_HOME);

                        uploadsRecyclerView.setAdapter(yourUploadsAdapter);
                        Log.d(TAG,"yourUploads set with num. "+documents.size()+" elements");
                    }
                });

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Azione da eseguire quando viene inviata la query di ricerca
                // Esempio: avviare la ricerca con i dati immessi dall'utente

                Log.d(TAG,"query: "+query + "; tag: "+selectedTag + "; course: "+selectedCourse);

                Bundle bundle = new Bundle();
                bundle.putString("queryTerm",query);

                if(selectedTag!=null || selectedCourse!=null) {
                    resultViewModel.searchDocuments(selectedCourse,selectedTag,query);
                } else {
                    resultViewModel.searchDocuments(query);
                }

                navController.navigate(R.id.searchResultFragment, bundle);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterSelectionDialog(v);
            }
        });


        return view;
        // Inflate the layout for this fragment
    }

    // chooser and dialog
    private void showFilterSelectionDialog(View v) {
        Log.d(TAG,"show filter started");

        // Crea un AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.document_filter);
        // Infla il layout XML per la dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        // Ottieni gli Spinner dal layout XML
        Spinner tagSpinner = dialogView.findViewById(R.id.filter_tag_spinner);

        // Aggiungi un listener agli Spinner per gestire gli eventi di selezione
        Button selectCourse = dialogView.findViewById(R.id.select_course);
        selectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseSelectionDialog(v);
            }
        });

        // Crea e mostra la dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Button submit = dialogView.findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tagLayout = requireView().findViewById(R.id.tag_layout);
                tagLayout.setVisibility(View.VISIBLE);
                ImageButton cancel = tagLayout.findViewById(R.id.cancel_button);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTag = null;
                        tagLayout.setVisibility(View.GONE);
                    }
                });

                selectedTag = tagSpinner.getSelectedItem().toString();

                TextView tag = requireView().findViewById(R.id.tag_textView);
                tag.setText(selectedTag);


                String[] opts = getResources().getStringArray(R.array.options_filter_spinner);
                int index = homeViewModel.findIndex(opts,selectedTag);
                Log.d(TAG,"found index: " + index);

                if(!getResources().getConfiguration().getLocales().get(0).getLanguage().equals("it")) {
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    configuration.setLocale(new Locale("it"));
                    resources.updateConfiguration(configuration,requireContext().getResources().getDisplayMetrics());
                    String[] searchOpts = resources.getStringArray(R.array.options_filter_spinner);

                    selectedTag = searchOpts[index];
                    Log.d(TAG,"new tag value: " + selectedTag);
                }

                if(index == 0)     // checks if "All" is selected
                    selectedTag = null;


                dialog.dismiss();
            }
        });
    }

    private void showCourseSelectionDialog(View view) {
        Log.d(TAG,"showcourse started");
        String[] macroAreas = homeViewModel.initMacroAreas(requireActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Seleziona la macroarea");

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
        String[] courses = homeViewModel.getAvailableCourses(requireContext(),selectedMacroArea);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Seleziona il corso");

        builder.setItems(courses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View courseLayout = requireView().findViewById(R.id.course_layout);
                courseLayout.setVisibility(View.VISIBLE);
                ImageButton cancel = courseLayout.findViewById(R.id.cancel_button);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCourse = null;
                        courseLayout.setVisibility(View.GONE);
                    }
                });

                selectedCourse = courses[which];
                // Visualizza il corso selezionato nel TextInputLayout
                TextView courseTextView = requireView().findViewById(R.id.course_textView);
                courseTextView.setText(selectedCourse);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}