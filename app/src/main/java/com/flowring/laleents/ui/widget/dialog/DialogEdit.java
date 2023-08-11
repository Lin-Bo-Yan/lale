package com.flowring.laleents.ui.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.FixDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.flowring.laleents.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogEdit extends FixDialogFragment {

    public static final int EDIT_TYPE_NAME = 0;
    public static final int EDIT_TYPE_EMAIL = 1;
    public static final int EDIT_TYPE_PHONE = 2;
    private static final String TAG = DialogEdit.class.getSimpleName();
    private AlertDialog m_dialog;
    private View m_view;
    private EditText m_edtText;
    private String sTitle;
    private String sData;
    private int nMinLen;
    private int nMaxLen;
    private int nEditType;

    public DialogEdit() {
        //this.setRetainInstance(true);
    }

    static public DialogEdit newInstance(String title, String data, int minLen, int maxLen) {
        DialogEdit fragment = new DialogEdit();
        Bundle bundle = new Bundle();
        bundle.putString("sTitle", title);
        bundle.putString("sData", data);
        bundle.putInt("nMinLen", minLen);
        bundle.putInt("nMaxLen", maxLen);
        bundle.putInt("nEditType", EDIT_TYPE_NAME);
        fragment.setArguments(bundle);
        return fragment;
    }

    static public DialogEdit newInstance(String title, String data, int minLen, int maxLen, int type) {
        DialogEdit fragment = new DialogEdit();
        Bundle bundle = new Bundle();
        bundle.putString("sTitle", title);
        bundle.putString("sData", data);
        bundle.putInt("nMinLen", minLen);
        bundle.putInt("nMaxLen", maxLen);
        bundle.putInt("nEditType", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    static public DialogEdit newInstanceEmail(String title, String data) {
        DialogEdit fragment = new DialogEdit();
        Bundle bundle = new Bundle();
        bundle.putString("sTitle", title);
        bundle.putString("sData", data);
        bundle.putInt("nMinLen", 6);
        bundle.putInt("nMaxLen", 30);
        bundle.putInt("nEditType", EDIT_TYPE_EMAIL);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();

        sTitle = getArguments().getString("sTitle");
        sData = getArguments().getString("sData");
        nMinLen = getArguments().getInt("nMinLen");
        nMaxLen = getArguments().getInt("nMaxLen");
        nEditType = getArguments().getInt("nEditType");

        if (savedInstanceState != null) {
            sTitle = savedInstanceState.getString("sTitle");
            sData = savedInstanceState.getString("sData");
            nMinLen = savedInstanceState.getInt("nMinLen");
            nMaxLen = savedInstanceState.getInt("nMaxLen");
            nEditType = getArguments().getInt("nEditType");
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(sTitle)
                .setPositiveButton(context.getString(R.string.sure_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editDone();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editCancel();
                    }
                })
                .create();

        m_view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit, null);
        m_edtText = m_view.findViewById(R.id.edit_text);
        m_edtText.setText(sData);
        m_edtText.setSelection(sData.length());
        if (nEditType == EDIT_TYPE_EMAIL) {
            m_edtText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            m_edtText.setHint("請輸入電子郵件");
        }
        if (nEditType == EDIT_TYPE_PHONE) {
            m_edtText.setInputType(InputType.TYPE_CLASS_PHONE);
            m_edtText.setHint("請輸入手機號碼");
        }

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(nMaxLen);
        m_edtText.setFilters(filterArray);
        m_edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (nEditType == EDIT_TYPE_EMAIL) {
                    validEmail(charSequence.toString());
                } else if (nEditType == EDIT_TYPE_PHONE) {
                    validPhone(charSequence.toString());
                } else {
                    validField(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialog.setView(m_view);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                m_edtText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_edtText.requestFocus();
                        showKeyboard();
                    }
                }, 100);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    editCancel();
                    m_dialog.dismiss();
                }
                return false;
            }
        });

        m_dialog = dialog;
        m_dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Translucent;
        return dialog;
    }

    private void editDone() {
        String sText = m_edtText.getText().toString();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_edtText.getWindowToken(), 0);

        Fragment fragment = getParentFragment();
        if (fragment instanceof IDialogEdit) {
            ((IDialogEdit) fragment).onDialogEditDone(sText, nEditType);
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof IDialogEdit) {
            ((IDialogEdit) activity).onDialogEditDone(sText, nEditType);
            return;
        }
    }

    private void editCancel() {
        Fragment fragment = getParentFragment();
        if (fragment instanceof IDialogEdit) {
            ((IDialogEdit) fragment).onDialogEditCancel();
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof IDialogEdit) {
            ((IDialogEdit) activity).onDialogEditCancel();
            return;
        }
    }

    private void validField(String data) {
        if (m_dialog != null && nMinLen >= 0) {
            String sText = data.trim();
            m_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(sText.length() >= nMinLen);
        }
    }

    private void validEmail(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        m_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(matcher.matches());
    }

    private void validPhone(String sData) {
        String sText = sData.trim();
        m_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(sText.length() > 9);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sTitle", sTitle);
        outState.putString("sData", m_edtText.getText().toString());
        outState.putInt("nMinLen", nMinLen);
        outState.putInt("nMaxLen", nMaxLen);
        super.onSaveInstanceState(outState);
    }

    public void showDialog(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public String trim(String str) {
        int len = str.length();
        int st = 0;
        char[] val = str.toCharArray();
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return str.substring(st, len);
    }

    private void showKeyboard() {

        if (getActivity() == null) return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(m_edtText, InputMethodManager.SHOW_IMPLICIT);
        String sText = m_edtText.getText().toString();
        validField(m_edtText.getText().toString());
    }

    public void closeKeyboard() {
        if (getActivity() == null)
            return;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public interface IDialogEdit {
        void onDialogEditDone(String txtData, int type);

        void onDialogEditCancel();
    }


}
