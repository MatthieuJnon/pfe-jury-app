package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserProjectJoinDao {

    @Insert
    void insert(UserProjectJoin userProjectJoin);

    @Query("SELECT * FROM users INNER JOIN user_project_join ON " +
            "users.uid=user_project_join.userId WHERE " +
            "user_project_join.projectId=:projectId")
    List<User> getUsersForProject(final int projectId);

    @Query("SELECT * FROM projects INNER JOIN user_project_join ON " +
            "projects.projectId=user_project_join.projectId WHERE " +
            "user_project_join.userId=:uid")
    List<Project> getProjectsForUser(final int uid);

}
