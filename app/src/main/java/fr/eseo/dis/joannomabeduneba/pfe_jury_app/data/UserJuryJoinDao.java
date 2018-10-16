package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserJuryJoinDao {

    @Insert
    void insert(UserJuryJoin userJuryJoin);

    @Query("SELECT * FROM users INNER JOIN user_jury_join ON " +
            "users.uid=user_jury_join.userId WHERE " +
            "user_jury_join.juryId=:juryId")
    List<User> getUsersForJury(final int juryId);

    @Query("SELECT * FROM jury INNER JOIN user_jury_join ON " +
            "jury.juryId=user_jury_join.juryId WHERE " +
            "user_jury_join.userId=:uid")
    List<Jury> getJuriesForUser(final int uid);

}
