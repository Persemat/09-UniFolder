package com.example.unifolder.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifolder.Document;
import com.example.unifolder.OnDocumentClickListener;
import com.example.unifolder.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private List<Bitmap> previews;
    private OnDocumentClickListener listener;

    public DocumentAdapter(List<Document> documents, List<Bitmap> previews, OnDocumentClickListener listener) {
        this.documents = documents;
        this.previews = previews;
        this.listener = listener;
    }

    public DocumentAdapter(List<Document> documents, List<Bitmap> previews) {
        this.documents = documents;
        this.previews = previews;
    }

    public DocumentAdapter(List<Document> documents) {
        this.documents = new ArrayList<>();
    }

    public void replaceAllDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setOnDocumentClickListener(OnDocumentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_results, parent, false);
        return new DocumentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        Bitmap bitmap = previews.get(position);
        holder.titleTextView.setText(document.getTitle());
        holder.courseTextView.setText(document.getCourse());
        holder.tagTextView.setText(document.getTag());
        holder.firstPageImageView.setImageBitmap(previews.get(position));

        holder.bind(document, bitmap, listener);
        holder.bind(listener);
    }

    @Override
    public int getItemCount() {
        if (documents != null) {
            return documents.size();
        } else {
            return 0; // Se la lista è nulla, restituisci 0 elementi
        }
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView, tagTextView;
        ImageView firstPageImageView;
        OnDocumentClickListener listener;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.document_title);
            courseTextView = itemView.findViewById(R.id.document_course);
            tagTextView = itemView.findViewById(R.id.document_tag);
            firstPageImageView = itemView.findViewById(R.id.first_page_image);
        }
        public void bind(final Document document, Bitmap bitmap, final OnDocumentClickListener listener) {
            titleTextView.setText(document.getTitle());
            firstPageImageView.setImageBitmap(bitmap);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onDocumentClicked(document);
                    }
                });


        }
        public void bind(final OnDocumentClickListener listener) {
            this.listener = listener;

            // Altri codici...
        }
    }
}

