package com.example.unifolder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifolder.Adapter.DocumentAdapter;
import com.example.unifolder.Ui.ResultViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {
    private static final String TAG = SearchResultFragment.class.getSimpleName();
    private TextView titleTextView;
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private ResultViewModel resultViewModel;
    private NavController navController;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String queryTerm;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1, String param2) {
        SearchResultFragment fragment = new SearchResultFragment();
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
            queryTerm = getArguments().getString("queryTerm");
            Log.d(TAG,"param retrieved");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        titleTextView = view.findViewById(R.id.search_results);
        recyclerView = view.findViewById(R.id.recycler_view);

        if(queryTerm != null && !queryTerm.isEmpty()) {
            titleTextView.append(" \"" + queryTerm + "\"");
            Log.d(TAG,"queryTerm set");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        documentAdapter = new DocumentAdapter(DocumentAdapter.VIEW_TYPE_RESULTS); // Assicurati di passare i dati necessari all'adapter

        // Inizializza il ResultViewModel utilizzando il ViewModelProvider
        resultViewModel = new ViewModelProvider(this,
                new ResultViewModelFactory(requireContext())).get(ResultViewModel.class);

        Log.d(TAG,"Observing live data");
        //osserva i risultati della ricerca dal ResultViewModel
        resultViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Document>>() {
            @Override
            public void onChanged(List<Document> documents) {
                Log.d(TAG, "documents added to adapter");
                documentAdapter.replaceAllDocuments(documents);
                recyclerView.setAdapter(documentAdapter);
            }
        });

        // Configura il pulsante di ritorno
        ImageButton backButton = view.findViewById(R.id.back_arrow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga al fragment precedente quando si clicca la freccia all'indietro
                navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
                navController.navigateUp();
            }
        });

        return view;
    }

            @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}