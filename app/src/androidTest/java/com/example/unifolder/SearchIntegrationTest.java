package com.example.unifolder;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.unifolder.Ui.Welcome.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class SearchIntegrationTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

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
        Espresso.closeSoftKeyboard();
        Thread.sleep(100);

        onView(withId(R.id.button_login)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void successLogin() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void testTitleSearchOkInteraction() throws InterruptedException {
        String titleWithMatch = "Sicurezza";

        Thread.sleep(100);
        onView(withId(R.id.search_view)).perform(click());
        Thread.sleep(100);
        onView(instanceOf(SearchView.SearchAutoComplete.class)).perform(typeText(titleWithMatch),
                pressKey(KeyEvent.KEYCODE_ENTER));

        Thread.sleep(1000);


        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(titleWithMatch))))));
    }

    @Test
    public void testTitleSearchNoResultInteraction() throws InterruptedException {
        String titleWithMatch = "NoMatchingTitles?xyz";

        Thread.sleep(100);
        onView(withId(R.id.search_view)).perform(click());
        Thread.sleep(100);
        onView(instanceOf(SearchView.SearchAutoComplete.class)).perform(typeText(titleWithMatch),
                pressKey(KeyEvent.KEYCODE_ENTER));

        Thread.sleep(1000);


        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(allOf(withId(R.id.errorNoResult_layout), isDisplayed()));
    }

    @Test
    public void testFilterSearchOkInteraction() throws InterruptedException {
        String tagChoice = "Altro", macroareaChoice = "Scienze", courseChoice = "INFORMATICA";
        int tagChoicePosition = 3; // position for "Altro"
        String emptyTitleTrigger = "a";

        Thread.sleep(100);
        onView(withId(R.id.filter_button)).perform(click());
        Thread.sleep(100);
        onData(instanceOf(String.class)).inAdapterView(withId(R.id.filter_tag_spinner)).atPosition(tagChoicePosition).perform();
        Thread.sleep(100);

        onView(ViewMatchers.withText(containsString("Seleziona"))).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText(macroareaChoice)).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText(containsString(courseChoice))).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText("Filtra")).perform(click());

        Thread.sleep(100);
        onView(withId(R.id.search_view)).perform(click());
        Thread.sleep(100);
        onView(instanceOf(SearchView.SearchAutoComplete.class)).perform(typeText(emptyTitleTrigger),
                pressKey(KeyEvent.KEYCODE_ENTER));

        Thread.sleep(1000);


        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(courseChoice))))));
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(tagChoice))))));
    }

    @Test
    public void testCombinedSearchOkInteraction() throws InterruptedException {
        String titleWithMatch = "Sicurezza";
        String tagChoice = "Appunti lezione", macroareaChoice = "Scienze", courseChoice = "INFORMATICA";
        int tagChoicePosition = 1; // position for "Appunti lezione"

        Thread.sleep(100);
        onView(withId(R.id.filter_button)).perform(click());
        Thread.sleep(100);
        onData(instanceOf(String.class)).inAdapterView(withId(R.id.filter_tag_spinner)).atPosition(tagChoicePosition).perform();
        Thread.sleep(100);

        onView(ViewMatchers.withText(containsString("Seleziona"))).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText(macroareaChoice)).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText(containsString(courseChoice))).perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withText("Filtra")).perform(click());

        Thread.sleep(100);
        onView(withId(R.id.search_view)).perform(click());
        Thread.sleep(100);
        onView(instanceOf(SearchView.SearchAutoComplete.class)).perform(typeText(titleWithMatch),
                pressKey(KeyEvent.KEYCODE_ENTER));

        Thread.sleep(3000);


        // Verifica che un elemento specifico sia visualizzato dopo l'azione di caricamento
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(titleWithMatch))))));
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(courseChoice))))));
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .check(matches(atPosition(0,hasDescendant(withText(containsString(tagChoice))))));
    }

    @Test
    public void testSearchResultAndDetailOkInteraction() throws InterruptedException{
        String titleWithMatch = "Sicurezza";
        String tagChoice = "Appunti lezione";
        String courseChoice = "INFORMATICA";

        testCombinedSearchOkInteraction();
        /*onView(withId(R.id.recycler_view).matches(atPosition(0,hasDescendant(withText(containsString(titleWithMatch)))))).perform(click());
         */
        // Fai clic sul primo elemento nella RecyclerView
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));

        Thread.sleep(3000); // Attendi che il nuovo fragment venga visualizzato

        // Verifica che i dettagli del documento siano corretti
        onView(withId(R.id.doc_title)).check(matches(withText(containsString(titleWithMatch))));
        onView(withId(R.id.doc_course)).check(matches(withText(containsString(courseChoice))));
        onView(withId(R.id.doc_tag)).check(matches(withText(containsString(tagChoice))));

        // Verifica che l'immagine del documento sia visualizzata
        onView(withId(R.id.pdf_image)).check(matches(isDisplayed()));
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Elemento in posizione: " + position + " ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // La posizione non è valida
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
