package com.flowring.laleents.ui.model;


import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.flowring.laleents.ui.widget.dialog.DialogWait;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BaseFragment extends Fragment {
    protected DialogWait dialogWait;
    public Lock lock = new ReentrantLock();
    protected String[] BroadcastMessageIds = new String[0];
    protected BroadcastReceiver FireBaseMsgBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    protected void cancelWait() {
        runOnUiThread(() -> {
            if (dialogWait != null) {
                dialogWait.dismissDialog();
                dialogWait = null;
            }
        });

    }

    public Context NewGetContext() {
        lock.lock();
        Context context = getContext();
        if (context != null) {
            lock.unlock();
            return context;
        } else
            return AllData.context;
    }

    protected void showWait() {
        runOnUiThread(() -> {
            lock.lock();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                FragmentManager fm = activity.getSupportFragmentManager();
                if (fm != null) {
                    dialogWait = (DialogWait) fm.findFragmentByTag("DIALOG_UPLOAD_THEME_WAIT");
                    if (dialogWait == null) {
                        dialogWait = DialogWait.newInstanceForTimeout();
                        dialogWait.show(fm, "DIALOG_UPLOAD_THEME_WAIT");
                    }
                }
            }
            lock.unlock();
        });

    }

    public CallbackUtils.ActivityReturn activityReturn;
    public ActivityResultLauncher ActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (activityReturn != null)
                        activityReturn.Callback(result);
                }
            }
    );

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastControlCenter.unregisterReceiver(getContext(), FireBaseMsgBroadcastReceiver);
    }

    @Override
    public void onResume() {
        LocalBroadcastControlCenter.registerReceiver(getContext(), BroadcastMessageIds, FireBaseMsgBroadcastReceiver);
        super.onResume();
    }
}
