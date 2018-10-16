package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

@Entity(tableName = "user_jury_join",
        primaryKeys = { "userId", "juryId"},
        indices = @Index(value = {"userId", "juryId"}),
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "uid",
                            childColumns = "userId"),
                @ForeignKey(entity = Jury.class,
                            parentColumns = "juryId",
                            childColumns = "juryId")
        })
public class UserJuryJoin {
    public final int userId;
    public final int juryId;

    public UserJuryJoin(final int userId, final int juryId) {
        this.userId = userId;
        this.juryId = juryId;
    }

}
