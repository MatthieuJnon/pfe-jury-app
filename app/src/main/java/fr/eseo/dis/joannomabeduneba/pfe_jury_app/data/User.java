package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    private int uid;

    @NonNull
    private String name;

    @NonNull
    private String role;

    @NonNull
    private String forename;

    @NonNull
    private String lastname;

    public User(int uid,
                @NonNull String name,
                @NonNull String role,
                @NonNull String forename,
                @NonNull String lastname)
    {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.forename = forename;
        this.lastname = lastname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid( int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getRole() {
        return role;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    @NonNull
    public String getForename() {
        return forename;
    }

    public void setForename(@NonNull String forename) {
        this.forename = forename;
    }

    @NonNull
    public String getLastname() {
        return lastname;
    }

    public void setLastname(@NonNull String lastname) {
        this.lastname = lastname;
    }
}
