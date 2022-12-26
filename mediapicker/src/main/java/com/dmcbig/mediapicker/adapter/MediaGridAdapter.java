package com.dmcbig.mediapicker.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.R;
import com.dmcbig.mediapicker.entity.Media;
import com.dmcbig.mediapicker.utils.FileUtils;
import com.dmcbig.mediapicker.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/5.
 */

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.MyViewHolder> {

    ArrayList<Media> medias;
    Context context;
    FileUtils fileUtils = new FileUtils();
    ArrayList<Media> selectMedias = new ArrayList<>();
    long maxSelect, maxSize;
    ArrayList<String> urList = new ArrayList<>();
    ArrayList<String> mimeTypeList = new ArrayList<>();
    Boolean bZoomImage;

    public MediaGridAdapter(ArrayList<Media> list, Context context, ArrayList<Media> select, int max, long maxSize, ArrayList<String> urlList, Boolean bZoomImage, ArrayList<String> mimeTypeList) {
        if (select != null) {
            this.selectMedias = select;
        }
        this.maxSelect = max;
        this.maxSize = maxSize;
        this.medias = list;
        this.context = context;
        this.urList = urlList;
        this.mimeTypeList = mimeTypeList;
        this.bZoomImage = bZoomImage;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView media_image, check_image;
        public View mask_view, check_view;
        public TextView textView;
        public RelativeLayout gif_info;
        public RelativeLayout video_info;

        public MyViewHolder(View view) {
            super(view);
            media_image = (ImageView) view.findViewById(R.id.media_image);
            check_image = (ImageView) view.findViewById(R.id.check_image);
            mask_view = view.findViewById(R.id.mask_view);
            check_view = view.findViewById(R.id.check_view);
            video_info = (RelativeLayout) view.findViewById(R.id.video_info);
            gif_info = (RelativeLayout) view.findViewById(R.id.gif_info);
            textView = (TextView) view.findViewById(R.id.textView_size);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }

    int getItemWidth() {
        return (ScreenUtils.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_view_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (bZoomImage) {
            String sImageUrl = urList.get(position);
            String mimeType = mimeTypeList.get(position);
            if (mimeType.equals("video")) {
                String sVideoUrl = downloadVideo(sImageUrl);
                Uri uri = Uri.parse("file://" + sVideoUrl);
                holder.gif_info.setVisibility(View.INVISIBLE);
                holder.video_info.setVisibility(View.VISIBLE);
                holder.check_image.setVisibility(View.GONE);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, uri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                holder.textView.setText(fileUtils.getVideoDuration(Long.parseLong(time)));

                Glide.with(context)
                        .load(uri)
                        .into(holder.media_image);
            } else if (mimeType.contains("gif")) {
                if (sImageUrl.startsWith("mxc://")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    sImageUrl = pref.getString("KEY_MATRIX_URL", "") + "/_matrix/media/r0/download/" + sImageUrl.replace("mxc://", "");
                }
                Glide.with(context)
                        .load(sImageUrl)
                        .into(holder.media_image);
                holder.video_info.setVisibility(View.INVISIBLE);
                holder.gif_info.setVisibility(View.VISIBLE);
                holder.check_image.setVisibility(View.GONE);
            } else {
                if (sImageUrl.startsWith("mxc://")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    sImageUrl = pref.getString("KEY_MATRIX_URL", "") + "/_matrix/media/r0/download/" + sImageUrl.replace("mxc://", "");
                }
                Glide.with(context)
                        .load(sImageUrl)
                        .into(holder.media_image);
                holder.gif_info.setVisibility(View.INVISIBLE);
                holder.video_info.setVisibility(View.INVISIBLE);
                holder.check_image.setVisibility(View.GONE);
            }
            holder.media_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, null, selectMedias, position, urList);
                }
            });
        } else {
            final Media media = medias.get(position);
            Uri mediaUri = Uri.parse("file://" + media.path);

            Glide.with(context)
                    .load(mediaUri)
                    .into(holder.media_image);
            if (media.mediaType == 3) {
                holder.gif_info.setVisibility(View.INVISIBLE);
                holder.video_info.setVisibility(View.VISIBLE);

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, mediaUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                holder.textView.setText(fileUtils.getVideoDuration(Long.parseLong(time)));
                //holder.textView.setText(fileUtils.getSizeByUnit(media.size));
            } else {
                holder.video_info.setVisibility(View.INVISIBLE);
                holder.gif_info.setVisibility(".gif".equalsIgnoreCase(media.extension) ? View.VISIBLE : View.INVISIBLE);
            }

            int isSelect = isSelect(media);
            holder.mask_view.setVisibility(isSelect >= 0 ? View.VISIBLE : View.INVISIBLE);
            holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_selected) : ContextCompat.getDrawable(context, R.drawable.btn_unselected));
//        holder.check_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int isSelect = isSelect(media);
//                if (selectMedias.size() >= maxSelect && isSelect < 0) {
//                    Toast.makeText(context, context.getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
//                } else {
//                    if (media.size > maxSize) {
//                        Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxSize)), Toast.LENGTH_LONG).show();
//                    } else {
//                        holder.mask_view.setVisibility(isSelect >= 0 ? View.INVISIBLE : View.VISIBLE);
//                        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_unselected) : ContextCompat.getDrawable(context, R.drawable.btn_selected));
//                        setSelectMedias(media);
//                        mOnItemClickListener.onItemClick(position, false);
//                    }
//                }
//            }
//        });

            holder.media_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                mOnItemClickListener.onItemClick(position, true);
                    int isSelect = isSelect(media);
                    if (selectMedias.size() >= maxSelect && isSelect < 0) {
                        Toast.makeText(context, String.format(context.getString(R.string.msg_amount_limit), maxSelect), Toast.LENGTH_SHORT).show();
                    } else {
                        if (media.size > maxSize) {
                            Toast.makeText(context, context.getString(R.string.msg_size_limit), Toast.LENGTH_LONG).show();
                        } else {
                            holder.mask_view.setVisibility(isSelect >= 0 ? View.INVISIBLE : View.VISIBLE);
                            holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_unselected) : ContextCompat.getDrawable(context, R.drawable.btn_selected));
                            setSelectMedias(media);
                            mOnItemClickListener.onItemClick(v, media, selectMedias, position, null);
                        }
                    }

                }
            });
        }


    }

    private String downloadVideo(String sVideoURL) {
        String fileName = sVideoURL.substring(sVideoURL.lastIndexOf("/") + 1);
        if (fileName.isEmpty()) {
            fileName = "mxcFile" + ".mp4";
            String filePath = getApplicationFolder(context, "video") + "/" + fileName;
            return filePath;
        } else {
            fileName = fileName + ".mp4";
            String filePath = getApplicationFolder(context, "video") + "/" + fileName;
            return filePath;
        }
    }

    private String getApplicationFolder(Context context, String subFolder) {
        File file = new File(context.getExternalFilesDir("").getParentFile(), subFolder);
        if (file != null) {
            if (!file.exists())
                file.mkdirs();
            return file.getAbsolutePath();
        }
        return context.getExternalCacheDir().getAbsolutePath();
    }


    public void setSelectMedias(Media media) {
        int index = isSelect(media);
        if (index == -1) {
            selectMedias.add(media);
        } else {
            selectMedias.remove(index);
        }
    }

    /**
     * @param media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(Media media) {
        int is = -1;
        if (selectMedias.size() <= 0) {
            return is;
        }
        for (int i = 0; i < selectMedias.size(); i++) {
            Media m = selectMedias.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void updateSelectAdapter(ArrayList<Media> select) {
        if (select != null) {
            this.selectMedias = select;
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Media> list) {
        this.medias = list;
        notifyDataSetChanged();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ArrayList<Media> getSelectMedias() {
        return selectMedias;
    }

    public ArrayList<Media> getAllMedias() {
        return medias;
    }

    @Override
    public int getItemCount() {
        if (bZoomImage) {
            return urList.size();
        } else {
            return medias.size();
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Media data, ArrayList<Media> selectMedias, int position, ArrayList<String> urlList);
        //void onItemClick(int position, boolean bClickImage);
    }
}
