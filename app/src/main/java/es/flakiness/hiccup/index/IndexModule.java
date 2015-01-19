package es.flakiness.hiccup.index;

import dagger.Module;
import es.flakiness.hiccup.AppModule;

@Module(
        injects = { TalkList.class, IndexActivity.class, IndexView.class },
        addsTo = AppModule.class
)
public class IndexModule {
}
