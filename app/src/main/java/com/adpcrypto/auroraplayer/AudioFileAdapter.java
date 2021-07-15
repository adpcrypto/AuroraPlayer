package com.adpcrypto.auroraplayer;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.adpcrypto.auroraplayer.classes.AudioDiff;
import com.adpcrypto.auroraplayer.classes.AudioFile;
import com.adpcrypto.auroraplayer.classes.InternalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.adpcrypto.auroraplayer.StartActivity.audioFileDatabase;
import static java.lang.String.*;


public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.MyHolder> {
    final private Context mContext;
    private  List<AudioFile> videoFiles;
    RecyclerView recyclerView;
    private Parcelable state;
    final String where;
    static Thread t;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public AudioFileAdapter(Context mContext, List<AudioFile> videoFiles,String where) {
        this.mContext = mContext;
        this.videoFiles = videoFiles;
        state =null;
        this.where = where;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_item,parent,false);
        return new MyHolder(view);
    }
    public void setItems(List<AudioFile> persons) {
        state = recyclerView.getLayoutManager().onSaveInstanceState();
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AudioDiff(this.videoFiles,persons));



        this.videoFiles = persons;

        new Handler(Looper.getMainLooper()).post(()->{diffResult.dispatchUpdatesTo(this);
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
        });




    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        if(t!=null){
            if(t.isAlive()){
                t.interrupt();
            }
        }
        t= new Thread(() -> {
            String str =videoFiles.get(position).getFileName();
            float s = Float.valueOf(videoFiles.get(position).getSize());
            float m = (float) (s/1048576.00);
            String g;
            if(m > 1000){
                g = format("%.2f", m/1000);
                g = g+ "GB";
            }else if(m<1){
                g = format("%.2f", m*1000);
                g = g+ "KB";
            }else{
                g = format("%.2f", m);
                g= g+"MB";
            }
            int t = Integer.valueOf(videoFiles.get(position).getDuration())/1000;
            int h;
            h = t/3600;
            String sm = format("%02d", (t%3600)/60);
            String ss = format("%02d", t%60);
            if(h!=0) {
                holder.duration.post(() -> holder.duration.setText(h + ":" + sm + ":" + ss));
            }else{
                holder.duration.post(() -> holder.duration.setText( sm + ":" + ss));
            }
            holder.fileName.post(() -> holder.fileName.setText(str.substring(0, str.lastIndexOf('.'))));
            String finalG = g;
            holder.fileSize.post(() -> holder.fileSize.setText(finalG));

            if(videoFiles.get(position).getIsfav()){
                holder.favourite.post(() -> holder.favourite.setBackgroundResource(R.drawable.ic_baseline_favorite_filled));
            }else{
                holder.favourite.post(() -> holder.favourite.setBackgroundResource(R.drawable.ic_baseline_favorite_border));
            }

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            Bitmap b = coverPicture(videoFiles.get(position).getPath());
            if(b==null)
                Glide.with(mContext).downloadOnly()
                        .load(new File(videoFiles.get(position).getPath()));
            else
                Glide.with(mContext).downloadOnly()
                        .load(b);
            holder.thumbnail.post(() -> {
                circularProgressDrawable.start();
                Glide.with(mContext).clear(holder.thumbnail);
                if(b==null) {
                    Glide.with(holder.thumbnail.getContext())
                            .load(new File(videoFiles.get(position).getPath()))
                            .placeholder(circularProgressDrawable)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .into(new DrawableImageViewTarget(holder.thumbnail));
                }
                else {
                    Glide.with(holder.thumbnail.getContext())
                            .load(b)
                            .placeholder(circularProgressDrawable)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .into(new DrawableImageViewTarget(holder.thumbnail));
                }

            });
        });
        t.start();





    }


    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        final ImageView thumbnail,menuMore,favourite;
        final TextView fileName,duration,fileSize;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.img_thumbnail);
            menuMore= itemView.findViewById(R.id.menu_more);
            fileName= itemView.findViewById(R.id.file_name);
            favourite = itemView.findViewById(R.id.fav_button);
            duration = itemView.findViewById(R.id.video_duration_text);
            fileSize = itemView.findViewById(R.id.file_size);


            favourite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(videoFiles.get(position).getIsfav()){
                    videoFiles.get(position).setIsfav(false);
                    view.setBackgroundResource(R.drawable.ic_baseline_favorite_border);
                }else {
                    videoFiles.get(position).setIsfav(true);
                    view.setBackgroundResource(R.drawable.ic_baseline_favorite_filled);
                }
                new Thread(()-> {
                    audioFileDatabase.audioFileDao().updateAudioFile(videoFiles.get(position));
                    List<AudioFile> temp;
                    temp = audioFileDatabase.audioFileDao().getAudioFilesStatic();
                    Collections.sort(temp, (s1, s2) -> s1.getFileName().compareToIgnoreCase(s2.getFileName()));
                    try {
                        InternalStorage.writeObject(mContext,"my_key",temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });



            menuMore.setOnClickListener(view -> {

                int position = getAdapterPosition();
                PopupMenu popup = new PopupMenu(mContext, view);
                popup.inflate(R.menu.menu_more_menu);
                popup.setOnMenuItemClickListener(menuItem -> {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.item_delete) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                .setMessage("Do you want to delete" + videoFiles.get(position).getPath())
                                .setPositiveButton("YES", (dialogInterface, i) -> {
                                    new Thread(() -> {
                                        Uri u = Uri.fromFile(new File(videoFiles.get(position).getPath()));
                                        new File(videoFiles.get(position).getPath()).delete();
                                        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, u));
                                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show());
                                    }).start();
                                    new Thread(()->audioFileDatabase.audioFileDao().deleteAudioFile(videoFiles.get(position))).start();
                                    dialogInterface.dismiss();
                                }).setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                        alertDialog.show();
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                .setTextColor(ContextCompat.getColor(mContext, R.color.dialogbutoncolor));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(mContext, R.color.dialogbutoncolor));
                        return true;
                    } else if (itemId == R.id.item_prop) {
                        LayoutInflater factory = LayoutInflater.from(mContext);
                        final View deleteDialogView = factory.inflate(R.layout.properties, null);
                        final AlertDialog deleteDialog = new AlertDialog.Builder(mContext)
                                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                        deleteDialog.setView(deleteDialogView);

                        TextView t1, t2, t3, t4, t7, t8;
                        t1 = deleteDialogView.findViewById(R.id.path);
                        t1.setText(videoFiles.get(position).getPath());

                        t2 = deleteDialogView.findViewById(R.id.file_name_text);
                        t2.setText(videoFiles.get(position).getFileName());

                        float s = Float.valueOf(videoFiles.get(position).getSize());
                        float m = (float) (s / 1048576.00);
                        String g;
                        if (m > 1000) {
                            g = format("%.2f", m / 1000);
                            g = g + "GB";
                        } else if (m < 1) {
                            g = format("%.2f", m * 1000);
                            g = g + "KB";
                        } else {
                            g = format("%.2f", m);
                            g = g + "MB";
                        }
                        int t = Integer.valueOf(videoFiles.get(position).getDuration()) / 1000;
                        int h;
                        h = t / 3600;
                        String sm = format("%02d", (t % 3600) / 60);
                        String ss = format("%02d", t % 60);

                        t3 = deleteDialogView.findViewById(R.id.file_size_text);
                        t3.setText(g + "");

                        t4 = deleteDialogView.findViewById(R.id.file_dur_text);
                        if (h != 0) {
                            t4.setText(h + ":" + sm + ":" + ss);
                        } else {
                            t4.setText(sm + ":" + ss);
                        }


                        t7 = deleteDialogView.findViewById(R.id.file_album_text);
                        t7.setText(videoFiles.get(position).getAlbum());

                        t8 = deleteDialogView.findViewById(R.id.file_artist_text);
                        if (videoFiles.get(position).getAlbum_artist() == null) {
                            t8.setText(videoFiles.get(position).getKey_artist());
                        } else {
                            t8.setText(videoFiles.get(position).getAlbum_artist());
                        }

                        deleteDialog.show();
                        deleteDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(mContext, R.color.dialogbutoncolor));


                        return true;
                    }
                    return false;
                });
                popup.show();
            });

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("PATH",videoFiles.get(getAdapterPosition()).getPath());
                intent.putExtra("WHERE",where);
                mContext.startActivity(intent);
            });


        }
    }
    static Bitmap coverPicture(String path) {
        MediaMetadataRetriever mr;
        byte[] byte1 = new byte[1];
        mr = new MediaMetadataRetriever();
        try {
            mr.setDataSource(path);
        }catch (Exception e){
            return null;
        }
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
