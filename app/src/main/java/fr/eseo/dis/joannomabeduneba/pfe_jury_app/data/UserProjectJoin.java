package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

@Entity(tableName = "user_project_join",
        primaryKeys = { "projectId", "userId"},
        indices = @Index(value = {"userId", "projectId"}),
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
    public final String annotation;
    public final boolean isSupervisor;
    public final boolean isStudent;
    public String note;

    public UserProjectJoin(final int userId,
                           final int projectId,
                           final String annotation,
                           final boolean isSupervisor,
                           final boolean isStudent,
                           final String note) {
        this.userId = userId;
        this.projectId = projectId;
        this.annotation = annotation;
        this.isSupervisor = isSupervisor;
        this.isStudent = isStudent;
        this.note = note;
    }
}
