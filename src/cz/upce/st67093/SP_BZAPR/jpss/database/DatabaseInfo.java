package cz.upce.st67093.SP_BZAPR.jpss.database;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public record DatabaseInfo(
        String owner,
        String description,
        long date
) implements Serializable {

    public String dateAsString() {
        Date currDate = new Date(date * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(currDate);
    }

}
