package com.example.blooddonationapp.Utils;

import android.text.TextUtils;
import android.widget.EditText;

public class ValidateTextFields {

    public static boolean field(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError("Required");
            editText.requestFocus();
            return false;
        }
        return true;
    }

}
