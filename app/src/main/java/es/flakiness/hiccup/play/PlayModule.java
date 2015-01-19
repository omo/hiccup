package es.flakiness.hiccup.play;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import es.flakiness.hiccup.AppModule;
import rx.subscriptions.CompositeSubscription;

@Module(
        injects = { PlayView.class },
        addsTo = AppModule.class
)
public class PlayModule {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final GestureInterpreter interpreter;
    private final Player player;
    private final PlayClockPreso preso;
    private Context context;
    private Uri uri;
    private int lastPosition;

    public PlayModule(Context context, Uri uri, int lastPosition) throws IOException {
        this.context = context;
        this.uri = uri;
        this.player = new Player(context, uri);
        this.subscriptions.add(this.player);
        this.interpreter = new GestureInterpreter(player, lastPosition);
        this.subscriptions.add(this.interpreter);
        this.preso = new PlayClockPreso(this.interpreter.getPlayingWithSeek());
        this.subscriptions.add(this.preso);
    }

    public void release() {
        this.subscriptions.unsubscribe();
    }

    @Provides public Uri provideUri() { return uri; }
    @Provides public PlayClockPreso providePreso() { return preso; }
    @Provides public Playing providePlaying() { return this.interpreter.getPlayingWithSeek(); }
    @Provides public GestureInterpreter provideInterpreter() {
        return interpreter;
    }
}
