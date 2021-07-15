package com.adpcrypto.auroraplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.io.File;
import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class FolderAdapterAlbum extends RecyclerView.Adapter<FolderAdapterAlbum.MyHolder> {
    private List<String> folderNames;
    private final Context mContext;
    private Parcelable state;
    RecyclerView recyclerView;

    public FolderAdapterAlbum(List<String> folderNames, Context mContext) {
        this.folderNames = folderNames;
        this.mContext = mContext;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(mContext).inflate(R.layout.album_item_grid,parent,false);



        
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.folder.setText(folderNames.get(position));
        new Thread(()->{
                int i = audioFileDatabase.audioFileDao().selectCountAlbum(folderNames.get(position));
                if(i>1)
                    holder.folderno.post(() -> holder.folderno.setText(i +" items"));
                else
                    holder.folderno.post(() -> holder.folderno.setText(i +" item"));
        }).start();
             new Thread(()->{
                 CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
                 circularProgressDrawable.setStrokeWidth(5f);
                 circularProgressDrawable.setCenterRadius(30f);
                 String p =audioFileDatabase.audioFileDao().getFileWithAlbum(folderNames.get(position)).get(0).getPath();
                 Bitmap b =coverpicture(p);
                 if(b==null)
                     Glide.with(mContext).downloadOnly()
                             .load(new File(p));
                 else
                     Glide.with(mContext).downloadOnly()
                             .load(b);
                 holder.imageView.post(() -> {
                     circularProgressDrawable.start();
                     Glide.with(mContext).clear(holder.imageView);
                     if(b==null) {
                         Glide.with(mContext)
                                 .load(new File(p))
                                 .placeholder(circularProgressDrawable)
                                 .format(DecodeFormat.PREFER_RGB_565)
                                 .into(new DrawableImageViewTarget(holder.imageView));
                     }
                     else {
                         Glide.with(mContext)
                                 .load(b)
                                 .placeholder(circularProgressDrawable)
                                 .format(DecodeFormat.PREFER_RGB_565)
                                 .into(new DrawableImageViewTarget(holder.imageView));
                     }

                 });

             }).start();
        holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, AudioFileActivity.class);
                intent.putExtra("album",folderNames.get(position));
                mContext.startActivity(intent);

        });
    }



    @Override
    public int getItemCount() {
        return folderNames.size();
    }

    public void setItems(List<String> audioFiles) {
        if(recyclerView.getLayoutManager()!=null)
            if(recyclerView.getLayoutManager().onSaveInstanceState()!=null)
                state = recyclerView.getLayoutManager().onSaveInstanceState();
        folderNames = audioFiles;
        new Handler(Looper.getMainLooper()).post(()-> {
            notifyDataSetChanged();
            if(state!=null)
                recyclerView.getLayoutManager().onRestoreInstanceState(state);
        });



    }

    public class MyHolder extends RecyclerView.ViewHolder{
        final TextView folder,folderno;
        final ImageView imageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            folder= itemView.findViewById(R.id.folder_name_text);
            folderno = itemView.findViewById(R.id.folder_no_text);
            imageView = itemView.findViewById(R.id.folder_img);
        }
    }

    static Bitmap coverpicture(String path) {
        MediaMetadataRetriever mr;
        byte[] byte1 = new byte[1];

        mr = new MediaMetadataRetriever();
        mr.setDataSource(path);

        try {
            byte1 = mr.getEmbeddedPicture();
            mr.release();
        }catch (Exception e){
            e.printStackTrace();
        }




        if(byte1 != null) {
            return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
        }
        else {
            return null;
        }

    }
}
