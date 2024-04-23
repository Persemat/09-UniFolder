package com.example.unifolder;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UploadViewModelUnitTest {
    private UploadViewModel uploadViewModel;

    @Before
    public void setUploadViewModel() {
        uploadViewModel = new UploadViewModel();
    }

    @Test
    public void testGetFileSizeString_ReturnsOk() {
        String zero = uploadViewModel.getFileSizeString(0);
        String oneKByte = uploadViewModel.getFileSizeString(1024);
        String oneMByte = uploadViewModel.getFileSizeString(1024*1024);

        assertEquals("0 B",zero);
        assertEquals("1,00 KB",oneKByte);
        assertEquals("1,00 MB",oneMByte);
    }
}