package kz.lora.conf.hot;

import kz.lora.conf.ConfUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;

public abstract class FileConfigFactory extends AbstractConfigFactory {

  protected abstract Path getBaseDir();

  @Override
  protected <T> String configLocationFor(Class<T> configInterface) {
    ConfigFileName x = configInterface.getAnnotation(ConfigFileName.class);
    return (x != null ? x.value() : configInterface.getSimpleName()) + getConfigFileExt();
  }

  private File configStorageFile(String configLocation) {
    return getBaseDir().resolve(configLocation).toFile();
  }

  public File storageFileFor(Class<?> configInterface) {
    return configStorageFile(configLocationFor(configInterface));
  }

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  private final ConfigStorage configStorage = new ConfigStorage() {

    @Override
    public String loadConfigContent(String configLocation) {
      return ConfUtil.readFile(configStorageFile(configLocation));
    }

    @Override
    public boolean isConfigContentExists(String configLocation) {
      return configStorageFile(configLocation).exists();
    }

    @Override
    public void saveConfigContent(String configLocation, String configContent) {
      File file = configStorageFile(configLocation);
      //noinspection ResultOfMethodCallIgnored
      file.getParentFile().mkdirs();
      ConfUtil.writeFile(file, configContent);
    }

    @Override
    public Date getLastChangedAt(String configLocation) {
      File file = configStorageFile(configLocation);
      if (!file.exists()) return null;
      try {
        FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());
        return new Date(lastModifiedTime.toMillis());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  };

  @Override
  protected ConfigStorage getConfigStorage() {
    return configStorage;
  }

}
