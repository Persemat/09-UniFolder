package com.example.unifolder;

import static com.example.unifolder.Util.LocalStorageManager.saveFileLocally;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.content.Context;
import android.os.Environment;

import androidx.test.core.app.ApplicationProvider;

import com.example.unifolder.Ui.Main.HomeViewModel;
import com.example.unifolder.Util.LocalStorageManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.versioning.AndroidVersions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class LocalStorageManagerUnitTest {

    @Mock
    Context mockContext;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
            MockitoAnnotations.initMocks(this);
        }

    @Test
    public void testSaveFileLocally() throws Exception {
        // Given
        String fileName = "testFile.txt";
        String fileContent = "mocked content";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());

        // Create a real directory using TemporaryFolder
        File directory = temporaryFolder.newFolder("downloads");

        // Mocking context.getExternalFilesDir to return the real directory
        when(mockContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).thenReturn(directory);

        // Create a mock FileOutputStream
        FileOutputStream mockOutputStream = mock(FileOutputStream.class);

        // Mocking FileOutputStream constructor to return mockOutputStream
        whenNew(FileOutputStream.class).withAnyArguments().thenReturn(mockOutputStream);

        // Act
        String result = LocalStorageManager.saveFileLocally(mockContext, inputStream, fileName);

        // Assert
        assertEquals(new File(directory, fileName).getAbsolutePath(), result);
    }
}

