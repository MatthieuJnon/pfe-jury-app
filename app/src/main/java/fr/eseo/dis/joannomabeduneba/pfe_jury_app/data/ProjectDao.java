package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM projects")
    List<Project> getAllProjects();

    @Query("SELECT * FROM projects WHERE projectId=:id")
    Project getProject(int id);

    @Query("SELECT * FROM projects WHERE project=:title")
    Project getProjectFromTitle(String title);

    @Insert
    void insert(Project... projects);

    @Update
    void update(Project... projects);

    @Delete
    void delete(Project... projects);

}
