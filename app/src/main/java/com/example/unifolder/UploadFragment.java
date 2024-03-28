package com.example.unifolder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

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
    private final String[] macroAreas = {getString(R.string.economics), getString(R.string.law), getString(R.string.medicine), getString(R.string.psychology), getString(R.string.education), getString(R.string.science), getString(R.string.sociology)};


    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload, container, false);



        return view;
    }

    public void showCourseSelectionDialog(View view) {
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
        String[] courses = getAvailableCourses(selectedMacroArea);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Seleziona il corso");

        builder.setItems(courses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCourse = courses[which];
                // Visualizza il corso selezionato nel TextInputLayout
                TextInputLayout textInputLayout = requireView().findViewById(R.id.course_textInput);
                textInputLayout.getEditText().setText(selectedCourse);
                Toast.makeText(requireContext(), "Hai selezionato: " + selectedCourse, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getAvailableCourses(String macroArea) {
        // Simula il recupero dei corsi disponibili per la macroarea selezionata
        // Questo può essere un'implementazione reale che interroga un backend o un'altra fonte di dati
        // Qui, per semplicità, viene restituito un array fisso di esempio
        switch (macroArea) {
            case "Macroarea 1":
                return new String[]{"Corso 1A", "Corso 1B", "Corso 1C"};
            case "Macroarea 2":
                return new String[]{"Corso 2A", "Corso 2B", "Corso 2C"};
            // Aggiungi altre macroaree e corsi disponibili secondo necessità
            default:
                return new String[]{};
        }
    }
}