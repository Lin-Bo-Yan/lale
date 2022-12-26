package com.dmcbig.mediapicker;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dmcbig.mediapicker.data.MessageInfo;
import com.dmcbig.mediapicker.data.MyEventBus;
import com.dmcbig.mediapicker.entity.Media;
import com.dmcbig.mediapicker.utils.FileUtils;
import com.dmcbig.mediapicker.view.PreviewFragment;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;

/**
 * Created by dmcBig on 2017/8/9.
 */


public class PreviewActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = PreviewActivity.class.getSimpleName();

    Button done, all;
    LinearLayout check_layout;
    ImageView check_image;
    ViewPager viewpager;
    TextView bar_title;
    View top, bottom, v_delete;
    ArrayList<Media> preRawList, selects;
    ImageButton btnDelete, btnForward, btnDownload;
    TextView txtSender, txtTime;
    LinearLayout functionLayout;

    private MessageInfo shareMessage = new MessageInfo();
    private ArrayList<MessageInfo> messagesList;
    private String roomID, userId;

    private String sImageUrl, sMimeType, senderName, messagetype, message;
    private Boolean bZoomImage;
    private ArrayList<String> idList;
    private ArrayList<String> refreshUrlList;
    private ArrayList<String> mimeTypeList;
    private ArrayList<String> refreshMimeTypeList;
    private ArrayList<MessageInfo> tempArray = new ArrayList<>();
    private ArrayList<String> tempIdArray = new ArrayList<>();
    private int pos;
    private Uri videoUri;
    private boolean isPress;//手指是否触摸屏幕
    private boolean isOpen;//是否打开下一个activity
    private boolean isOpenDelete;//是否打开delete
    private long exitTime = 0;

    //傳遞大數據
    private EventBus eventBus;
    private MyEventBus event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //註冊 Eventbus
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        setContentView(R.layout.preview_main);
        findViewById(R.id.btn_back).setOnClickListener(this);
        check_image = (ImageView) findViewById(R.id.check_image);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);
        bar_title = (TextView) findViewById(R.id.bar_title);
        txtSender = (TextView) findViewById(R.id.txt_sender);
        txtTime = (TextView) findViewById(R.id.txt_time);
        done = (Button) findViewById(R.id.done);
        all = (Button) findViewById(R.id.all);
        top = findViewById(R.id.top);
        functionLayout = (LinearLayout) findViewById(R.id.function_layout);
        bottom = findViewById(R.id.bottom);
        btnDelete = (ImageButton) findViewById(R.id.btn_delete);
        v_delete = (View) findViewById(R.id.v_delete);
        btnForward = (ImageButton) findViewById(R.id.btn_forward);
        btnDownload = (ImageButton) findViewById(R.id.btn_download);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        Intent intent = getIntent();
        bZoomImage = intent.getBooleanExtra("imagezoom", false);
        isOpenDelete = intent.getBooleanExtra("openDelete", false);
        isOpen = false;
        if (bZoomImage) {
            done.setVisibility(View.GONE);
            all.setVisibility(View.GONE);
            all.setOnClickListener(this);
            check_layout.setVisibility(View.GONE);
            if (isOpenDelete) {
                v_delete.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(this);
            } else {
                v_delete.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
            shareMessage = (MessageInfo) intent.getSerializableExtra("shareMessageInfo");
//            tempArray = (ArrayList<MessageInfo>) intent.getSerializableExtra("imageMsgList");
//            tempIdArray = (ArrayList<String>) intent.getSerializableExtra("imageIdList");

            sImageUrl = shareMessage.url;
            roomID = intent.getStringExtra("room_id");
            userId = intent.getStringExtra("userId");

            btnForward.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.VISIBLE);
            btnForward.setOnClickListener(this);
            btnDownload.setOnClickListener(this);


            messagesList = new ArrayList<>();
            idList = new ArrayList<>();
//            getMediaListFromServer(true, shareMessage.receiveTime);
//            getAllMediaListFromServer(shareMessage.receiveTime);
            getAllMediaListFromDB();
        } else {
            check_layout.setOnClickListener(this);
            done.setOnClickListener(this);
            preRawList = intent.getParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST);
            selects = new ArrayList<>();
            selects.addAll(preRawList);
            setView(preRawList);
        }
    }

    private void getAllMediaListFromDB() {
        for (int i = 0; i < tempArray.size(); i++) {
            if (tempArray.get(i)._id.equals(shareMessage._id)) {
                pos = i;
            }
        }

        //從收藏頁面來
        if (tempArray.size() == 0) {
            tempArray.add(shareMessage);
        }
        if (tempIdArray.size() == 0) {
            tempIdArray.add(shareMessage._id);
        }

        messagesList.addAll(0, tempArray);
        idList.addAll(0, tempIdArray);
        shareMessage = messagesList.get(pos);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setZoomView();
                isOpen = false;
                Log.d(TAG, "Bertest getMediaListFromServer setOpen false");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消註冊釋放資源
        eventBus.unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MyEventBus event) {
        Log.d(TAG, "eventbus message size :" + event.getMessageIdList().size());
        tempArray = event.getMessageList();
        tempIdArray = event.getMessageIdList();
    }

//    private void getMediaListFromServer(final boolean bLoadOlder, final long time) {
////        tempArray = new ArrayList<>();
////        tempIdArray = new ArrayList<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PreviewActivity.this);
//                String header = "Bearer " + pref.getString("KEY_JWT", "");
//                String http = pref.getString("KEY_LALE_URL", "");
//                String sURL = http + String.format("/local/media/newer?roomId=%s&limit=%d", roomID, 15);
//                if (bLoadOlder) {
//                    sURL = http + String.format("/local/media/older?roomId=%s&limit=%d", roomID, 15);
//                }
//                if (time != -1) {
//                    sURL = sURL + "&time=" + time;
//                }
//
//                try {
//                    Request request = new Request.Builder()
//                            .addHeader("Authorization", header)
//                            .url(sURL)
//                            .get()
//                            .build();
//
//                    OkHttpClient client = new OkHttpClient();
//                    Response response = client.newCall(request).execute();
//                    if (response.code() == 200) {
//                        String result = response.body().string();
//                        if (bLoadOlder) {
//                            Log.d(TAG, "Bertest getMediaListFromServer old JSON = " + result);
//                        } else {
//                            Log.d(TAG, "Bertest getMediaListFromServer new JSON = " + result);
//                        }
//                        JSONObject json = new JSONObject(result);
//                        if (json.optBoolean("success", false)) {
//                            JSONArray jsonData = json.getJSONArray("data");
//                            if (jsonData.length() == 0 && messagesList.size() != 0) {
//                                isOpen = false;
//                                Log.d(TAG, "Bertest getMediaListFromServer setOpen false");
//                                return;
//                            }
//
//                            for (int i = 0; i < jsonData.length(); i++) {
//                                JSONObject jsonMedia = jsonData.getJSONObject(i);
//                                MessageInfo msg = new MessageInfo();
//                                msg.fromJsonObject(jsonMedia);
//                                if (msg._id.equals(shareMessage._id)) {
//                                    continue;
//                                }
//                                msg.url = mxcToUrl(msg.url);
//                                tempArray.add(msg);
//                                tempIdArray.add(msg._id);
//                            }
//
//                            if (messagesList.size() == 0) {
//                                messagesList.addAll(0, tempArray);
//                                idList.addAll(0, tempIdArray);
//                                messagesList.add(shareMessage);
//                                idList.add(shareMessage._id);
//                                pos = messagesList.size() - 1;
//                            } else {
//                                if (bLoadOlder) {
//                                    pos = tempArray.size() - 1;
//                                    messagesList.addAll(0, tempArray);
//                                    idList.addAll(0, tempIdArray);
//                                    shareMessage = messagesList.get(pos);
//                                } else {
//                                    pos = messagesList.size();
//                                    messagesList.addAll(tempArray);
//                                    idList.addAll(tempIdArray);
//                                    shareMessage = messagesList.get(pos);
//                                }
//                            }
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setZoomView();
//                                    isOpen = false;
//                                    Log.d(TAG, "Bertest getMediaListFromServer setOpen false");
//                                }
//                            });
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }

    private void getAllMediaListFromServer(final long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                String url = String.format("/local/media?deviceId=%s&roomId=%s&userId=%s",
                        deviceID,
                        roomID,
                        userId);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PreviewActivity.this);
                String header = "Bearer " + pref.getString("KEY_JWT", "");
                String https = pref.getString("KEY_LALE_URL", "");
                String sURL = https + url;
                Log.d(TAG, "getAllMediaListFromServer(" + url + ")");
                try {
                    Request request = new Request.Builder()
                            .addHeader("Authorization", header)
                            .url(sURL)
                            .get()
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String result = response.body().string();
                        JSONObject json = new JSONObject(result);
                        if (json.optBoolean("success", false)) {
                            JSONArray jsonData = json.getJSONArray("data");
                            Log.d(TAG, "getAllMediaListFromServer JSON = " + jsonData);
                            if (jsonData.length() == 0 && messagesList.size() != 0) {
                                isOpen = false;
                                Log.d(TAG, "Bertest getAllMediaListFromServer setOpen false");
                                return;
                            }

//                            for (int i = 0; i < jsonData.length(); i++) {
//                                JSONObject jsonMedia = jsonData.getJSONObject(i);
//                                MessageInfo msg = new MessageInfo();
//                                msg.fromJsonObject(jsonMedia);
//                                msg.url = mxcToUrl(msg.url);
//                                tempArray.add(msg);
//                                tempIdArray.add(msg._id);
//                            }

                            for (int i = 0; i < tempArray.size(); i++) {
                                if (tempArray.get(i)._id.equals(shareMessage._id)) {
                                    pos = i;
                                }
                            }

                            messagesList.addAll(0, tempArray);
                            idList.addAll(0, tempIdArray);
                            shareMessage = messagesList.get(pos);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setZoomView();
                                    isOpen = false;
                                    Log.d(TAG, "Bertest getMediaListFromServer setOpen false");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
//        if (result == RESULT_OK) {
//            if (request == 0x12) {
//                if (data.hasExtra("ImageUrl")) {
//                    sImageUrl = data.getStringExtra("ImageUrl");
//                    sMimeType = data.getStringExtra("mimeType");
//                    refreshUrlList = data.getStringArrayListExtra("urlList");
//                    refreshMimeTypeList = data.getStringArrayListExtra("mimeTypeList");
//                    setZoomView();
//                }
//            }
//        }
    }

    private void setZoomView() {
        bar_title.setVisibility(View.GONE);
        txtSender.setVisibility(View.VISIBLE);
        txtTime.setVisibility(View.VISIBLE);
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

        txtSender.setText(shareMessage.senderName);
        txtTime.setText(getDateFormat(shareMessage.receiveTime));
        for (int i = 0; i < messagesList.size(); i++) {
            MessageInfo messageInfo = messagesList.get(i);
            String fileExtension = FileUtils.getImageExtension(messageInfo.mimeType);
            String fileName = String.format("%d%s", messageInfo.receiveTime, fileExtension);
            fragmentArrayList.add(PreviewFragment.newInstance(null,messageInfo.mimeType, messageInfo.url, fileName));
        }
        AdapterFragment adapterFragment = new AdapterFragment(getSupportFragmentManager(), fragmentArrayList);
        viewpager.setAdapter(adapterFragment);
        viewpager.addOnPageChangeListener(this);
        viewpager.setCurrentItem(pos, false);
    }

    private String getDateFormat(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return formatter.format(calendar.getTime());
    }

    void setView(ArrayList<Media> default_list) {
        setDoneView(default_list.size());
        functionLayout.setVisibility(View.GONE);
        bar_title.setText(1 + "/" + preRawList.size());
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        for (Media media : default_list) {
            fragmentArrayList.add(PreviewFragment.newInstance(media, "", ""));
        }
        AdapterFragment adapterFragment = new AdapterFragment(getSupportFragmentManager(), fragmentArrayList);
        viewpager.setAdapter(adapterFragment);
        viewpager.addOnPageChangeListener(this);
    }

    void setDoneView(int num1) {
        done.setText(getString(R.string.done) + "(" + num1 + "/" + getIntent().getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT) + ")");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            if (bZoomImage) {
                finish();
            } else {
                done(selects, PickerConfig.RESULT_UPDATE_CODE);
            }
        } else if (id == R.id.done) {
            done(selects, PickerConfig.RESULT_CODE);
        } else if (id == R.id.check_layout) {
            Media media = preRawList.get(viewpager.getCurrentItem());
            int select = isSelect(media, selects);
            if (select < 0) {
                check_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_selected));
                selects.add(media);
            } else {
                check_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_unselected));
                selects.remove(select);
            }
            setDoneView(selects.size());
        } else if (id == R.id.btn_delete) {
            showConfirmDialog(shareMessage);

        } else if (id == R.id.btn_forward) {
            if (messagesList.size() == 0) {
                return;
            }
            String url = messagesList.get(pos).url;
            if (url.equals("null")) {
                return;
            }
            forwardImage();

        } else if (id == R.id.btn_download) {
            if (messagesList.size() == 0) {
                return;
            }
            String url = messagesList.get(pos).url;
            if (url.equals("null")) {
                return;
            }
            if (messagesList.get(pos).mimeType.equals("video")) {
                url = "file://" + downloadVideo(url);
                videoUri = Uri.parse(url);
            }
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                            String fileName = formatter.format(calendar.getTime());
                            Uri uri = saveBitmapToGallery(getApplicationContext(), fileName, bitmap, videoUri);
                            if (uri != null) {
                                Toast.makeText(getApplicationContext(), "已下載", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else if (id == R.id.all) {
            Intent intent = new Intent(PreviewActivity.this, PickerActivity.class);
//            intent.putExtra("urlList", urlList);
//            intent.putExtra("mimeTypeList", mimeTypeList);
            intent.putExtra("bZoomImage", bZoomImage);
            startActivityForResult(intent, 0x12);
        }
    }

    private void forwardImage() {
        Intent intent = new Intent();
        intent.putExtra("message", messagesList.get(pos).msg);
        intent.putExtra("messageType", messagesList.get(pos)._type);
        intent.putExtra("senderName", messagesList.get(pos).senderName);
        intent.putExtra("time", messagesList.get(pos).receiveTime);
        intent.putExtra("room_id", roomID);
        setResult(RESULT_OK, intent);
        PreviewActivity.this.finish();
    }

    private void showConfirmDialog(final MessageInfo message) {
        String text = "確定要刪除嗎？";
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(text)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent();
                            intent.putExtra("deleteMessage", message._id);
                            setResult(RESULT_OK, intent);
                            PreviewActivity.this.finish();
                        } catch (Exception e) {
                            Log.e("deleteCollect", e.toString());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
        Button btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPos.setTextColor(Color.RED);
    }

    private Uri saveBitmapToGallery(Context context, String fileName, Bitmap bitmap, Uri videoUri) {
        File mPicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Lale");
        OutputStream out = null;
        try {
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            if (videoUri != null) {
                InputStream inputStream = null;
                BufferedOutputStream bos = null;
                File filePicture = new File(mPicDir, fileName + ".mp4");
                if (filePicture.exists()) {
                    filePicture.delete();
                }
                String mPicPath = filePicture.getAbsolutePath();

                try {
                    int videosize = 9004720;
                    inputStream = resolver.openInputStream(videoUri);
                    bos = new BufferedOutputStream(new FileOutputStream(mPicPath, false));
                    byte[] b = new byte[videosize];
                    int i = 0;
                    while ((i = inputStream.read(b)) != -1) {
                        bos.write(b, 0, b.length);
                    }
                    values.put(MediaStore.Images.ImageColumns.DATA, mPicPath);
                    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName + ".mp4");
                    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "video/mp4");
                    //將圖片的拍攝時間設置為當前的時間
                    long current = System.currentTimeMillis();
                    values.put(MediaStore.Video.VideoColumns.DATE_ADDED, current);
                    values.put(MediaStore.Video.VideoColumns.DATE_MODIFIED, current);
                    values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, current);
                    values.put(MediaStore.Video.VideoColumns.DURATION, 4000);
                    values.put(MediaStore.Video.VideoColumns.SIZE, videosize);
                    values.put(MediaStore.Video.VideoColumns.WIDTH, 1920);
                    values.put(MediaStore.Video.VideoColumns.HEIGHT, 1080);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                        if (bos != null) bos.close();
                        Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                        if (uri != null) {
                            return uri;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream(1920 * 1920);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                long size = stream.size();
                stream.close();

                mPicDir.mkdirs();
                File filePicture = new File(mPicDir, fileName + ".jpg");
                if (filePicture.exists()) {
                    filePicture.delete();
                }

                String mPicPath = filePicture.getAbsolutePath();
                values.put(MediaStore.Images.ImageColumns.DATA, mPicPath);
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName + ".jpg");
                values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpg");
                //將圖片的拍攝時間設置為當前的時間
                long current = System.currentTimeMillis();
                values.put(MediaStore.Images.ImageColumns.DATE_ADDED, current);
                values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, current);
                values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, current);
                values.put(MediaStore.Images.ImageColumns.SIZE, size);
                values.put(MediaStore.Images.ImageColumns.WIDTH, bitmap.getWidth());
                values.put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.getHeight());
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    out = resolver.openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    return uri;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(Media media, ArrayList<Media> list) {
        int is = -1;
        if (list.size() <= 0) {
            return is;
        }
        for (int i = 0; i < list.size(); i++) {
            Media m = list.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void done(ArrayList<Media> list, int code) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, list);
        setResult(code, intent);
        finish();
    }

    public void setBarStatus() {
        if (top.getVisibility() == View.VISIBLE) {
            top.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
        } else {
            top.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.VISIBLE);
        }
    }

    public void setVideoBarStatus(boolean bHide) {
        if (top != null && bottom != null) {
            bottom.setVisibility(View.GONE);
            if (bHide) {
                top.setVisibility(View.GONE);
            } else {
                top.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        done(selects, PickerConfig.RESULT_UPDATE_CODE);
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (bZoomImage) {
            AdapterFragment adapter = ((AdapterFragment) viewpager.getAdapter());
            int maxPosition = adapter.getCount() - 1;

            if (isOpen) {
                Log.d(TAG, "Bertest gonPageScrolled isOpen");
            } else {
                Log.d(TAG, "Bertest gonPageScrolled noOpen");
            }
            if (position == maxPosition && isPress && positionOffsetPixels == 0 && !isOpen) {
                isOpen = true;
//                getMediaListFromServer(false, messagesList.get(maxPosition).receiveTime);
            } else if (!isOpen && position == 0 && isPress && positionOffsetPixels == 0) {
                if ((System.currentTimeMillis() - exitTime) < 100) {
                    return;
                }
                exitTime = System.currentTimeMillis();
                isOpen = true;
//                getMediaListFromServer(true, messagesList.get(0).receiveTime);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (bZoomImage) {
            pos = position;
            txtSender.setText(messagesList.get(pos).senderName);
            txtTime.setText(getDateFormat(messagesList.get(position).receiveTime));
        } else {
            bar_title.setText((position + 1) + "/" + preRawList.size());
            check_image.setImageDrawable(isSelect(preRawList.get(position), selects) < 0 ? ContextCompat.getDrawable(this, R.drawable.btn_unselected) : ContextCompat.getDrawable(this, R.drawable.btn_selected));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        AdapterFragment adapter = ((AdapterFragment) viewpager.getAdapter());
        if (adapter == null) {
            return;
        }
        if (state == SCROLL_STATE_DRAGGING) {
            isPress = true;
            Fragment curFragment = adapter.getItem(viewpager.getCurrentItem());
            if (curFragment instanceof PreviewFragment) {
                ((PreviewFragment) curFragment).stopVideo();
            }
        } else {
            isPress = false;
        }
    }

    public String downloadVideo(String sVideoURL) {
        String fileName = sVideoURL.substring(sVideoURL.lastIndexOf("/") + 1);
        if (fileName.isEmpty()) {
            fileName = "mxcFile" + ".mp4";
            String filePath = getApplicationFolder(this, "video") + "/" + fileName;
            return filePath;
        } else {
            fileName = fileName + ".mp4";
            String filePath = getApplicationFolder(this, "video") + "/" + fileName;
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

    private String mxcToUrl(String mxc) {
        if (mxc.startsWith("mxc://")) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            return pref.getString("KEY_MATRIX_URL", "") +
                    "/_matrix/media/r0/download/" + mxc.replace("mxc://", "");
        }
        return mxc;
    }


    public class AdapterFragment extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;

        public AdapterFragment(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }
}
