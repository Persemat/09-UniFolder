package com.example.unifolder;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.unifolder.Ui.Welcome.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UploadInstrumentedTest {
    private static final String ANIMATION_IDLING_RESOURCE_NAME = "AnimationIdlingResource";
    private CountingIdlingResource animationIdlingResource;

    // Regola per avviare Activity nel test
    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);
    //public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);



    @Before
    public void setUp() {
        // Disabilita le animazioni

        // Lingua it

    }

    @Before
    public void login() throws InterruptedException {
        // performs Login action
        String user = "test@foo.it"; String pwd = "admin1";

        Thread.sleep(100);
        onView(withId(R.id.text_input_email)).perform(typeText(user));
        Thread.sleep(100);
        onView(withId(R.id.text_input_password)).perform(typeText(pwd));
        Thread.sleep(100);
        // closes keyboard
        Espresso.onView(isRoot()).perform(ViewActions.pressBack());

        onView(withId(R.id.button_login)).perform(click());
        Thread.sleep(1000);
    }

    //@Test
    public void successLogin() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void testUploadWithTitleErrorInteraction() throws InterruptedException {
        // Fai clic sul pulsante che avvia l'UploadFragment
        onView(ViewMatchers.withId(R.id.uploadFragment)).perform(click());
        Thread.sleep(2000);

        // Simula l'input sui campi dell'UploadFragment
        String title = "Test Title";
        String course = "Test Course";
        String tag = "Test Tag";


        onView(withId(R.id.course_editText)).perform(click());
        onView(ViewMatchers.withText("Scienze")).perform(click());
        onView(ViewMatchers.withText(containsString("INFORMATICA"))).perform(click());
        onView(withId(R.id.tag_spinner)).perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withText("Altro")).perform(click());


        // Esegui l'azione di caricamento (supponendo che sia un pulsante con id R.id.upload_document_button)
        onView(withId(R.id.submit_button)).perform(click());

        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(withText(R.string.title_error))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testUploadWithCourseErrorInteraction() throws InterruptedException {
        // Fai clic sul pulsante che avvia l'UploadFragment
        onView(ViewMatchers.withId(R.id.uploadFragment)).perform(click());
        Thread.sleep(2000);

        // Simula l'input sui campi dell'UploadFragment
        String title = "Test Title";
        String course = "Test Course";
        String tag = "Test Tag";


        onView(ViewMatchers.withId(R.id.title_editText)).perform(typeText(title));
        onView(withId(R.id.tag_spinner)).perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withText("Altro")).perform(click());

        // closes keyboard
        Espresso.closeSoftKeyboard();


        // Esegui l'azione di caricamento (supponendo che sia un pulsante con id R.id.upload_document_button)
        onView(withId(R.id.submit_button)).perform(click());

        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(withText(R.string.course_error))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testUploadWithFileErrorInteraction() throws InterruptedException {
        // Fai clic sul pulsante che avvia l'UploadFragment
        onView(ViewMatchers.withId(R.id.uploadFragment)).perform(click());
        Thread.sleep(2000);

        // Simula l'input sui campi dell'UploadFragment
        String title = "Test Title";
        String course = "Test Course";
        String tag = "Test Tag";


        onView(ViewMatchers.withId(R.id.title_editText)).perform(typeText(title));
        onView(withId(R.id.course_editText)).perform(click());
        onView(ViewMatchers.withText("Scienze")).perform(click());
        onView(ViewMatchers.withText(containsString("INFORMATICA"))).perform(click());
        onView(withId(R.id.tag_spinner)).perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withText("Altro")).perform(click());

        // closes keyboard
        Espresso.closeSoftKeyboard();

        // Esegui l'azione di caricamento (supponendo che sia un pulsante con id R.id.upload_document_button)
        onView(withId(R.id.submit_button)).perform(click());

        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(withText(R.string.file_error))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void tearDown() {
        // Ripristina le animazioni dopo il test
    }
}
