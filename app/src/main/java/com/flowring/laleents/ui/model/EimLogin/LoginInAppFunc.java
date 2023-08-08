package com.flowring.laleents.ui.model.EimLogin;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.ui.main.webBody.EimLoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginInAppFunc {
    public EditText edit_url;
    private EditText edit_account,edit_password;
    private ImageView show_password;
    public boolean canSign = false;
    private AppCompatTextView txt_warn_url;

    public static String passwordValid;
    public static String accountValid;
    public static String urlValid;

    public LoginInAppFunc(Activity activity) {
        edit_url = activity.findViewById(R.id.edit_url);
        edit_account = activity.findViewById(R.id.edit_account);
        edit_password = activity.findViewById(R.id.edit_password);
        txt_warn_url = activity.findViewById(R.id.txt_warn_url);

        edit_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty() && editable.toString() != null){
                    urlIsValid(editable.toString());
                } else {
                    urlIsValid("");
                }
            }
        });
        edit_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                accountIsValid(editable.toString());
            }
        });
        edit_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                StringUtils.HaoLog("beforeTextChanged:" + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                StringUtils.HaoLog("onTextChanged:"+charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordIsValid(editable.toString());
            }
        });

        show_password = activity.findViewById(R.id.img_show_password);
        show_password.setVisibility(View.VISIBLE);
        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!show_password.isSelected()) {
                    show_password.setSelected(true);
                    //密碼可見
                    edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    show_password.setSelected(false);
                    //密碼不可見
                    edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                edit_password.setSelection(edit_password.getText().length());
            }
        });
    }

    private void urlIsValid(String value){
        Pattern pattern = Pattern.compile(DefinedUtils.URL_RULE);
        Matcher matcher = pattern.matcher(value);
        if(matcher.matches()){
            txt_warn_url.setVisibility(View.INVISIBLE);
            urlValid = value;
        } else {
            if (value.length() > 0) {
                txt_warn_url.setVisibility(View.VISIBLE);
            } else {
                txt_warn_url.setVisibility(View.INVISIBLE);
            }
            urlValid = value;
        }
    }

    private void accountIsValid(String value){
        if(!value.isEmpty() && value != null){
            Pattern pattern = Pattern.compile(DefinedUtils.RULE);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                accountValid = value;
            } else {
                if (value.length() > 0) {
                    value = value.substring(0, value.length() - 1);
                    edit_account.setText(value);
                }
            }
        }
    }

    private void passwordIsValid(String value) {
        if(!value.isEmpty() && value != null){
            Pattern pattern = Pattern.compile(DefinedUtils.RULE);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                passwordValid = value;
                canSign = true;
            } else {
                canSign = false;
                if (value.length() > 0) {
                    value = value.substring(0, value.length() - 1);
                    edit_password.setText(value);
                }
            }
        }
    }
}
