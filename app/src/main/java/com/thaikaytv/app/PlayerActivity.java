package com.thaikaytv.app;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.thaikaytv.app.models.Channel;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private TextView channelNameText;
    private ImageButton prevChannel, nextChannel, backButton;
    private List<Channel> allChannels;
    private int currentChannelIndex = -1;
    private Channel currentChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.player_view);
        channelNameText = findViewById(R.id.channel_name);
        prevChannel = findViewById(R.id.btn_prev_channel);
        nextChannel = findViewById(R.id.btn_next_channel);
        backButton = findViewById(R.id.btn_back);

        initializePlayer();

        if (getIntent().hasExtra("channels")) {
            allChannels = (List<Channel>) getIntent().getSerializableExtra("channels");
            currentChannelIndex = getIntent().getIntExtra("channel_index", 0);
            playChannel(currentChannelIndex);
        } else if (getIntent().hasExtra("channel_url")) {
            String url = getIntent().getStringExtra("channel_url");
            String name = getIntent().getStringExtra("channel_name");
            currentChannel = new Channel(name != null ? name : "Live", url);
            playUrl(url);
            channelNameText.setText(currentChannel.getName());
        }

        setupControls();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();
    }

    private void setupControls() {
        prevChannel.setOnClickListener(v -> {
            if (allChannels != null && currentChannelIndex > 0) {
                playChannel(currentChannelIndex - 1);
            }
        });

        nextChannel.setOnClickListener(v -> {
            if (allChannels != null && currentChannelIndex < allChannels.size() - 1) {
                playChannel(currentChannelIndex + 1);
            }
        });

        backButton.setOnClickListener(v -> finish());

        playerView.setControllerVisibilityListener(visibility -> {
            findViewById(R.id.controls_overlay).setVisibility(visibility);
        });
    }

    private void playChannel(int index) {
        if (allChannels != null && index >= 0 && index < allChannels.size()) {
            currentChannelIndex = index;
            currentChannel = allChannels.get(index);
            playUrl(currentChannel.getUrl());
            channelNameText.setText(currentChannel.getName());
        }
    }

    private void playUrl(String url) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP) {
            if (allChannels != null && currentChannelIndex < allChannels.size() - 1) {
                playChannel(currentChannelIndex + 1);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN) {
            if (allChannels != null && currentChannelIndex > 0) {
                playChannel(currentChannelIndex - 1);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (allChannels != null) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && currentChannelIndex > 0) {
                    playChannel(currentChannelIndex - 1);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && currentChannelIndex < allChannels.size() - 1) {
                    playChannel(currentChannelIndex + 1);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPip) {
        super.onPictureInPictureModeChanged(isInPip);
        if (isInPip) {
            findViewById(R.id.controls_overlay).setVisibility(View.GONE);
        }
    }
}
