package com.example.unifolder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unifolder.Ui.RenderDocumentViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    NavController navController;
    RenderDocumentViewModel renderDocumentViewModel;
    private Document displayedDocument;
    private List<Bitmap> documentPages;
    TextView titleTextView;
    TextView courseTextView;
    TextView tagTextView;
    ImageView scrollDocument;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        renderDocumentViewModel = new ViewModelProvider(this, new RenderDocumentViewModelFactory(requireContext())).get(RenderDocumentViewModel.class);
        titleTextView = view.findViewById(R.id.doc_title);
        courseTextView = view.findViewById(R.id.doc_course);
        tagTextView = view.findViewById(R.id.doc_tag);
        scrollDocument = view.findViewById(R.id.pdf_image);

        renderDocumentViewModel.getDocumentMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Document>() {
            @Override
            public void onChanged(Document document) {
                displayedDocument = document;
            }
        });
        renderDocumentViewModel.getBitmapMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Bitmap>>() {
            @Override
            public void onChanged(List<Bitmap> bitmaps) {
                documentPages = bitmaps;
                showDocument();
            }

        });

        // Configura il pulsante di ritorno
        ImageButton closeButton = view.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga al fragment precedente quando si clicca la freccia all'indietro
                navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
                navController.navigateUp();
            }
        });
        return view;
    }

    private void showDocument() {
        titleTextView.setText(displayedDocument.getTitle());
        courseTextView.setText(displayedDocument.getCourse());
        tagTextView.setText(displayedDocument.getTag());
    }
}