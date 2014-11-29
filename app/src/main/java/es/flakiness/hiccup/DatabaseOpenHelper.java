package es.flakiness.hiccup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.qbusict.cupboard.CupboardFactory;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    static {
        CupboardFactory.cupboard().register(Talk.class);
    }

    public static int VERSION = 2;
    public static String NAME = "main";

    public DatabaseOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CupboardFactory.cupboard().withDatabase(sqLiteDatabase).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        CupboardFactory.cupboard().withDatabase(sqLiteDatabase).upgradeTables();
    }
}
