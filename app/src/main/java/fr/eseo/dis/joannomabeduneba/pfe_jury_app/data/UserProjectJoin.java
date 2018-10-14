package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = "user_project_join",
        primaryKeys = { "projectId", "userId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "uid",
                            childColumns = "userId"),
                @ForeignKey(entity = Project.class,
                            parentColumns = "projectId",
                            childColumns = "projectId")
        })
public class UserProjectJoin {
    public final int userId;
    public final int projectId;

    public UserProjectJoin(final int userId, final int projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }
}
