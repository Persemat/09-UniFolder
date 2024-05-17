package com.example.unifolder.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifolder.R;

import java.util.List;

public class DocumentPagerAdapter extends RecyclerView.Adapter<DocumentPagerAdapter.PageViewHolder> {

    private Context mContext;
    private List<Bitmap> mDocumentPages;

    public DocumentPagerAdapter(Context context, List<Bitmap> documentPages) {
        mContext = context;
        mDocumentPages = documentPages;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.item_document_page, parent, false);
        return new PageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.imageView.setImageBitmap(mDocumentPages.get(position));
    }

    @Override
    public int getItemCount() {
        return mDocumentPages.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.document_page_image);
        }
    }
}
