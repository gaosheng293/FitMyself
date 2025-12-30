package com.example.fitmyself;

import android.text.TextUtils;

public class InputValidator {

    /**
     * 校验数字输入是否合法
     * 规则：
     * 1. 不能为空
     * 2. 必须是数字
     * 3. 必须大于 0 (根据你的需求，不能是0)
     *
     * @param text 输入框的文字
     * @return true=合法, false=不合法
     */
    public static boolean isValidNumber(String text) {
        // 1. 检查是否为空
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        try {
            // 2. 尝试转成数字
            float value = Float.parseFloat(text);
            // 3. 检查是否大于 0
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}