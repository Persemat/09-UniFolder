package com.example.unifolder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;

import com.example.unifolder.Data.Repository.Document.DocumentRepository;
import com.example.unifolder.Data.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Data.Source.Document.DocumentRemoteDataSource;
import com.example.unifolder.Model.Document;
import com.example.unifolder.Ui.Main.OnDocumentRenderedCallback;
import com.example.unifolder.Ui.Main.SavedDocumentCallback;
import com.example.unifolder.Ui.Main.SearchResultCallback;
import com.example.unifolder.Ui.Main.UploadDocumentCallback;
import com.example.unifolder.Util.PdfProcessor;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class DocumentRepositoryUnitTest {
    @Mock
    private DocumentRemoteDataSource mockRemoteDataSource;
    @Mock
    private DocumentLocalDataSource mockLocalDataSource;
    @Spy
    @InjectMocks
    private DocumentRepository mockDocumentRepository;
    private DocumentRepository documentRepository;
    private Context context;


    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        mockRemoteDataSource = mock(DocumentRemoteDataSource.class);
        mockLocalDataSource = mock(DocumentLocalDataSource.class);
        mockDocumentRepository = Mockito.spy(DocumentRepository.class);
        documentRepository = new DocumentRepository(mockLocalDataSource, mockRemoteDataSource);
    }

    @Test
    public void testSearchDocumentByTitle_Ok() throws Exception {
        // Given
        String searchQuery = "test";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));

        // Creare un ListenableFuture mock e impostarlo per restituire i documenti mockati quando viene richiamato
        SettableFuture<List<Document>> future = SettableFuture.create();
        future.set(mockDocuments);
        when(mockRemoteDataSource.searchDocumentsByTitle(searchQuery)).thenReturn(future);

        // Creare un SearchResultCallback mock
        SearchResultCallback mockCallback = mock(SearchResultCallback.class);

        // When
        documentRepository.searchDocumentByTitle(searchQuery, mockCallback);

        // Then
        // Verifica che il metodo di ricerca del DataSource remoto sia stato chiamato con il searchQuery
        verify(mockRemoteDataSource, times(1)).searchDocumentsByTitle(searchQuery);
        // Verifica che il callback sia stato chiamato con i documenti mockati
        verify(mockCallback, times(1)).OnSearchCompleted(mockDocuments);
    }

    @Test
    public void testSearchDocumentByFilter_Ok() throws Exception {
        // Given
        String course = "course";
        String tag = "tag";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));

        // Creare un ListenableFuture mock e impostarlo per restituire i documenti mockati quando viene richiamato
        SettableFuture<List<Document>> future = SettableFuture.create();
        future.set(mockDocuments);
        when(mockRemoteDataSource.searchDocumentsByCourseAndTag(course,tag)).thenReturn(future);

        // Creare un SearchResultCallback mock
        SearchResultCallback mockCallback = mock(SearchResultCallback.class);

        // When
        documentRepository.searchDocumentByFilter(course, tag, mockCallback);

        // Then
        // Verifica che il metodo di ricerca del DataSource remoto sia stato chiamato con il searchQuery
        verify(mockRemoteDataSource, times(1)).searchDocumentsByCourseAndTag(course, tag);
        // Verifica che il callback sia stato chiamato con i documenti mockati
        verify(mockCallback, times(1)).OnSearchCompleted(mockDocuments);
    }

    @Test
    public void testSearchDocumentByTitleAndFilter_Ok() throws Exception {
        // Given
        String searchQuery = "test";
        String course = "course";
        String tag = "tag";
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf"));

        // Creare un ListenableFuture mock e impostarlo per restituire i documenti mockati quando viene richiamato
        SettableFuture<List<Document>> future = SettableFuture.create();
        future.set(mockDocuments);
        when(mockRemoteDataSource.searchDocumentsByTitleAndFilter(searchQuery, course, tag)).thenReturn(future);

        // Creare un SearchResultCallback mock
        SearchResultCallback mockCallback = mock(SearchResultCallback.class);

        // When
        documentRepository.searchDocumentByTitleAndFilter(searchQuery, course, tag, mockCallback);

        // Then
        // Verifica che il metodo di ricerca del DataSource remoto sia stato chiamato con il searchQuery
        verify(mockRemoteDataSource, times(1)).searchDocumentsByTitleAndFilter(searchQuery, course, tag);
        // Verifica che il callback sia stato chiamato con i documenti mockati
        verify(mockCallback, times(1)).OnSearchCompleted(mockDocuments);
    }

    @Test
    public void testUploadDocument_Ok() throws InterruptedException {
        // Given
        Document mockDocument = new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf");
        SavedDocumentCallback mockCallback = mock(SavedDocumentCallback.class);

        // Configura un CountDownLatch per attendere il completamento dell'operazione asincrona
        CountDownLatch latch = new CountDownLatch(1);

        // Stubbing il metodo uploadDocument del RemoteDataSource
        doAnswer(invocation -> {
            // Simula il completamento dell'operazione asincrona chiamando direttamente il callback
            UploadDocumentCallback callback = invocation.getArgument(1);
            callback.onDocumentUploaded(mockDocument);
            // Decrementa il latch per segnalare il completamento dell'operazione
            latch.countDown();
            return null;
        }).when(mockRemoteDataSource).uploadDocument(eq(mockDocument), any(UploadDocumentCallback.class));

        // When
        documentRepository.uploadDocument(mockDocument, context, mockCallback);

        // Attendi fino a quando il latch non viene decrementato o fino a quando scade il timeout
        latch.await();

        // Then
        // Verifica che il metodo di upload del DataSource remoto sia stato chiamato con il documento mockato
        verify(mockRemoteDataSource, times(1)).uploadDocument(eq(mockDocument), any(UploadDocumentCallback.class));
        // Verifica che il metodo di salvataggio del DataSource locale sia stato chiamato con il documento caricato e il contesto mockato
        verify(mockLocalDataSource, times(1)).saveDocument(any(Document.class), any(Context.class), any(SavedDocumentCallback.class));
    }

    @Test
    public void testRenderDocument() throws InterruptedException, ExecutionException {
        // Given
        Document mockDocument = new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf");
        Context mockContext = mock(Context.class);
        OnDocumentRenderedCallback mockCallback = mock(OnDocumentRenderedCallback.class);
        CompletableFuture<Document> mockFuture = CompletableFuture.completedFuture(mockDocument);

        // Stubbing il metodo getDocumentByIdAsync per restituire un CompletableFuture completato con il documento mockato
        doReturn(mockFuture).when(mockDocumentRepository).getDocumentByIdAsync(mockDocument.getId());

        // When
        mockDocumentRepository.renderDocument(mockDocument, mockContext, mockCallback);

        // Then
        // Verifica che il metodo getDocumentByIdAsync sia stato chiamato correttamente
        verify(mockDocumentRepository, times(1)).getDocumentByIdAsync(mockDocument.getId());
        // Verifica che la callback sia stata chiamata con il documento mockato e le bitmap mockate
        verify(mockCallback, times(1)).OnDocumentRendered(any(Document.class), any(List.class));
    }
}
