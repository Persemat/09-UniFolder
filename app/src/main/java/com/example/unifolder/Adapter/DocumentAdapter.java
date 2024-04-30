package com.example.unifolder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifolder.Document;
import com.example.unifolder.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private List<Document> documents;

    public DocumentAdapter(List<Document> documents) {
        this.documents = new ArrayList<>();
    }

    public void addDocuments(List<Document> documents) {
        documents.clear();
        this.documents.addAll(documents);
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
        holder.titleTextView.setText(document.getTitle());
        holder.courseTextView.setText(document.getCourse());
        holder.tagTextView.setText(document.getTag());
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView, tagTextView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.document_title);
            courseTextView = itemView.findViewById(R.id.document_course);
            tagTextView = itemView.findViewById(R.id.document_tag);
        }
    }
}

