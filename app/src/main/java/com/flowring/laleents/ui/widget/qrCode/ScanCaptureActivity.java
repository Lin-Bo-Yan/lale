package com.flowring.laleents.ui.widget.qrCode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.flowring.laleents.R;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.tools.phone.PermissionUtils;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ScanCaptureActivity extends MainAppCompatActivity {

    private static final String TAG = ScanCaptureActivity.class.getSimpleName();

    public enum ScanCaptureType {
        Json
    }

    private DecoratedBarcodeView barcodeScannerView;
    private boolean scanOnly = false;
    ScanCaptureType scanCaptureType = null;
    private final BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                handleDecode_switchJudge(result.getText());
            }
            StringUtils.HaoLog("barcodeResult = " + result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            StringUtils.HaoLog( "possibleResultPoints");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanCaptureType = (ScanCaptureType) getIntent().getExtras().get("ScanCaptureType");
        scanOnly = (boolean) getIntent().getBooleanExtra("scanOnly", false);
        setContentView(scanOnly ? R.layout.activity_scan_bind : R.layout.activity_scan_qrcode);
        initToolbar(getString(R.string.scan_qrcode));

        barcodeScannerView = findViewById(R.id.dbv_custom);

        ViewfinderView viewFinder = findViewById(com.google.zxing.client.android.R.id.zxing_viewfinder_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scan_frame);
        viewFinder.drawResultBitmap(bitmap);

        barcodeScannerView.decodeContinuous(barcodeCallback);
        barcodeScannerView.setStatusText("");

        if (!scanOnly) {
            LinearLayout btnQRcode = findViewById(R.id.btn_my_qrcode);
            btnQRcode.setVisibility(View.GONE);
            LinearLayout btnChooseQrcode = findViewById(R.id.btn_choose_qrcode);
            btnChooseQrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String read_external_storage_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                    if (PermissionUtils.checkPermission(ScanCaptureActivity.this, read_external_storage_permission)) {
                        Intent intent = new Intent(getApplicationContext(), PickerActivity.class);
                        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);
                        startActivityForResult(intent, DefinedUtils.REQUEST_IMAGE_PICKER);
                    } else {
                        PermissionUtils.requestPermission(ScanCaptureActivity.this, read_external_storage_permission, "拿取相簿中的QRcode圖片需要檔案存取權限");
                    }
                }
            });
        }
        String permission_camera = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permission_camera) != PackageManager.PERMISSION_GRANTED){
            PermissionUtils.requestPermission(this, permission_camera, "掃描QRcode功能需要相機權限");
        }

//        capture = new CaptureManager(this, barcodeScannerView);
//        capture.initializeFromIntent(getIntent(), savedInstanceState);
//        capture.decode();
    }

    private void initToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView txtTitle = findViewById(R.id.txt_toolbar_title);
        txtTitle.setText(title);

        ImageView img = findViewById(R.id.img_toolbar_left);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void handleDecode_switchJudge(String result) {
        barcodeScannerView.pause();
        switch (scanCaptureType){
            case Json:
                boolean isJson = false;
                try {
                    isJson = new JSONObject(result) != null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isJson) {
                    Intent resultIntent_switchJudge = new Intent();
                    resultIntent_switchJudge.putExtra("SCAN_QRCODE", result);
                    setResult(Activity.RESULT_OK, resultIntent_switchJudge);
                    finish();
                } else {
                    barcodeScannerView.resume();
                }
                break;
        }
    }

    private void Imagedecode(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        bitmap = null;

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Result scanResult = null;
        try {
            scanResult = reader.decode(bBitmap);
            if (scanResult == null) {
                showErrorHint();
                return;
            }
            handleDecode_switchJudge(scanResult.getText());

        } catch (Exception e) {
            e.printStackTrace();
            showErrorHint();
        }
    }

    private void showErrorHint() {
        Toast.makeText(getApplicationContext(), "無法讀取行動條碼，\n請選擇其他照片。 ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //barcodeScannerView.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        StringUtils.HaoLog(String.format("onActivityResult requestCode = %d, resultCode = %d", request, result));

        if (request == DefinedUtils.REQUEST_IMAGE_PICKER) {
            ArrayList<Media> images = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            if (images.size() < 1) {
                return;
            }

            Media image = images.get(0);
            if (image != null) {
                Imagedecode(image.path);
            }
        }
    }

}


/* 人豪寫的方法，他的用意是登入開啟的QR跑判斷 ScanCaptureType.Json這條，登入後開啟的QR跑 ScanCaptureType.Bind，但實驗發現兩個其實做一樣的事情
    private void handleDecode(String result) {
        barcodeScannerView.pause();
        if (scanCaptureType.equals(ScanCaptureType.Bind)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SCAN_QRCODE", result);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else if (scanCaptureType.equals(ScanCaptureType.Json)) {
            boolean isJson = false;
            try {
                isJson = new JSONObject(result) != null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isJson) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SCAN_QRCODE", result);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                barcodeScannerView.resume();
            }
        } else if (scanCaptureType.equals(ScanCaptureType.Bind)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SCAN_QRCODE", result);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }

    }
*/


/* 原本就已經註解掉了，不知有何用意，就先移下來
        if (!isBind) {
            ImageView imgRight = findViewById(R.id.img_toolbar_right);
            imgRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ScanCaptureActivity.this, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);
                    startActivityForResult(intent, DefinedUtils.REQUEST_IMAGE_PICKER);
                }
            });
        }
*/