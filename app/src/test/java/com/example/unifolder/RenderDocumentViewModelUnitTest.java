package com.example.unifolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.unifolder.Data.Repository.Document.DocumentRepository;
import com.example.unifolder.Model.Document;
import com.example.unifolder.Ui.Main.OnDocumentRenderedCallback;
import com.example.unifolder.Ui.Main.RenderDocumentViewModel;
import com.example.unifolder.Ui.Main.ResultViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = ShadowLog.class)
public class RenderDocumentViewModelUnitTest {
    @Mock
    private DocumentRepository mockDocumentRepository;

    private RenderDocumentViewModel renderDocumentViewModel;

    @Before
    public void setup() {
        ShadowLog.setupLogging();
        mockDocumentRepository = mock(DocumentRepository.class);
        renderDocumentViewModel = new RenderDocumentViewModel(mockDocumentRepository);

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
    public void testRenderDocument() throws InterruptedException, ExecutionException {
        // Given
        Document document = new Document("Document 1","Author 1","Course 1","Tag 1","path/docName1.pdf");
        Context mockContext = mock(Context.class);
        List<Bitmap> mockBitmaps = new ArrayList<>();
        mockBitmaps.add(mock(Bitmap.class));

        // Stubbing the repository method call
        doAnswer(invocation -> {
            OnDocumentRenderedCallback callback = invocation.getArgument(2);
            callback.OnDocumentRendered(document, mockBitmaps);
            return null;
        }).when(mockDocumentRepository).renderDocument(eq(document), eq(mockContext), any(OnDocumentRenderedCallback.class));

        // When
        renderDocumentViewModel.renderDocument(document, mockContext);

        // Attendi fino a quando il LiveData non viene aggiornato o fino a quando scade il timeout
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        // Then
        // Verifica che il metodo di rendering del repository sia stato chiamato
        verify(mockDocumentRepository, times(1)).renderDocument(eq(document), eq(mockContext), any(OnDocumentRenderedCallback.class));
        // Verifica che i LiveData siano stati impostati correttamente
        assertEquals(document, renderDocumentViewModel.getDocumentMutableLiveData().getValue());
        assertEquals(mockBitmaps, renderDocumentViewModel.getBitmapMutableLiveData().getValue());
    }
}
