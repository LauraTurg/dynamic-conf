package kz.lora.conf.jdbc.errors;

import java.sql.SQLException;

public class NoSchema extends RuntimeException {
  public NoSchema(SQLException sqlException) {
    super(sqlException);
  }
}
