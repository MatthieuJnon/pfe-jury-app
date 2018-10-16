package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JuryDao {

    @Query("SELECT * FROM jury")
    List<Jury> getAllJuries();

    @Query("SELECT * FROM jury WHERE juryId=:id")
    Jury getJury(int id);

    @Insert
    void insert(Jury... juries);

    @Update
    void update(Jury... juries);

    @Delete
    void delete(Jury... juries);

}
