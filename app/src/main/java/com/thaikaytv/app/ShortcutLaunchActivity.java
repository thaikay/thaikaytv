package com.thaikaytv.app;

import android.app.Activity;
import android.os.Bundle;

public class ShortcutLaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chỉ dùng để nhận shortcut intent, thực tế sẽ mở PlayerActivity
        finish();
    }
}
