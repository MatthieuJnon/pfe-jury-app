package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserProjectJoinDao {

    @Insert
    void insert(UserProjectJoin userProjectJoin);

    @Query("SELECT * FROM users INNER JOIN user_project_join ON " +
            "users.uid=user_project_join.userId WHERE " +
            "user_project_join.projectId=:projectId")
    List<User> getUsersForProject(final int projectId);

    @Query("SELECT * FROM users INNER JOIN user_project_join ON " +
            "users.uid=user_project_join.userId WHERE " +
            "user_project_join.projectId=:projectId")
    List<User> getProjectsForUser(final int projectId);

    @Query("SELECT * FROM users INNER JOIN user_project_join ON " +
            "users.uid=user_project_join.userId WHERE " +
            "user_project_join.projectId=:projectId AND user_project_join.isStudent=1")
    List<User> getStudentsForProject(final int projectId);


    @Query("SELECT * FROM user_project_join WHERE userId=:uid AND projectId=:projectId")
    UserProjectJoin getUserProjectJoin(final int uid, final int projectId);

}
