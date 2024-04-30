package com.example.unifolder.Adapter;

import android.util.Log;
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

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HOME = 1;
    public static final int VIEW_TYPE_RESULTS = 2;
    private List<Document> documents;
    private int viewType;

    public DocumentAdapter() {
        documents = new ArrayList<>();
    }
    public DocumentAdapter(int viewType) {
        new DocumentAdapter();
        this.viewType = viewType;
    }

    public DocumentAdapter(List<Document> documents) {
        this.documents = new ArrayList<>();
    }

    public void replaceAllDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        Log.d("HomeFragment","itemViewType = " + viewType);
        if (viewType == VIEW_TYPE_HOME) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
            return new HomeDocumentViewHolder(itemView);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_results, parent, false);
            return new ResultsDocumentViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Document document = documents.get(position);
        if(holder instanceof ResultsDocumentViewHolder) {
            ((ResultsDocumentViewHolder) holder).titleTextView.setText(document.getTitle());
            ((ResultsDocumentViewHolder) holder).courseTextView.setText(document.getCourse());
            ((ResultsDocumentViewHolder) holder).tagTextView.setText(document.getTag());
        } else if(holder instanceof HomeDocumentViewHolder){
            ((HomeDocumentViewHolder) holder).titleTextView.setText(document.getTitle());
            ((HomeDocumentViewHolder) holder).courseTextView.setText(document.getCourse());
        }

    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class ResultsDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView, tagTextView;

        public ResultsDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.document_title);
            courseTextView = itemView.findViewById(R.id.document_course);
            tagTextView = itemView.findViewById(R.id.document_tag);
        }
    }

    public static class HomeDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView;

        public HomeDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            courseTextView = itemView.findViewById(R.id.course);
        }
    }
}

