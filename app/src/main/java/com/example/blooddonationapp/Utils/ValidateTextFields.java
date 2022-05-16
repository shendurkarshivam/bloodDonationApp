package com.example.blooddonationapp.Utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;

public class ValidateTextFields {

    public static boolean field(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError("Required");
            editText.requestFocus();
            return false;
        }
        return true;
    }
    public static ArrayList<String> cities = new ArrayList<>();

}
