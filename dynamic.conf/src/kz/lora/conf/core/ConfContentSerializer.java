package kz.lora.conf.core;

public interface ConfContentSerializer {

  String serialize(ConfContent confContent);

  ConfContent deserialize(String text);

}
