package fr.eseo.dis.joannomabeduneba.pfe_jury_app.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Jury {

    @PrimaryKey
    public final int juryId;

    public final Date date;

    public Jury(final int juryId, final Date date) {
        this.juryId = juryId;
        this.date = date;
    }
}
