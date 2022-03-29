package kz.lora.conf.hot;

import java.util.Date;

public interface ConfigStorage {

  String loadConfigContent(String configLocation) throws Exception;

  boolean isConfigContentExists(String configLocation) throws Exception;

  void saveConfigContent(String configLocation, String configContent) throws Exception;

  Date getLastChangedAt(String configLocation) throws Exception;

}
