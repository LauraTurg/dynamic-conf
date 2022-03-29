package kz.lora.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Documented
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultLongValue {

  long value();

}
