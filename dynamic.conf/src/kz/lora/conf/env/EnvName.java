package kz.lora.conf.env;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Documented
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvName {

  String value();

}
