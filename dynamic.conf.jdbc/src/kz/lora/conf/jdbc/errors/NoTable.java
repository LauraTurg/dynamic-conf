package kz.lora.conf.jdbc.errors;

import java.sql.SQLException;

public class NoTable extends RuntimeException {
  public NoTable(SQLException sqlException) {
    super(sqlException);
  }
}
