package com.example.unifolder.Adapter;

import android.graphics.Bitmap;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HOME = 1;
    public static final int VIEW_TYPE_RESULTS = 2;
    private List<Document> documents;
    private int viewType;
    private List<Bitmap> previews;
    private OnDocumentClickListener listener;


    public DocumentAdapter() {
        documents = new ArrayList<>();
    }

    public DocumentAdapter(List<Document> documents, List<Bitmap> previews, OnDocumentClickListener listener) {
        this.documents = documents;
        this.previews = previews;
        this.listener = listener;
        this.viewType = VIEW_TYPE_RESULTS;
    }

    public DocumentAdapter(List<Document> documents, List<Bitmap> previews) {
        this.documents = documents;
        this.previews = previews;
    }
    public DocumentAdapter(int viewType) {
        new DocumentAdapter();
        this.viewType = viewType;
    }

    public DocumentAdapter(List<Document> documents, List<Bitmap> previews, OnDocumentClickListener listener, int viewType) {
        this.documents = documents;
        this.previews = previews;
        this.listener = listener;
        this.viewType = viewType;
    }

    public DocumentAdapter(List<Document> documents) {
        this.documents = new ArrayList<>();
    }

    public void replaceAllDocuments(List<Document> documents) {
        Collections.reverse(documents);
        this.documents = documents;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public void setOnDocumentClickListener(OnDocumentClickListener listener) {
        this.listener = listener;
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
            Bitmap bitmap = previews.get(position);
            ((ResultsDocumentViewHolder) holder).titleTextView.setText(document.getTitle());
            ((ResultsDocumentViewHolder) holder).courseTextView.setText(document.getCourse());
            ((ResultsDocumentViewHolder) holder).tagTextView.setText(document.getTag());
            ((ResultsDocumentViewHolder) holder).firstPageImageView.setImageBitmap(previews.get(position));
            ((ResultsDocumentViewHolder) holder).bind(document, bitmap, listener);
            ((ResultsDocumentViewHolder) holder).bind(listener);
        } else if(holder instanceof HomeDocumentViewHolder){
            Bitmap bitmap = previews.get(position);
            ((HomeDocumentViewHolder) holder).titleTextView.setText(document.getTitle());
            ((HomeDocumentViewHolder) holder).courseTextView.setText(document.getCourse());
            ((HomeDocumentViewHolder) holder).bind(document, bitmap, listener);
            ((HomeDocumentViewHolder) holder).bind(listener);
        }

    }

    @Override
    public int getItemCount() {
        if (documents != null) {
            return documents.size();
        } else {
            return 0; // Se la lista è nulla, restituisci 0 elementi
        }
    }

    public static class ResultsDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView, tagTextView;
        ImageView firstPageImageView;
        OnDocumentClickListener listener;

        public ResultsDocumentViewHolder(@NonNull View itemView) {
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

    public static class HomeDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, courseTextView;
        ImageView firstPageImageView;
        OnDocumentClickListener listener;

        public HomeDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            courseTextView = itemView.findViewById(R.id.course);
            firstPageImageView = itemView.findViewById(R.id.doc_preview);
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

