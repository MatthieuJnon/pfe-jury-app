package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {
        User.class,
        Project.class,
        UserProjectJoin.class
}, version = 1)
public abstract class PFEDatabase extends RoomDatabase {

    private static final String DB_NAME = "PFEDatabase.db";
    private static volatile PFEDatabase instance;

    static synchronized PFEDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static PFEDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                PFEDatabase.class,
                DB_NAME).build();
    }

    public abstract UserProjectJoinDao getUserProjectJoinDao();
}
