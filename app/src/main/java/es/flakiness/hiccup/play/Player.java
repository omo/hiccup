package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class Player {
    private final MediaPlayer player;
    private final Uri uri;
    private boolean prepared;
    private boolean startRequested;

    public Player(Context context, Uri uri) throws IOException {
        this.uri = uri;
        this.player = new MediaPlayer();
        this.player.setDataSource(context, uri);
        this.player.prepareAsync();
        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Player.this.onPlayerPrepared();
            }
        });
    }

    public void onPlayerPrepared() {
        prepared = true;
        startIfNeededAndPossible();
    }

    public void start() {
        startRequested = true;
        startIfNeededAndPossible();
    }

    private void startIfNeededAndPossible() {
       if (startRequested && prepared)
           player.start();
    }

    public void onHostClose() {
        player.stop();
        player.release();
    }
}
