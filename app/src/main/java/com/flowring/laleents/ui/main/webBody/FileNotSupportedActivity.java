package com.flowring.laleents.ui.main.webBody;

import com.flowring.laleents.ui.model.MainAppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.download.DownloadUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FileNotSupportedActivity extends MainAppCompatActivity {

    Button shareButton;
    TextView fileNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_not_supported);
        initFileNameTextView();

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(view -> {
            if (PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
                String jsonString = getIntent().getStringExtra("JSONObject");
                judgmentFileName(jsonString);
            } else {
                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, null, "該功能需要下載權限");
            }
        });
    }

    private void initFileNameTextView(){
        String fileName = null;
        String jsonString = getIntent().getStringExtra("JSONObject");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            fileName = jsonObject.optString("fileName");
        }catch (JSONException e){
            e.printStackTrace();
        }
        fileNameTextView = findViewById(R.id.fileNameTextView);
        fileNameTextView.setText(fileName);
    }
    private void judgmentFileName(String jsonString){
        String oldFileName = null;
        String fileId = null;
        String url = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            oldFileName = jsonObject.optString("fileName");
            fileId = jsonObject.optString("fileId");
            url = jsonObject.optString("url");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if (oldFileName.isEmpty()){
            DownloadUtils.notSupportedFileInShare(url,null,fileId,FileNotSupportedActivity.this);
        } else {
            DownloadUtils.notSupportedFileInShare(url,oldFileName,fileId,FileNotSupportedActivity.this);
        }
    }

    public static void shareFileType(File folder, Context context){
        StringUtils.HaoLog("shareFileType 分享檔案");
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            if(folder.exists()){
                Uri cacheDirUri = FileProvider.getUriForFile(context, "com.flowring.laleents.fileprovider", folder);
                if(cacheDirUri != null){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM,cacheDirUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setType("application/*");
                    context.startActivity(Intent.createChooser(intent,"分享檔案"));
                } else {StringUtils.HaoLog("Uri 空值");}
            } else {StringUtils.HaoLog("檔案不存在");}
        } else {StringUtils.HaoLog("存儲設備不可用，無法進行讀寫操作");}
    }
}