package es.flakiness.hiccup.play;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import es.flakiness.hiccup.AppModule;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.talk.TalkStore;
import rx.subscriptions.CompositeSubscription;

@Module(
        injects = { PlayView.class, PlayActivity.class },
        addsTo = AppModule.class
)
public class PlayModule {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final GestureInterpreter interpreter;
    private final Player player;
    private final PlayProgressPreso preso;
    private final PlaySession session;

    public PlayModule(Context context, Uri uri, int lastPosition) throws IOException {
        this.player = new Player(context, uri);
        this.subscriptions.add(this.player);
        this.interpreter = new GestureInterpreter(player, lastPosition);
        this.subscriptions.add(this.interpreter);
        this.preso = new PlayProgressPreso(this.interpreter.getSeeker());
        this.subscriptions.add(this.preso);
        // This sucks :-(
        this.session = new PlaySession(uri, interpreter.getSeeker(), Injections.get(context.getApplicationContext(), TalkStore.class));
        this.subscriptions.add(this.session);
    }

    public void release() {
        this.subscriptions.unsubscribe();
    }

    @Provides public PlayProgressPreso providePreso() { return preso; }
    @Provides public Playing providePlaying() { return this.interpreter.getSeeker(); }
    @Provides public GestureInterpreter provideInterpreter() {
        return interpreter;
    }
}
