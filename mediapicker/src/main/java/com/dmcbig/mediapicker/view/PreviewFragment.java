package com.dmcbig.mediapicker.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dmcbig.mediapicker.PreviewActivity;
import com.dmcbig.mediapicker.R;
import com.dmcbig.mediapicker.entity.Media;
import com.dmcbig.mediapicker.utils.FileUtils;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dmcBig on 2017/8/16.
 */

public class PreviewFragment extends Fragment {
    private PhotoView mPhotoView;
    private ImageView play_view;
    private VideoView view_video;
    private TextView cannot_read_image;
    private String sVideoUrl;
    private boolean isUrlNull;
    public Bitmap fbitmap;
    private Uri uri;
    private MediaController mediaController;
    private boolean bVideoIsBeingTouched = false;
    private Handler mHandler = new Handler();
    //   private PhotoViewAttacher mAttacher;i

    public static PreviewFragment newInstance(Media media, String label, String url) {
        PreviewFragment f = new PreviewFragment();
        Bundle b = new Bundle();
        b.putString("mimetype", label);
        b.putString("url", url);
        b.putParcelable("media", media);
        f.setArguments(b);
        return f;
    }

    public static PreviewFragment newInstance(Media media, String label, String url, String fileName) {
        PreviewFragment f = new PreviewFragment();
        Bundle b = new Bundle();
        b.putString("mimetype", label);
        b.putString("url", url);
        b.putString("fileName", fileName);
        b.putParcelable("media", media);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preview_fragment_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Media media = getArguments().getParcelable("media");

        cannot_read_image = (TextView) view.findViewById(R.id.cannot_read_image);
        play_view = (ImageView) view.findViewById(R.id.play_view);
        view_video = (VideoView) view.findViewById(R.id.view_video);
        mPhotoView = (PhotoView) view.findViewById(R.id.photoview);
        mPhotoView.setMaximumScale(5);
        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                PreviewActivity previewActivity = (PreviewActivity) getActivity();
                if (previewActivity == null) {
                    return;
                }
                previewActivity.setBarStatus();
            }
        });
        if (media == null) {
            String sImageUrl = getArguments().getString("url");
            String fileName = getArguments().getString("fileName");
            if ("null".equals(sImageUrl) || sImageUrl.isEmpty()) {
                cannot_read_image.setVisibility(View.VISIBLE);
                return;
            } else {
                cannot_read_image.setVisibility(View.GONE);
            }
            String mimetype = getArguments().getString("mimetype");
            if ("video".equals(mimetype)) {
                sVideoUrl = ((PreviewActivity) getActivity()).downloadVideo(sImageUrl);
                uri = Uri.parse(sVideoUrl);
                play_view.setVisibility(View.VISIBLE);
                play_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(uri, "video/*");
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        if (isIntentAvailable(getContext(), intent)) {
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(getContext(),getString(R.string.cant_play_video), Toast.LENGTH_SHORT).show();
//                        }
                        play_view.setVisibility(View.GONE);
                        mPhotoView.setVisibility(View.GONE);
                        view_video.setVisibility(View.VISIBLE);
//                        view_video.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                if (!bVideoIsBeingTouched) {
//                                    bVideoIsBeingTouched = true;
//                                    if (view_video.isPlaying()) {
//                                        ((PreviewActivity) getActivity()).setBarStatus();
//                                    }
//                                    mHandler.postDelayed(new Runnable() {
//                                        public void run() {
//                                            bVideoIsBeingTouched = false;
//                                        }
//                                    }, 100);
//                                }
//                                return true;
//                            }
//                        });
                        view_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                view_video.setVisibility(View.GONE);
                                play_view.setVisibility(View.VISIBLE);
                                mPhotoView.setVisibility(View.VISIBLE);
                            }
                        });
                        mediaController = new MediaController(getContext()) {
                            @Override
                            public void show() {
                                super.show();
                                if (getActivity() == null) {
                                    return;
                                }
                                ((PreviewActivity) getActivity()).setVideoBarStatus(false);
                            }

                            @Override
                            public void hide() {
                                super.hide();
                                if (getActivity() == null) {
                                    return;
                                }
                                ((PreviewActivity) getActivity()).setVideoBarStatus(true);
                            }
                        };
                        mediaController.setAnchorView(view_video);
                        startPlay(uri);
                    }
                });
                Glide.with(getActivity())
                        .load(sVideoUrl)
                        .into(mPhotoView);
            } else {
                if (sImageUrl.startsWith("mxc://")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    sImageUrl = pref.getString("KEY_MATRIX_URL", "") + "/_matrix/media/r0/download/" + sImageUrl.replace("mxc://", "");
                }
                //讀取資料夾圖片
                String filePath = FileUtils.getApplicationFolder(getContext(), "picture") +
                        "/" + fileName;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                if (bitmap == null) {
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(sImageUrl)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                    mPhotoView.setImageBitmap(bitmap);
                                }
                            });
                } else {
                    mPhotoView.setImageBitmap(bitmap);
                }
            }
        } else {
            setPlayView(media);
            Glide.with(getActivity())
                    .load(media.path)
                    .into(mPhotoView);
        }
    }

    private void startPlay(Uri uri) {
        view_video.setMediaController(mediaController);
        view_video.setVideoURI(uri);
        view_video.requestFocus();
        view_video.start();
        if (getActivity() == null) {
            return;
        }
        ((PreviewActivity) getActivity()).setVideoBarStatus(true);
    }

    void setPlayView(final Media media) {
        if (media.mediaType == 3) {
            play_view.setVisibility(View.VISIBLE);
            play_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(getUri(media.path), "video/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (isIntentAvailable(getContext(), intent)) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.cant_play_video), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    Uri getUri(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".dmc", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    /**
     * 检查是否有可以处理的程序
     *
     * @param context
     * @param intent
     * @return
     */
    private boolean isIntentAvailable(Context context, Intent intent) {
        List resolves = context.getPackageManager().queryIntentActivities(intent, 0);
        return resolves.size() > 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void stopVideo() {
        if (view_video == null) {
            return;
        }
        if (view_video.getVisibility() == View.VISIBLE) {
            view_video.pause();
            view_video.setVisibility(View.GONE);
            play_view.setVisibility(View.VISIBLE);
            mPhotoView.setVisibility(View.VISIBLE);
        }
    }
}