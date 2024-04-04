package com.example.unifolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Welcome.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private TextView welcomeTextView;
    private UserViewModel userViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        welcomeTextView = view.findViewById(R.id.welcome_textview);

      /*  // Dentro il tuo fragment o activity
        RecyclerView recyclerView = findViewById(R.id.documents_recyclerview);
       List<Document> documents = // Recupera la lista di documenti dal tuo database o da altre fonti
                DocumentAdapter adapter = new DocumentAdapter(documents);
       recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
*/
        return view;
        // Inflate the layout for this fragment
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni una istanza del tuo UserViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

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