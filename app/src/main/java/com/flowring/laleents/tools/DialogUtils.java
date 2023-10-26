package com.flowring.laleents.tools;


import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static com.flowring.laleents.tools.FileUtils.createImageFile;
import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;
import static com.flowring.laleents.ui.main.webBody.MainWebActivity.smartServerDialogLock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.flowring.laleents.R;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.room.RoomControlCenter;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.tools.phone.PermissionUtils;
import com.flowring.laleents.ui.widget.dialog.StringAdapter;
import com.flowring.laleents.ui.widget.jitsiMeet.WaitAnswerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogUtils {

    static public void hideCall(Context context, MessageInfo MessageInfo) {
        runOnUiThread(() -> {
            if (callDialog != null) {
                callDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MsgControlCenter.stopRing();
                    }
                });
                callDialog.dismiss();
                callDialog = null;
            }
        });
    }
    static AlertDialog callDialog;

    static public MessageInfo callMessageInfo = null;

    static public void showCall(Context context, MessageInfo messageInfo) {
        MsgControlCenter.playRing();
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isLock = keyguardManager != null && keyguardManager.inKeyguardRestrictedInputMode();
        if (isLock) {
            Intent intent = new Intent(context, WaitAnswerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("MessageInfo", messageInfo);
            context.startActivity(intent);
        } else {
            callMessageInfo = messageInfo;
            RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(callMessageInfo.room_id);
            UserControlCenter.getOrgtreeuserimage();
            if (roomMinInfo != null){
                showCallDialog(context, roomMinInfo);
            } else {
                RoomControlCenter.getRoom0(callMessageInfo.room_id, new CallbackUtils.APIReturn() {
                    @Override
                    public void Callback(boolean isok, String DataOrErrorMsg) {

                        showCallDialog(context, AllData.getRoomMinInfo(callMessageInfo.room_id));
                    }
                });
            }
        }
    }

    static boolean isCallOk = false;
    static void showCallDialog(Context context, RoomMinInfo roomMinInfo) {
        if (Settings.canDrawOverlays(context)) {
            runOnUiThread(() -> {
                if (callDialog != null) {
                    callDialog.dismiss();
                    callDialog = null;
                }
                isCallOk = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
                callDialog = builder.create();
                callDialog.setView(LayoutInflater.from(context).inflate(R.layout.dialog_call, null));

                callDialog.setCancelable(false);
                callDialog.setCanceledOnTouchOutside(false);
                //8.0系統加強後台管理，禁止在其他應用和窗口彈提醒彈窗，如果要彈，必須使用TYPE_APPLICATION_OVERLAY，否則彈不出


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    callDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
//                    callDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_INPUT_METHOD_DIALOG));

                } else {
                    callDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_PHONE));
                }
//                callDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                callDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//                callDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                callDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                callDialog.getWindow().setDimAmount(0);
                callDialog.getWindow().setFlags(FLAG_NOT_TOUCH_MODAL,FLAG_NOT_TOUCH_MODAL);

                Window window = callDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.TOP;
                callDialog.show();

                TextView title = callDialog.findViewById(R.id.title);
                StringUtils.HaoLog("title=" + title + " MessageInfo=" + callMessageInfo + "");
                if (AllData.context == null) {
                    AllData.context = context.getApplicationContext();
                }


                title.setText(roomMinInfo == null ? callMessageInfo.room_id : roomMinInfo.name);

                TextView text = callDialog.findViewById(R.id.text);
                if ((callMessageInfo.getCallRequest().type.equals("audio"))) {
                    text.setText("lale 企業語音...");
                } else {
                    text.setText("lale 企業視訊...");

                }
                ImageView call_light = callDialog.findViewById(R.id.call_light);
                if (AllData.context == null) {
                    AllData.context = context.getApplicationContext();
                }

                callDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        MsgControlCenter.stopRing();
                        if (!isCallOk){MsgControlCenter.sendRejectRequest(callMessageInfo.room_id, callMessageInfo.id);}
                    }
                });
                call_light.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCallOk = true;
                        callDialog.cancel();
                        boolean isGroup = false;
                        String roomName = "";
                        if (AllData.context != null) {
                            RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(callMessageInfo.room_id);
                            if (roomMinInfo != null) {
                                isGroup = roomMinInfo.isGroup();
                                roomName = roomMinInfo.name;
                            }

                        }
                        MsgControlCenter.sendApplyRequest(callMessageInfo.room_id, callMessageInfo.id);
                        ActivityUtils.gotoWebJitisiMeet(context, UserControlCenter.getUserMinInfo().displayName,
                                UserControlCenter.getUserMinInfo().userId,
                                UserControlCenter.getUserMinInfo().avatarThumbnailUrl,
                                UserControlCenter.getUserMinInfo().token, UserControlCenter.getUserMinInfo().externalServerSetting.mqttUrl,
                                UserControlCenter.getUserMinInfo().externalServerSetting.jitsiServerUrl, callMessageInfo.getCallRequest().type, callMessageInfo.id, callMessageInfo.room_id, roomName, isGroup
                        );
                    }
                });
                ImageView call_times = callDialog.findViewById(R.id.call_times);
                call_times.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callDialog.cancel();

                    }
                });
            });
        }
    }

    static public void showDialogMessage(Context context, String text) {
        runOnUiThread(()->{
            new AlertDialog.Builder(context)
                    .setMessage(text)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(true)
                    .create().show();
        });

    }

    static public void showDialogMessage(Context context, String title, String text) {
        runOnUiThread(()->{
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        });
    }

    static public AlertDialog showDialogMessage(Context context, String title, String text, CallbackUtils.noReturn callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.Callback();
                    }
                })
                .create();
        alertDialog.show();
        smartServerDialogLock = false;
        return alertDialog;
    }

    // 按鈕可調，按鈕有後續動作，按空白處不可關閉Dialog
    public static void showDialog(Context context, String title, List<String> buttons, List<CallbackUtils.noReturn> callbacks) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage(title);

            List<String> translatedButtons = new ArrayList<>();
            for (String button : buttons) {
                String translatedButton = translateButton(button);
                translatedButtons.add(translatedButton);
            }

            if(translatedButtons.size() > 0){
                builder.setPositiveButton(translatedButtons.get(0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(0).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 1){
                builder.setNegativeButton(translatedButtons.get(1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(1).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 2){
                builder.setNeutralButton(translatedButtons.get(2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(2).Callback();
                    }
                });
            }
            builder.setCancelable(false).create().show();
        });
    }

    public static void showDialog(Context context, String title, String text, List<String> buttons, List<CallbackUtils.noReturn> callbacks) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(text);

            List<String> translatedButtons = new ArrayList<>();
            for (String button : buttons) {
                String translatedButton = translateButton(button);
                translatedButtons.add(translatedButton);
            }

            if(translatedButtons.size() > 0){
                builder.setPositiveButton(translatedButtons.get(0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(0).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 1){
                builder.setNegativeButton(translatedButtons.get(1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(1).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 2){
                builder.setNeutralButton(translatedButtons.get(2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(2).Callback();
                    }
                });
            }
            builder.setCancelable(false).create().show();
        });
    }

    // 按鈕可調，按鈕有後續動作，按空白處可關閉Dialog
    public static void showDialogCancelable(Context context, String title, List<String> buttons, List<CallbackUtils.noReturn> callbacks) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage(title);

            List<String> translatedButtons = new ArrayList<>();
            for (String button : buttons) {
                String translatedButton = translateButton(button);
                translatedButtons.add(translatedButton);
            }

            if(translatedButtons.size() > 0){
                builder.setPositiveButton(translatedButtons.get(0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(0).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 1){
                builder.setNegativeButton(translatedButtons.get(1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(1).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 2){
                builder.setNeutralButton(translatedButtons.get(2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(2).Callback();
                    }
                });
            }
            builder.create().show();
        });
    }

    public static void showDialogCancelable(Context context, String title, String text, List<String> buttons, List<CallbackUtils.noReturn> callbacks) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(text);

            List<String> translatedButtons = new ArrayList<>();
            for (String button : buttons) {
                String translatedButton = translateButton(button);
                translatedButtons.add(translatedButton);
            }

            if(translatedButtons.size() > 0){
                builder.setPositiveButton(translatedButtons.get(0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(0).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 1){
                builder.setNegativeButton(translatedButtons.get(1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(1).Callback();
                    }
                });
            }

            if(translatedButtons.size() > 2){
                builder.setNeutralButton(translatedButtons.get(2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callbacks.get(2).Callback();
                    }
                });
            }
            builder.create().show();
        });
    }

    private static String translateButton(String button) {
        switch (button) {
            case "logout":
                return "登出";
            case "ok":
                return "確定";
            case "cancel":
                return "取消";
            case "closure":
                return "關閉";
            case "feedback":
                return "問題回報";
            default:
                return button;
        }
    }

    public static AlertDialog showOneDialog(Activity activity, String title, String negative, String positive, DialogInterface.OnClickListener positiveClick) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(title);
        alertDialog.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        alertDialog.setPositiveButton(positive, positiveClick);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getColor(R.color.colorPrimaryDark));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getColor(R.color.recordHintRed));
        return dialog;
    }

    static public AlertDialog EditDialog(Context context, String title, CallbackUtils.messageReturn c) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setView(LayoutInflater.from(context).inflate(R.layout.dialog_edit_2, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_edit_2);
        Button btnPositive = (Button) dialog.findViewById(R.id.cancel);
        Button btnNegative = (Button) dialog.findViewById(R.id.ok);
        final EditText etContent = (EditText) dialog.findViewById(R.id.edit_text);
        etContent.setHint(title);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String str = etContent.getText().toString();
                if (str.isEmpty()) {
                    etContent.setError("請輸入內容");
                } else {
                    dialog.dismiss();
                    c.Callback(etContent.getText().toString());
                }
            }
        });


        return dialog;

    }

    static public Dialog showDialogList(Context context, Map<String, View.OnClickListener> button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_dialog, null);
        RecyclerView content = v.findViewById(R.id.recyclerView);
        content.setLayoutManager(new LinearLayoutManager(context));
        String[] array = new String[button.size()];
        button.keySet().toArray(array);
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        content.setAdapter(new StringAdapter(context, array, new StringAdapter.OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(String albumIteam) {
                StringUtils.HaoLog("點了 " + albumIteam);
                dialog.dismiss();
                button.get(albumIteam).onClick(v);
            }
        }));
        return dialog;
    }

    static public Dialog showGetImage(Activity activity, CallbackUtils.ReturnData<File> callback) {
        Map<String, View.OnClickListener> list = new HashMap<>();
        list.put("拍照", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.checkPermission(activity, Manifest.permission.CAMERA)) {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;

                        try {
                            photoFile = createImageFile(activity);
                        } catch (Exception ex) {

                        }

                        if (photoFile != null) {
                            callback.Callback(true, "", photoFile);
                            Uri photoURI = FileProvider.getUriForFile(activity,
                                    "com.flowring.laleents.fileprovider", photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            activity.startActivityForResult(intent, DefinedUtils.REQUEST_CARD_CAMERA);
                        }
                    }
                } else {
                    PermissionUtils.requestPermission(activity, Manifest.permission.CAMERA, "該功能需要相機權限");
                }
            }
        });
        list.put("相簿", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1103);
                } else {
                    Intent intent = new Intent(activity, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                    long maxSize = 104857600L;//long long long long類型
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize);
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);
                    activity.startActivityForResult(intent, DefinedUtils.REQUEST_IMAGE_PICKER);
                }

            }
        });
        return DialogUtils.showDialogList(activity, list);


    }

    static public Dialog showGetImageOrDefault(Activity activity, String Default, CallbackUtils.ReturnData<File> callback, ActivityResultLauncher resultLauncher) {
        Map<String, View.OnClickListener> list = new HashMap<>();
        list.put("拍照", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.checkPermission(activity, Manifest.permission.CAMERA)) {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;

                        try {
                            photoFile = createImageFile(activity);
                        } catch (Exception ex) {

                        }

                        if (photoFile != null) {
                            callback.Callback(true, "拍照", photoFile);
                            Uri photoURI = FileProvider.getUriForFile(activity,
                                    "com.flowring.laleents.fileprovider", photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            resultLauncher.launch(intent);
                        }
                    }
                } else {
                    PermissionUtils.requestPermission(activity, Manifest.permission.CAMERA, "該功能需要相機權限");
                }
            }
        });
        list.put("相簿", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1103);
                } else {
                    Intent intent = new Intent(activity, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                    long maxSize = 104857600L;//long long long long類型
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize);
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);
                    callback.Callback(true, "相簿", null);
                    resultLauncher.launch(intent);

                }

            }
        });
        list.put(Default, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.Callback(true, "預設", null);

            }
        });
        return DialogUtils.showDialogList(activity, list);


    }

    public static void showSignOutDialog(Activity activity,CallbackUtils.TokenReturn tokenReturn){
        runOnUiThread(() -> {
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_account_logout);
            dialog.show();
            Button sureButton = dialog.findViewById(R.id.sureButton);
            sureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    tokenReturn.Callback();
                }
            });
        });
    }

    public static void showUpgradeDialog(Activity activity) {
        runOnUiThread(() -> {
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(activity)
                            .setMessage(activity.getString(R.string.update_app_text))
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                CloudUtils.iCloudUtils.gotoGooglePlay(activity);
                                activity.finish();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog, i) -> {
                                dialog.dismiss();
                                activity.finish();
                            })
                            .setCancelable(false);
            alertDialogBuilder.show();
        });
    }

}
