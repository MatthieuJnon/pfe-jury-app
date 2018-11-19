package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "jury")
public class Jury implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public final int juryId;

    public final Date date;

    public Jury(final int juryId, final Date date) {
        this.juryId = juryId;
        this.date = date;
    }

}
