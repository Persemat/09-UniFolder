package com.example.unifolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import android.util.Log;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.unifolder.Data.Repository.Document.DocumentRepository;
import com.example.unifolder.Model.Document;
import com.example.unifolder.Ui.Main.ResultViewModel;
import com.example.unifolder.Ui.Main.SearchResultCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = ShadowLog.class)
public class ResultViewModelUnitTest {

    @Mock
    private DocumentRepository mockDocumentRepository;

    private ResultViewModel resultViewModel;

    @Before
    public void setup() {
        ShadowLog.setupLogging();
        mockDocumentRepository = mock(DocumentRepository.class);
        resultViewModel = new ResultViewModel(mockDocumentRepository);

        // Inizializza ArchTaskExecutor per eseguire operazioni sul thread principale
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });
    }

    @After
    public void cleanup() {
        // Ripristina l'implementazione predefinita di ArchTaskExecutor
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void testSearchDocuments_Ok() throws InterruptedException {
        // Given
        String query = "test";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));
        mockDocuments.add(new Document("Document 2","Author 2","Course 2","Tag 2","path/docName2.pdf"));

        // Simulare la chiamata al repository
        doAnswer(invocation -> {
            SearchResultCallback callback = invocation.getArgument(1);
            callback.OnSearchCompleted(mockDocuments);
            return null;
        }).when(mockDocumentRepository).searchDocumentByTitle(eq(query), any(SearchResultCallback.class));

        // Quando
        resultViewModel.searchDocuments(query);

        // Attendi fino a quando il LiveData non viene aggiornato o fino a quando scade il timeout
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        // Quindi
        assertEquals(mockDocuments, resultViewModel.getSearchResultsLiveData().getValue());
        // Verifica che il metodo di ricerca del repository sia stato chiamato
        verify(mockDocumentRepository, times(1)).searchDocumentByTitle(eq(query), any(SearchResultCallback.class));
    }

    @Test
    public void testSearchDocumentsWithTag_Ok() throws InterruptedException {
        // Given
        String query = "test";
        String tag = "tag";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));
        mockDocuments.add(new Document("Document 2","Author 2","Course 2","Tag 2","path/docName2.pdf"));

        // Simulare la chiamata al repository
        doAnswer(invocation -> {
            SearchResultCallback callback = invocation.getArgument(2);
            callback.OnSearchCompleted(mockDocuments);
            return null;
        }).when(mockDocumentRepository).searchDocumentByFilter(eq(query), eq(tag), any(SearchResultCallback.class));

        // Quando
        resultViewModel.searchDocuments(query, tag);

        // Attendi fino a quando il LiveData non viene aggiornato o fino a quando scade il timeout
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        // Quindi
        assertEquals(mockDocuments, resultViewModel.getSearchResultsLiveData().getValue());
        // Verifica che il metodo di ricerca del repository sia stato chiamato
        verify(mockDocumentRepository, times(1)).searchDocumentByFilter(eq(query), eq(tag), any(SearchResultCallback.class));
    }

    @Test
    public void testSearchDocumentsWithTagAndCourse_Ok() throws InterruptedException {
        // Given
        String query = "test";
        String tag = "tag";
        String course = "course";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));
        mockDocuments.add(new Document("Document 2","Author 2","Course 2","Tag 2","path/docName2.pdf"));

        // Simulare la chiamata al repository
        doAnswer(invocation -> {
            SearchResultCallback callback = invocation.getArgument(3);
            callback.OnSearchCompleted(mockDocuments);
            return null;
        }).when(mockDocumentRepository).searchDocumentByTitleAndFilter(eq(query), eq(course), eq(tag), any(SearchResultCallback.class));

        // Quando
        resultViewModel.searchDocuments(course, tag, query);

        // Attendi fino a quando il LiveData non viene aggiornato o fino a quando scade il timeout
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        // Quindi
        assertEquals(mockDocuments, resultViewModel.getSearchResultsLiveData().getValue());
        // Verifica che il metodo di ricerca del repository sia stato chiamato
        verify(mockDocumentRepository, times(1)).searchDocumentByTitleAndFilter(eq(query), eq(course), eq(tag), any(SearchResultCallback.class));
    }
}
