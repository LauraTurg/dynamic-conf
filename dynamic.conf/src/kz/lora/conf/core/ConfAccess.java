package kz.lora.conf.core;

import java.util.Date;

public interface ConfAccess {

  ConfContent load();

  void write(ConfContent confContent);

  Date lastModifiedAt();

}
