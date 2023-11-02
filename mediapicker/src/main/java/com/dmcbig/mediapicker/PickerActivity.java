package com.dmcbig.mediapicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.adapter.FolderAdapter;
import com.dmcbig.mediapicker.adapter.MediaGridAdapter;
import com.dmcbig.mediapicker.adapter.SpacingDecoration;
import com.dmcbig.mediapicker.data.DataCallback;
import com.dmcbig.mediapicker.data.ImageLoader;
import com.dmcbig.mediapicker.data.MediaLoader;
import com.dmcbig.mediapicker.data.VideoLoader;
import com.dmcbig.mediapicker.entity.Folder;
import com.dmcbig.mediapicker.entity.Media;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.dmcbig.mediapicker.widget.OnScrollListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class PickerActivity extends Activity
        implements DataCallback, View.OnClickListener {//, MediaGridAdapter.OnRecyclerViewItemClickListener {

    Intent argsIntent;
    RecyclerView recyclerView;
    Button done, category_btn, preview;
    MediaGridAdapter gridAdapter;
    ListPopupWindow mFolderPopupWindow;
    private FolderAdapter mFolderAdapter;
    private ArrayList<String> mimeTypeList;
    private ArrayList<String> urlList;
    private int n;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argsIntent = getIntent();


        setContentView(R.layout.main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        findViewById(R.id.btn_back).setOnClickListener(this);

        done = (Button) findViewById(R.id.done);
        category_btn = (Button) findViewById(R.id.category_btn);
        preview = (Button) findViewById(R.id.preview);
        done.setOnClickListener(this);

        //get view end

        if (argsIntent.hasExtra("bZoomImage")) {
            createZoomImageAdapter();
            preview.setVisibility(View.GONE);
            category_btn.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
//            createZoomImageFolderAdapter();
//            getMediaData();
        } else {
            maxSize = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
            setTitleBar();
            createAdapter();
            createFolderAdapter();
            getMediaData();
            category_btn.setOnClickListener(this);
            preview.setOnClickListener(this);
        }

    }

    private void createZoomImageAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.GridSpanCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        long maxSize = argsIntent.getLongExtra(PickerConfig.MAX_SELECT_SIZE, PickerConfig.DEFAULT_SELECTED_MAX_SIZE);
        //创建并设置Adapter
        urlList = argsIntent.getStringArrayListExtra("urlList");
        n = urlList.size() + 1;
        mimeTypeList = argsIntent.getStringArrayListExtra("mimeTypeList");
        boolean bZoomImage = argsIntent.getBooleanExtra("bZoomImage", false);
        gridAdapter = new MediaGridAdapter(null, this, null, urlList.size(), maxSize, urlList, bZoomImage, mimeTypeList);
        gridAdapter.setOnItemClickListener(new MediaGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Media data, ArrayList<Media> selectMedias, int position, ArrayList<String> urlList) {
                String ImageUrl = urlList.get(position);
                String mimeType = mimeTypeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("ImageUrl", ImageUrl);
                intent.putExtra("mimeType", mimeType);
                intent.putExtra("urlList", urlList);
                intent.putExtra("mimeTypeList", mimeTypeList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
//        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(gridAdapter, mLayoutManager);
//        mLayoutManager.setSpanSizeLookup(lookup);
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(gridAdapter);
        recyclerView.scrollToPosition(urlList.size() - 1);

        recyclerView.addOnScrollListener(new OnScrollListener() {
            boolean b = true;

            @Override
            public void onLoadMore() {
                Log.d("滾動", "URL現在是" + urlList.size());
                Log.d("滾動", "N現在是" + n);
                if (urlList.size() < n && !b) {
                    // Simulate get network data，delay 1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData();
                                    gridAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("滾動", "結束");
                    b = recyclerView.canScrollVertically(-1);
                    Log.d("滾動", "頂端" + !b);
                    if (!b) {
                        onLoadMore();
                        n = n + 4;
                        b = true;
                    }
                }

            }
        });
    }

    private void getData() {
        String a = "https://mtx.lale.im:8443/_matrix/media/r0/download/lalepass.lale.im/nlwiLOyNWmynbnoxCMPoCvaH";
        String b = "https://mtx.lale.im:8443/_matrix/media/r0/download/lalepass.lale.im/WpVTuNXVeHrlLLDwFgcGdJtL";
        String c = "https://mtx.lale.im:8443/_matrix/media/r0/download/lalepass.lale.im/aijlcBQAINsDRxdVnUGXLfSB";
        String d = "https://mtx.lale.im:8443/_matrix/media/r0/download/lalepass.lale.im/lwCNNScJKqNVRKIrmIVryrbn";
        urlList.add(0, a);
        urlList.add(1, b);
        urlList.add(2, c);
        urlList.add(3, d);
        mimeTypeList.add(0, "image/jpeg");
        mimeTypeList.add(1, "image/jpeg");
        mimeTypeList.add(2, "image/jpeg");
        mimeTypeList.add(3, "image/jpeg");
    }


    public void setTitleBar() {
        int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
        if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_title));
        } else if (type == PickerConfig.PICKER_IMAGE) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_image_title));
        } else if (type == PickerConfig.PICKER_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_video_title));
        }
    }

    void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.GridSpanCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        ArrayList<Media> medias = new ArrayList<>();
        ArrayList<Media> select = argsIntent.getParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST);
        int maxSelect = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        long maxSize = argsIntent.getLongExtra(PickerConfig.MAX_SELECT_SIZE, PickerConfig.DEFAULT_SELECTED_MAX_SIZE);
        gridAdapter = new MediaGridAdapter(medias, this, select, maxSelect, maxSize, null, false, null);
        //gridAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(gridAdapter);
    }

    void createFolderAdapter() {
        ArrayList<Folder> folders = new ArrayList<>();
        mFolderAdapter = new FolderAdapter(folders, this);
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setHeight((int) (ScreenUtils.getScreenHeight(this) * 0.6));
        mFolderPopupWindow.setAnchorView(findViewById(R.id.footer));
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setSelectIndex(position);
                category_btn.setText(mFolderAdapter.getItem(position).name);
                gridAdapter.updateAdapter(mFolderAdapter.getSelectMedias());
                mFolderPopupWindow.dismiss();
            }
        });
    }

    @AfterPermissionGranted(119)
    void getMediaData() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
            if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                getLoaderManager().initLoader(type, null, new MediaLoader(this, this));
            } else if (type == PickerConfig.PICKER_IMAGE) {
                getLoaderManager().initLoader(type, null, new ImageLoader(this, this));
            } else if (type == PickerConfig.PICKER_VIDEO) {
                getLoaderManager().initLoader(type, null, new VideoLoader(this, this));
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.READ_EXTERNAL_STORAGE), 119, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onData(ArrayList<Folder> list) {
        setView(list);
        category_btn.setText(list.get(0).name);
        mFolderAdapter.updateAdapter(list);
    }

    private int preIndex = -1;
    private int maxSize = -1;

    void setView(ArrayList<Folder> list) {
        gridAdapter.updateAdapter(list.get(0).getMedias());
        setButtonText();
        gridAdapter.setOnItemClickListener(new MediaGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Media data, ArrayList<Media> selectMedias, int position, ArrayList<String> urlList) {
                setButtonText();
            }
        });
    }

    void setButtonText() {
        String title = getString(R.string.done);
        if (argsIntent.hasExtra("selected_title")) {
            title = argsIntent.getStringExtra("selected_title");
        }

        int max = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        if (max == 1) {
            done.setText(title);
            preview.setText(getString(R.string.preview));
        } else {
            done.setText(title + "(" + gridAdapter.getSelectMedias().size() + "/" + max + ")");
            preview.setText(getString(R.string.preview) + "(" + gridAdapter.getSelectMedias().size() + ")");
        }
    }

//    private void previewImages(int position) {
//        Intent intent = new Intent(this, PreviewActivity.class);
//        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT));
//        intent.putExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getAllMedias());
//        intent.putExtra(PickerConfig.PRE_SELECT_LIST, gridAdapter.getSelectMedias());
//        intent.putExtra(PickerConfig.PRE_SELECT_INDEX, position);
//        this.startActivityForResult(intent, 200);
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            ArrayList<Media> selectMedias = new ArrayList<>();
            done(selectMedias);
        } else if (id == R.id.category_btn) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
            }
        } else if (id == R.id.done) {
            done(gridAdapter.getSelectMedias());
        } else if (id == R.id.preview) {
            if (gridAdapter.getSelectMedias().size() <= 0) {
                Toast.makeText(this, getString(R.string.select_null), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT));
            intent.putExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getSelectMedias());
            this.startActivityForResult(intent, 200);
//            int idx = gridAdapter.getAllMedias().indexOf(gridAdapter.getSelectMedias().get(0));
//            previewImages(idx);
        }
    }

    public void done(ArrayList<Media> selects) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selects);
        setResult(PickerConfig.RESULT_CODE, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ArrayList<Media> selectMedias = new ArrayList<>();
        done(selectMedias);
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            ArrayList<Media> selects = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            if (resultCode == PickerConfig.RESULT_UPDATE_CODE) {
                gridAdapter.updateSelectAdapter(selects);
                setButtonText();
            } else if (resultCode == PickerConfig.RESULT_CODE) {
                done(selects);
            }
        }
    }

//    @Override
//    public void onItemClick(int position, boolean bClickImage) {
//        setButtonText();
//        if (bClickImage) {
//            previewImages(position);
//        }
//    }
}
