package com.example.unifolder;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.net.Uri;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class TestFail {
    private MockApplication mockApplication;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Mock
    private UploadViewModel uploadViewModel;

    private UploadFragment uploadFragment;

    @Before
    public void setup() {
        // Inizializza i mock
        //MockitoAnnotations.openMocks(this);

        // Ottieni l'istanza di UploadFragment all'interno di MainActivity
        activityScenarioRule.getScenario().onActivity(activity -> {
            uploadFragment = (UploadFragment) activity.getSupportFragmentManager().getFragments().get(0); // Assumi che UploadFragment sia il primo fragment
        });

        mockApplication = Mockito.mock(MockApplication.class);
        // Sovrascrivi il provider di ViewModel per restituire un mock del ViewModel desiderato
        UploadViewModelFactory providerFactory = new UploadViewModelFactory(mockApplication);
        when(providerFactory.create(eq(UploadViewModel.class))).thenReturn(uploadViewModel);

        // Inietta il provider di ViewModel personalizzato nel fragment
        //uploadFragment.setViewModelProviderFactory(providerFactory);
    }

    @Test
    public void testUploadButtonClicked() {
       // FragmentScenario<UploadFragment> scenario = FragmentScenario.launchInContainer(UploadFragment.class);
        // Simula il click sul pulsante submitButton
        onView(withId(R.id.submit_button)).perform(click());

        // Esegui le verifiche sui valori di input e sul comportamento del viewmodel
        // Ad esempio, verifica che il metodo checkInputValuesAndUpload sia stato chiamato correttamente
        String title = "Test Title";
        String course = "Test Course";
        String tag = "Test Tag";
        Uri selectedFileUri = Uri.parse("content://test/selectedFile");

        // Verifica che il metodo checkInputValuesAndUpload sia stato chiamato con i valori di input attesi
        verify(uploadViewModel).checkInputValuesAndUpload(
                eq(title),
                eq("username"), // Fornisci il valore di username appropriato per il test
                eq(course),
                eq(tag),
                eq(selectedFileUri),
                any(View.class) // Verifica che sia passata una istanza di View corretta
        );
    }

}

class MockApplication extends Application {

}
