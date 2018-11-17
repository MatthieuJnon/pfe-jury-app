package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "projects")
public class Project {

    @PrimaryKey(autoGenerate = true)
    private int projectId;

    @NonNull
    private String project;

    private int confidentiality;

    private String description;

    public final Integer juryId;

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
}
