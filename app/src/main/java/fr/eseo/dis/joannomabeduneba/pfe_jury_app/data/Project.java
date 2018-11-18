package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "projects")
public class Project implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int projectId;

    @NonNull
    private String project;

    private int confidentiality;

    private String description;

    private Integer juryId;

    public Project(int projectId,
                   @NonNull String project,
                   int confidentiality,
                   String description,
                   final int juryId)
    {
        this.projectId = projectId;
        this.project = project;
        this.confidentiality = confidentiality;
        this.description = description;
        this.juryId = juryId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @NonNull
    public String getProject() {
        return project;
    }

    public void setProject(@NonNull String project) {
        this.project = project;
    }

    public int getConfidentiality() {
        return confidentiality;
    }

    public void setConfidentiality(int confidentiality) {
        this.confidentiality = confidentiality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getJuryId() {
        return this.juryId;
    }

    public void setJuryId(Integer id) {
        this.juryId = id;
    }
}
