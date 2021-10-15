package cz.upce.st67093.SP_BZAPR.jpss.database;

import java.io.Serializable;

public record Record (
        String title,
        String password,
        String accountName,
        String description
        //TODO: add more fields, for example expire date
        ) implements Serializable
{

}
