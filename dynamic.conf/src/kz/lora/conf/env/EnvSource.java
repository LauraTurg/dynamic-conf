package kz.lora.conf.env;

public interface EnvSource {

  String getValue(String name);

  EnvSource SYSTEM = System::getenv;

}
