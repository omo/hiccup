package es.flakiness.hiccup;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;

@Module(
    injects = { TalkStore.class, TalkList.class, TalkStore.class, MainActivity.class }
)
public class AppModule {
    private App app;
    private DatabaseOpenHelper databaseOpenHelper;

    public AppModule(App app) {
        this.app = app;
        this.databaseOpenHelper = new DatabaseOpenHelper(app);
    }

    @Provides @Singleton
    DatabaseCompartment provideDatabaseCompartment() {
        return CupboardFactory.cupboard().withDatabase(databaseOpenHelper.getWritableDatabase());
    }

    @Provides @Singleton
    Bus provideBus() {
        return new Bus();
    }
}
