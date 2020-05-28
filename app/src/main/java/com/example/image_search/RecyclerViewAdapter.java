package com.example.image_search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ProductHolder> {
    @NonNull
    private Context mContext;
    private List< Product > mProductList;
    public static final String IMG_URL = "http://192.168.43.131:5000/image/";
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class  ProductHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mSiteName;
        TextView mPrice;
        ImageView mImage;

        public ProductHolder(final Context context, View itemView, final OnItemClickListener listener){
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mSiteName = itemView.findViewById(R.id.siteName);
            mImage = itemView.findViewById(R.id.image);
            mPrice = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mProductList.get(position).getUrl()));
                        context.startActivity(browserIntent);
                    }
                }
                }
            });
        }
    }

    RecyclerViewAdapter(Context mContext, List< Product > mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
        return new ProductHolder(parent.getContext(), mView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder viewHolder, int i) {
        Glide.with(this.mContext)
                .load(mProductList.get(i).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.mImage);

        viewHolder.mName.setText(mProductList.get(i).getName());
        viewHolder.mSiteName.setText(mProductList.get(i).getSiteName());
        viewHolder.mPrice.setText("IDR "+mProductList.get(i).getPrice());
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }
}

