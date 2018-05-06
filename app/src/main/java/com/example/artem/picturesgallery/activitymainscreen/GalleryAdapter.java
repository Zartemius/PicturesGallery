package com.example.artem.picturesgallery.activitymainscreen;

/*
*В данном классе реализован адаптер для RecyclerView из ActivityMainScreen.
* Так же в нем с помощью интерфейса OnItemClickListener реализована возможность делать элементы RecyclerView кликабельным.
* Картинки загружаются в ImageView через библиотеку Picasso.
*/

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.artem.picturesgallery.R;
import com.example.artem.picturesgallery.model.InfoAboutPicture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.StatsSnapshot;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.NewViewHolder> {

    private Context mContext;
    private List<InfoAboutPicture> mListOfItems;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public GalleryAdapter(Context context, List<InfoAboutPicture> listOfItems){
        mContext = context;
        mListOfItems = listOfItems;
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder{
        ImageView picture;

        public NewViewHolder(View viewItem, final OnItemClickListener listener){

            super (viewItem);
            picture = itemView.findViewById(R.id.item_view_holder__picture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                       int position = getAdapterPosition();
                       if(position != RecyclerView.NO_POSITION){
                           listener.onItemClick(position);
                       }
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(NewViewHolder holder, int position) {
        InfoAboutPicture currentItem = mListOfItems.get(position);
        String urlOfImage = currentItem.getWebformatURL();

        RequestCreator rc = Picasso.get()
                .load(urlOfImage)
                .fit()
                .centerCrop();

        Log.i("LINK", ""+currentItem.getWebformatURL());
        rc.into(holder.picture, new Callback() {
            @Override
            public void onSuccess() {
                Log.i("SUCCESS_LISTENER", ""+currentItem.getWebformatURL());
            }

            @Override
            public void onError(Exception e) {
                Log.i("ERROR_LISTENER", ""+currentItem.getWebformatURL());
            }
        });
        StatsSnapshot ss = Picasso.get().getSnapshot();

        Log.i("download image hits","" +ss.cacheHits);
        Log.i("download image misses","" +ss.cacheMisses);
        Log.i("download image count","" +ss.downloadCount);
        Log.i("download image size","" +ss.totalDownloadSize);
    }

    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view_holder,parent,false);
        NewViewHolder mNewViewHolder = new NewViewHolder(view,mOnItemClickListener);
        return mNewViewHolder;
    }

    @Override
    public int getItemCount() {
        return mListOfItems.size();
    }
}
