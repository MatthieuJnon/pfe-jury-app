package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @NonNull
    private String name;

    private String password;

    @NonNull
    private String role;

    @NonNull
    private String forename;

    @NonNull
    private String lastname;

    private boolean isLogged;

    private String token;

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", forename='" + forename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", isLogged=" + isLogged +
                ", token='" + token + '\'' +
                '}';
    }

    public User(int uid,
                @NonNull String name,
                String password,
                @NonNull String role,
                @NonNull String forename,
                @NonNull String lastname,
                boolean isLogged,
                String token)
    {
        this.uid = uid;
        this.name = name;
        this.password = password;
        this.role = role;
        this.forename = forename;
        this.lastname = lastname;
        this.isLogged = isLogged;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUid() {
        return uid;
    }

    public void setUid( int uid) {
        this.uid = uid;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
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
