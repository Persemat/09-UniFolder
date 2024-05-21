package com.example.unifolder;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class HomeViewModelUnitTest {
    private HomeViewModel homeViewModel;
    private Context context;

    @Before
    public void setUploadViewModel() {
        context = ApplicationProvider.getApplicationContext();
        homeViewModel = new HomeViewModel();
    }

    @Test
    public void testFindIndex_ReturnsOk() {
        String[] opts = {"opt1", "opt2"};

        assertEquals(homeViewModel.findIndex(opts,opts[0]), 0);
        assertEquals(homeViewModel.findIndex(opts,opts[1]), 1);
        assertEquals(homeViewModel.findIndex(opts,"opt"), -1);
    }
}
