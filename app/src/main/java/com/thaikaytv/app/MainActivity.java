package com.thaikaytv.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thaikaytv.app.models.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView channelRecyclerView;
    private ChannelAdapter adapter;
    private List<Channel> channels = new ArrayList<>();
    private TextView emptyView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddM3u;
    private SharedPreferences prefs;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("thaikaytv_prefs", MODE_PRIVATE);

        channelRecyclerView = findViewById(R.id.channel_recycler);
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);
        fabAddM3u = findViewById(R.id.fab_add_m3u);

        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelAdapter(this, channels);
        channelRecyclerView.setAdapter(adapter);

        adapter.setOnChannelClickListener(channel -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("channels", new ArrayList<>(channels));
            intent.putExtra("channel_index", channels.indexOf(channel));
            startActivity(intent);
        });

        adapter.setOnChannelLongClickListener(channel -> {
            showShortcutDialog(channel);
        });

        fabAddM3u.setOnClickListener(v -> showAddM3uDialog());

        String savedUrl = prefs.getString("m3u_url", "");
        if (!savedUrl.isEmpty()) {
            loadM3U(savedUrl);
        }
    }

    private void showAddM3uDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_m3u, null);
        EditText urlInput = dialogView.findViewById(R.id.et_m3u_url);

        builder.setView(dialogView)
            .setTitle("Thêm link M3U")
            .setPositiveButton("Load", (dialog, which) -> {
                String url = urlInput.getText().toString().trim();
                if (!url.isEmpty()) {
                    prefs.edit().putString("m3u_url", url).apply();
                    loadM3U(url);
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void showShortcutDialog(Channel channel) {
        new AlertDialog.Builder(this)
            .setTitle("Thêm shortcut cho " + channel.getName())
            .setMessage("Bạn muốn thêm kênh này ra màn hình chính ATV?")
            .setPositiveButton("Thêm", (dialog, which) -> {
                ShortcutHelper.addChannelShortcut(this, channel);
                Toast.makeText(this, "Đã thêm shortcut: " + channel.getName(),
                    Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void loadM3U(String url) {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                List<Channel> result = M3UParser.parseFromUrl(url);
                mainHandler.post(() -> {
                    channels.clear();
                    channels.addAll(result);
                    adapter.updateChannels(channels);
                    progressBar.setVisibility(View.GONE);
                    if (channels.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this,
                        "Lỗi load M3U: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
