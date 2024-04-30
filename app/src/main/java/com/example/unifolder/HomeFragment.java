package com.example.unifolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Ui.ResultViewModel;
import com.example.unifolder.Util.ServiceLocator;
import com.example.unifolder.Welcome.UserViewModel;
import com.example.unifolder.Welcome.UserViewModelFactory;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private TextView welcomeTextView;
    private UserViewModel userViewModel;
    private NavController navController;
    private ResultViewModel resultViewModel;
    private IUserRepository userRepository;

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
    private String selectedTag = null;
    private String selectedCourse = null;


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

        resultViewModel = new ViewModelProvider(this,
                new ResultViewModelFactory(requireContext())).get(ResultViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = view.findViewById(R.id.search_view);
        filterButton = view.findViewById(R.id.filter_button);
        welcomeTextView = view.findViewById(R.id.welcome_textview);

        //Inizializza il NavController ottenendolo dal NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        navController = navHostFragment.getNavController();

        /* Dentro il tuo fragment o activity
                RecyclerView recyclerView = view.findViewById(R.id.first_recyclerview);
                 // Recupera la lista di documenti dal tuo database o da altre fonti
                DocumentAdapter adapter = new DocumentAdapter();
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
       */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Azione da eseguire quando viene inviata la query di ricerca
                // Esempio: avviare la ricerca con i dati immessi dall'utente

                Log.d(TAG,"query: "+query + "; tag: "+selectedTag + "; course: "+selectedCourse);
                //TODO: pass to viewmodel
                if(selectedTag!=null || selectedCourse!=null) {
                    resultViewModel.searchDocuments(selectedCourse,selectedTag,query);
                    navController.navigate(R.id.searchResultFragment);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("queryTerm",query);
                    resultViewModel.searchDocuments(query);
                    navController.navigate(R.id.searchResultFragment, bundle);
                }


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
                int index = findIndex(opts,selectedTag);
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

    private int findIndex(String[] opts, String selectedTag) {
        for (int i=0;i<opts.length;i++)
            if (opts[i].equals(selectedTag))
                return i;
        return -1;
    }

    private void showCourseSelectionDialog(View view) {
        Log.d(TAG,"showcourse started");
        String[] macroAreas = initMacroAreas(requireActivity().getApplicationContext());
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

    private String[] initMacroAreas(Context context) {
        return new String[]{context.getString(R.string.economics), context.getString(R.string.law),
                context.getString(R.string.medicine), context.getString(R.string.psychology),
                context.getString(R.string.education), context.getString(R.string.science),
                context.getString(R.string.sociology)};
    }

    private void showCoursesDialog(String selectedMacroArea) {
        // Mostra la lista dei corsi disponibili nella macroarea selezionata
        String[] courses = getAvailableCourses(requireContext(),selectedMacroArea);

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

    private String[] getAvailableCourses(Context c, String macroArea) {
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

    public void cancelFilter() {

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        // Ottieni una istanza del tuo UserViewModel
        userViewModel = new ViewModelProvider(this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        // Osserva i dati dell'utente
        userViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess()) {
                User user = ((Result.UserResponseSuccess) result).getData();
                if (user != null) {
                    // Imposta l'username dell'utente nella TextView
                    welcomeTextView.setText("Buongiorno, " + user.getUsername());
                }
            }
        });
    }

}