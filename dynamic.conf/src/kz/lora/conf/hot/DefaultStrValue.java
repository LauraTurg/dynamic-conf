package kz.lora.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@Documented
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultStrValue {

  String value();

}
