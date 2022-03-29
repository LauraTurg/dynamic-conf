package kz.lora.conf.core.fields;

import java.util.List;

public interface FieldDefParser {

  List<FieldDef> parse(Class<?> aClass);

}
