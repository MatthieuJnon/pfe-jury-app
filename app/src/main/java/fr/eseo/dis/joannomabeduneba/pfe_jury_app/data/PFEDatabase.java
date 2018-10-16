package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.Converters;

@Database(entities = {
        User.class,
        Project.class,
        UserProjectJoin.class,
        Jury.class,
        UserJuryJoin.class
}, version = 3)
@TypeConverters({Converters.class})
public abstract class PFEDatabase extends RoomDatabase {

    private static final String DB_NAME = "PFEDatabase.db";
    private static volatile PFEDatabase instance;

    public static synchronized PFEDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static PFEDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                PFEDatabase.class,
                DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract UserProjectJoinDao getUserProjectJoinDao();
    public abstract UserJuryJoinDao getUserJuryJoinDao();
    public abstract UserDao getUserDao();
    public abstract JuryDao getJuryDao();
    public abstract ProjectDao getProjectDao();
}
