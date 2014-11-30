package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class TalkPlayer {
    private final MediaPlayer player;
    private final Uri uri;

    public TalkPlayer(Context context, Uri uri) throws IOException {
        this.uri = uri;
        this.player = new MediaPlayer();
        this.player.setDataSource(context, uri);
        this.player.prepareAsync();
        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                TalkPlayer.this.onPlayerPrepared();
            }
        });
    }

    public void onPlayerPrepared() {
        player.start();
    }

    public void onHostClose() {
        player.stop();
        player.release();
    }
}
