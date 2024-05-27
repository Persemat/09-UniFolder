package com.example.unifolder.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.example.unifolder.R;


public class AvatarAdapter extends BaseAdapter {
    private Context mContext;

    // Array di immagini o dati che desideri visualizzare nella GridView
    private int[] profileImages = {
            R.drawable.baseline_account_circle_24,
            R.drawable.informatico,
            R.drawable.informatica,
            R.drawable.studioso,
            R.drawable.studiosa,
            R.drawable.scienziato,
            R.drawable.scienziata,
            R.drawable.infermiere,
            R.drawable.avvocato,
            R.drawable.ptdonna,
            R.drawable.ptuomo,
            R.drawable.manager
    };

    public AvatarAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return profileImages.length;
    }

    @Override
    public Object getItem(int position) {
        return profileImages[position];
    }

    @Override
    public long getItemId(int position) {
        return profileImages[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // Se la vista non è stata creata, creala
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200)); // Personalizza le dimensioni delle immagini
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            // Se la vista esiste già, riutilizzala
            imageView = (ImageView) convertView;
        }

        imageView.setTag(profileImages[position]);
        imageView.setImageResource(profileImages[position]);

        return imageView;
    }
}

