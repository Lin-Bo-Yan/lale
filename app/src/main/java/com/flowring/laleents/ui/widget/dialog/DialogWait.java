package com.flowring.laleents.ui.widget.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.FixDialogFragment;
import androidx.fragment.app.Fragment;

import com.flowring.laleents.R;

public class DialogWait extends FixDialogFragment {

    private Dialog m_dialog;
    private View m_view;
    private final Handler timeoutHandler = new Handler();
    private long startTime;
    private boolean enableTimeout = false;
    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            long spentTime = System.currentTimeMillis() - startTime;
            long mins = (spentTime / 1000) / 60;
            long seconds = (spentTime / 1000) % 60;
            if (mins == 1 && seconds > 60)
                timeoutReturn();
            else
                timeoutHandler.postDelayed(timeoutRunnable, 1000);
        }
    };

    public static DialogWait newInstanceForTimeout() {
        DialogWait dlg = new DialogWait();
        Bundle args = new Bundle();
        args.putString("message", "");
        args.putBoolean("enable_timeout", true);
        dlg.setArguments(args);
        dlg.setCancelable(false);
        return dlg;
    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

        }

        m_view = getActivity().getLayoutInflater().inflate(R.layout.dialog_wait, null);
        m_dialog = new Dialog(getActivity(), R.style.DialogWait);
        m_dialog.setTitle(null);
        m_dialog.setContentView(m_view);
        m_view.setAlpha(0.9f);

        //bernice 2016/08/24 timeout
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("enable_timeout")) {
            enableTimeout = getArguments().getBoolean("enable_timeout");
        }
        if (enableTimeout) {
            startTime = System.currentTimeMillis();
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutHandler.postDelayed(timeoutRunnable, 1000);
        }

        setCancelable(false);

        return m_dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void dismissDialog() {
        if (m_dialog != null && m_dialog.isShowing()){
            m_dialog.dismiss();
        }
        m_dialog = null;
        if (enableTimeout){
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }

    private void timeoutReturn() {
        timeoutHandler.removeCallbacks(timeoutRunnable);

        Fragment parent = getParentFragment();
        if (parent instanceof IDialogWait)
            ((IDialogWait) parent).OnDialogWaitTimeout();
    }

    public interface IDialogWait {
        void OnDialogWaitTimeout();
    }
}
