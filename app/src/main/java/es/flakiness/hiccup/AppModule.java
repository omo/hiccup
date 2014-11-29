package es.flakiness.hiccup;

import dagger.Module;
import dagger.Provides;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;

@Module(
    injects = { TalkStore.class, TalkList.class, TalkStore.class }
)
public class AppModule {
    private App app;
    private DatabaseOpenHelper databaseOpenHelper;

    public AppModule(App app) {
        this.app = app;
        this.databaseOpenHelper = new DatabaseOpenHelper(app);
    }

    @Provides
    DatabaseCompartment provideDatabaseCompartment() {
        return CupboardFactory.cupboard().withDatabase(databaseOpenHelper.getWritableDatabase());
    }
}
