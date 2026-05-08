package com.thaikaytv.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import com.thaikaytv.app.models.Channel;
import java.util.Arrays;

public class ShortcutHelper {

    public static void addChannelShortcut(Context context, Channel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("channel_url", channel.getUrl());
            intent.putExtra("channel_name", channel.getName());
            intent.setData(android.net.Uri.parse("thaikaytv://play?id=" + channel.getId()));

            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "channel_" + channel.getId())
                .setShortLabel(channel.getName())
                .setLongLabel(channel.getName())
                .setIcon(Icon.createWithResource(context, R.drawable.ic_tv))
                .setIntent(intent)
                .build();

            if (shortcutManager != null) {
                try {
                    shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void removeChannelShortcut(Context context, Channel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            if (shortcutManager != null) {
                shortcutManager.removeDynamicShortcuts(Arrays.asList("channel_" + channel.getId()));
            }
        }
    }
}
