package kz.lora.conf.hot;

import kz.lora.conf.core.ConfAccess;
import kz.lora.conf.core.ConfAccessStdSerializer;
import kz.lora.conf.core.ConfContent;
import kz.lora.conf.core.ConfContentSerializer;
import kz.lora.conf.core.ConfImplBuilder;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractConfigFactory {

  protected abstract ConfigStorage getConfigStorage();

  protected abstract <T> String configLocationFor(Class<T> configInterface);

  protected ConfContentSerializer getConfContentSerializer() {
    return new ConfAccessStdSerializer();
  }

  protected ConfAccess confAccess(Class<?> configInterface) {
    return new ConfAccess() {
      final String configLocation = configLocationFor(configInterface);

      @Override
      public ConfContent load() {
        try {
          return getConfContentSerializer().deserialize(getConfigStorage().loadConfigContent(configLocation));
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }

      @Override
      public void write(ConfContent confContent) {
        try {
          getConfigStorage().saveConfigContent(configLocation, getConfContentSerializer().serialize(confContent));
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }

      @Override
      public Date lastModifiedAt() {
        try {
          return getConfigStorage().getLastChangedAt(configLocation);
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }
    };

  }

  protected long autoResetTimeout() {
    return 3000;
  }

  private final ConcurrentHashMap<Class<?>, Object> proxyMap = new ConcurrentHashMap<>();

  public <T> T createConfig(Class<T> configInterface) {

    {
      //noinspection unchecked
      T ret = (T) proxyMap.get(configInterface);
      if (ret != null) return ret;
    }

    synchronized (proxyMap) {
      {
        //noinspection unchecked
        T ret = (T) proxyMap.get(configInterface);
        if (ret != null) return ret;
      }

      {
        T ret = ConfImplBuilder.confImplBuilder(configInterface, confAccess(configInterface))
                               .currentTimeMillis(this::currentTimeMillis)
                               .changeCheckTimeoutMs(autoResetTimeout())
                               .build();

        proxyMap.put(configInterface, ret);
        return ret;
      }
    }
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

}
