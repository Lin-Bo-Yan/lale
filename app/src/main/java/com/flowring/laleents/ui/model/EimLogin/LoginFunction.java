package com.flowring.laleents.ui.model.EimLogin;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.ui.main.webBody.EimLoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFunction {
    public EditText edit_url,edit_account,edit_password;
    public TextView txt_desc_password;
    public ImageView show_password;
    public boolean canSign = false;

    public static String values;

    public LoginFunction(Activity activity) {
        edit_url = activity.findViewById(R.id.edit_url);
        edit_account = activity.findViewById(R.id.edit_account);
        edit_password = activity.findViewById(R.id.edit_password);
        txt_desc_password = activity.findViewById(R.id.txt_desc_password);

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
                StringUtils.HaoLog("afterTextChanged:"+editable.toString());
                isValid(editable.toString());
                EimLoginActivity.checkSignBtn();
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

    private void isValid(final String value) {
        Pattern pattern;
        Matcher matcher;
        Pattern length;
        Pattern pwd_case;
        Matcher matcher_length;
        Matcher matcher_case;


        pattern = Pattern.compile(DefinedUtils.PASSWORD_PATTERN);
        length = Pattern.compile(DefinedUtils.PASSWORD_LENGTH);
        pwd_case = Pattern.compile(DefinedUtils.PASSWORD_CASE);
        matcher = pattern.matcher(value);
        matcher_length = length.matcher(value);
        matcher_case = pwd_case.matcher(value);
        if (matcher.matches()) {
            txt_desc_password.setVisibility(View.INVISIBLE);
            values = value;
            canSign = true;
        } else {
            canSign = false;
            if (matcher_case.matches() && !matcher_length.matches()) {
                txt_desc_password.setText("密碼長度至少需6碼");
                txt_desc_password.setVisibility(View.VISIBLE);
            } else if (matcher_case.matches()) {
                txt_desc_password.setText(R.string.desc_password_rule);
                txt_desc_password.setVisibility(View.VISIBLE);
            } else
                txt_desc_password.setVisibility(View.VISIBLE);
        }

    }
}
