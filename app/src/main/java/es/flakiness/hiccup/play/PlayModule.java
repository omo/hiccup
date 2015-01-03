package es.flakiness.hiccup.play;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import es.flakiness.hiccup.AppModule;

@Module(
        injects = { PlayView.class },
        addsTo = AppModule.class
)
public class PlayModule {
    private final GestureInterpreter interpreter;
    private Context context;
    private Uri uri;
    private int lastPosition;

    public PlayModule(Context context, Uri uri, int lastPosition) throws IOException {
        this.context = context;
        this.uri = uri;
        this.interpreter = new GestureInterpreter(context, uri, lastPosition);
    }

    @Provides
    public GestureInterpreter provideInterpreter() {
        return interpreter;
    }
}
