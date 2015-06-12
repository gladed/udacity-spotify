package com.udacity.gladed.spotifystreamer.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.udacity.gladed.spotifystreamer.R;

import java.util.ArrayList;
import java.util.List;

/** An adapter for showing lists of images (e.g. artist or albums) */
public class ImageAdapter<T> extends RecyclerView.Adapter<ImageAdapter.ViewHolder<T>> {
    public static final String TAG = "ArtistAdapter";


    private List<ImageInfo<T>> mImageInfos = new ArrayList<>();
    private Context mContext;
    private ImageInfoSelectionListener<T> mImageInfoSelectionListener;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder<>(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder<T> holder, int position) {
        holder.setImageInfo(mContext, mImageInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageInfos.size();
    }

    public void setImageInfoSelectionListener(ImageInfoSelectionListener<T> imageInfoSelectionListener) {
        mImageInfoSelectionListener = imageInfoSelectionListener;
    }

    public void setImageInfos(List<ImageInfo<T>> images) {
        mImageInfos = images;
        // Note: we could animate with smarter notifications, but we will generally get all data at once.
        notifyDataSetChanged();
    }

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {
        ImageView mBackground;
        ImageView mImage;
        TextView mTitle;
        TextView mSubtitle;
        ImageInfo<T> mImageInfo;

        public ViewHolder(View itemView, final ImageAdapter adapter) {
            super(itemView);
            mBackground = (ImageView)itemView.findViewById(R.id.imageBackground);
            mImage = (ImageView)itemView.findViewById(R.id.image);
            mTitle = (TextView)itemView.findViewById(R.id.title);
            mSubtitle = (TextView)itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Clicked " + mImageInfo.mTitle);
                    if (adapter.mImageInfoSelectionListener != null) {
                        adapter.mImageInfoSelectionListener.onImageInfoSelected(mImageInfo);
                    }
                }
            });
        }

        void setImageInfo(Context context, ImageInfo<T> imageInfo) {
            mImageInfo = imageInfo;
            mTitle.setText(imageInfo.getTitle());

            if (!TextUtils.isEmpty(imageInfo.getSubtitle())) {
                mSubtitle.setText(imageInfo.getSubtitle());
                mSubtitle.setVisibility(View.VISIBLE);
            } else {
                mSubtitle.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(imageInfo.getImageUrl())) {
                mImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(imageInfo.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(mImage);

                Glide.with(context)
                        .load(imageInfo.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(mBackground);

            }
        }
    }

    /** Unit of data shared with us, including the underlying data type whatever it is */
    public static class ImageInfo<T> {
        private T mRoot;
        private String mTitle;
        private String mSubtitle;
        private String mImageUrl;

        public ImageInfo(T root, String title, String subtitle, String imageUrl) {
            mRoot = root;
            mTitle = title;
            mSubtitle = subtitle;
            mImageUrl = imageUrl;
        }

        public T getRoot() {
            return mRoot;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getSubtitle() {
            return mSubtitle;
        }

        public String getImageUrl() {
            return mImageUrl;
        }
    }

    public interface ImageInfoSelectionListener<T> {
        void onImageInfoSelected(ImageInfo<T> selected);
    }
}
