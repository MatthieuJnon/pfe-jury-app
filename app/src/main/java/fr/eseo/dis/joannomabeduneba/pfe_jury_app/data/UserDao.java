package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

    @Query("SELECT * FROM users WHERE isLogged = 1")
    User getLoggedUser();

    @Query("SELECT * FROM users WHERE name=:name")
    List<User> getUserFromName(String name);

    @Query("SELECT * FROM users WHERE forename=:forename AND lastname=:lastname")
    User getUserFromFullName(String forename, String lastname);

}
