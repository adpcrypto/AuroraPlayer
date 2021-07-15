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

import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;

public class FolderAdapterAudio extends RecyclerView.Adapter<FolderAdapterAudio.MyHolder> {
    private List<String> folderNames;
    private final Context mContext;
    private Parcelable state;
    RecyclerView recyclerView;

    public FolderAdapterAudio(List<String> folderNames, Context mContext) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.folder_item_list,parent,false);



        
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.folder.setText(folderNames.get(position));
        new Thread(()->{
                int i = audioFileDatabase.audioFileDao().selectCountFolder(folderNames.get(position));
                if(i>1)
                    holder.folderno.post(() -> holder.folderno.setText(i +" items"));
                else
                    holder.folderno.post(() -> holder.folderno.setText(i +" item"));

        }).start();

        holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, AudioFileActivity.class);
                intent.putExtra("foldername",folderNames.get(position));
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
