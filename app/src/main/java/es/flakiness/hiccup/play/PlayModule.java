package es.flakiness.hiccup.play;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = { PlayView.class }
)
public class PlayModule {
    private final Player player;
    private Context context;
    private Uri uri;

    public PlayModule(Context context, Uri uri) throws IOException {
        this.context = context;
        this.uri = uri;
        this.player = new Player(context, uri);
    }

    @Provides
    public Player providePlayer() {
        return player;
    }
}
