package kz.lora.conf;

import kz.lora.conf.error.NoValue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ConfData {

  private final Map<String, List<Object>> data = new HashMap<>();

  public Map<String, List<Object>> getData() {
    return data;
  }

  public void readFromFile(String fileName) {
    readFromFile(new File(fileName));
  }

  public void readFromFile(File file) {
    try {
      try (FileInputStream in = new FileInputStream(file)) {
        readFromStream(in);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void readFromByteArray(byte[] byteArray) {
    readFromStream(new ByteArrayInputStream(byteArray));
  }

  public void readFromStream(InputStream inputStream) {
    try {
      readFromStream0(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void readFromCharSequence(CharSequence charSequence) {
    readFromByteArray(charSequence.toString().getBytes(UTF_8));
  }

  @SuppressWarnings({"UnnecessaryLabelOnBreakStatement", "UnnecessaryLabelOnContinueStatement"})
  private void readFromStream0(InputStream inputStream) throws IOException {
    final LinkedList<Map<String, List<Object>>> stack = new LinkedList<>();
    stack.add(data);

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
      WHILE:
      while (true) {
        String line = br.readLine();
        if (line == null) {
          break WHILE;
        }
        line = line.replaceAll("^\\s+", "");
        if (line.startsWith("#")) {
          continue WHILE;
        }
        if (line.length() == 0) {
          continue WHILE;
        }
        String[] pair = parseToPair(line);
        if (pair == null) {
          continue WHILE;
        }
        if (pair.length != 2) {
          continue WHILE;
        }

        if ("{".equals(pair[1])) {
          Map<String, List<Object>> hash = new HashMap<>();
          addValue(stack.getLast(), pair[0], hash);
          stack.add(hash);
          continue WHILE;
        }

        if ("}".equals(pair[0])) {
          stack.removeLast();
          continue WHILE;
        }

        addValue(stack.getLast(), pair[0], pair[1]);
      }
    }
  }

  private static void addValue(Map<String, List<Object>> map, String key, Object value) {
    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
  }

  static String[] parseToPair(String line) {
    if (line == null) {
      return null;
    }
    {
      int idx1 = line.indexOf('=');
      int idx2 = line.indexOf(':');
      if (idx1 > -1 && (idx2 < 0 || idx1 < idx2)) {
        return new String[]{line.substring(0, idx1).trim(), line.substring(idx1 + 1).trim()};
      }
      if (idx2 > -1 && (idx1 < 0 || idx2 < idx1)) {
        return new String[]{line.substring(0, idx2).trim(), line.substring(idx2 + 1)};
      }
    }

    line = line.replaceAll("^\\s+", "");

    int idx = line.indexOf(' ');
    if (idx < 0) {
      String key = line.trim();
      if (key.length() == 0) return null;
      return new String[]{key, null};
    }

    {
      String value = line.substring(idx + 1).trim();
      if (value.length() == 0) value = null;
      return new String[]{line.substring(0, idx), value};
    }
  }

  public String strEx(String path) {
    String[]                  split    = path.split("/");
    Map<String, List<Object>> cur      = data;
    StringBuilder             prevPath = new StringBuilder();
    for (int i = 0, C = split.length - 1; i < C; i++) {
      String step = split[i];
      if (prevPath.length() > 0) {
        prevPath.append('/');
      }
      prevPath.append(step);
      cur = getMap(cur, new Name(step), prevPath);
      if (cur == null) {
        throw new NoValue(prevPath);
      }
    }
    return getStr(cur, new Name(split[split.length - 1]), prevPath);
  }

  public String str(String path, String defaultValue) {
    try {
      return strEx(path);
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  public int asInt(String path) {
    String str = str(path);
    if (str == null) return 0;
    return Integer.parseInt(str);
  }

  public int asInt(String path, int defaultValue) {
    String str = str(path);
    if (str == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException ignore) {
      return defaultValue;
    }
  }

  public int asIntEx(String path) {
    String str = strEx(path);
    return Integer.parseInt(str);
  }

  public long asLong(String path) {
    String str = str(path);
    if (str == null) {
      return 0;
    }
    return Long.parseLong(str);
  }

  public long asLong(String path, long defaultValue) {
    String str = str(path);
    if (str == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException ignore) {
      return defaultValue;
    }
  }

  public long asLongEx(String path) {
    String str = strEx(path);
    return Long.parseLong(str);
  }

  public boolean bool(String path) {
    return bool(path, false);
  }

  public boolean bool(String path, boolean defaultValue) {
    try {
      return boolEx(path);
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  @SuppressWarnings("RedundantIfStatement")
  public boolean boolEx(String path) {
    String str = strEx(path);
    if (str == null) return false;
    str = str.trim().toUpperCase();
    if (str.length() == 0) return false;
    {
      if (str.equals("1")) return true;

      if (str.equals("ИСТИНА")) return true;
      if (str.equals("TRUE")) return true;
      if (str.equals("YES")) return true;
      if (str.equals("ON")) return true;
      if (str.equals("ДА")) return true;
      if (str.equals("T")) return true;
      if (str.equals("Д")) return true;
      if (str.equals("Y")) return true;

      if (str.equals("真相")) return true;
    }
    return false;
  }

  public Date dateEx(String path) {
    return dateEx(path,
                  "yyyy-MM-dd'T'HH:mm:ss.SSS",
                  "yyyy-MM-dd'T'HH:mm:ss",
                  "yyyy-MM-dd'T'HH:mm",
                  "yyyy-MM-dd HH:mm:ss.SSS",
                  "yyyy-MM-dd HH:mm:ss",
                  "yyyy-MM-dd HH:mm",
                  "yyyy-MM-dd",
                  "dd/MM/yyyy HH:mm:ss.SSS",
                  "dd/MM/yyyy HH:mm:ss",
                  "dd/MM/yyyy HH:mm",
                  "dd/MM/yyyy",
                  "HH:mm:ss.SSS",
                  "HH:mm:ss",
                  "HH:mm"
    );
  }

  @SuppressWarnings("UnnecessaryContinue")
  public Date dateEx(String path, String... formats) {

    String strValue = strEx(path);
    if (strValue == null) {
      return null;
    }
    strValue = strValue.trim();
    if (strValue.startsWith("#")) {
      return null;
    }

    List<SimpleDateFormat> formatList = new LinkedList<>();
    for (String format : formats) {
      if (format == null) {
        continue;
      }
      for (String f : format.split(";")) {
        String trimmedFormat = f.trim();
        if (trimmedFormat.length() == 0) {
          continue;
        }
        formatList.add(new SimpleDateFormat(trimmedFormat));
      }
    }

    for (SimpleDateFormat sdf : formatList) {

      try {
        return sdf.parse(strValue);
      } catch (ParseException e) {
        continue;
      }

    }

    throw new NoValue(path);
  }

  public Date date(String path) {
    return date(path, (Date) null);
  }

  public Date date(String path, String... formats) {
    return date(path, null, formats);
  }

  @SuppressWarnings("RedundantIfStatement")
  public Date date(String path, Date defaultValue) {
    try {
      Date ret = dateEx(path);
      if (ret == null) return null;
      return ret;
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  public Date date(String path, Date defaultValue, String... formats) {
    try {
      Date ret = dateEx(path, formats);
      if (ret == null) {
        return defaultValue;
      }
      return ret;
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  public String str(String path) {
    try {
      return strEx(path);
    } catch (NoValue e) {
      return null;
    }
  }


  private static class Name {
    private static final Pattern END_DIGIT = Pattern.compile("^(.+)\\.(\\d+)$");

    public final int    index;
    public final String name;

    public Name(String bigName) {
      if (bigName == null) {
        index = 0;
        name  = null;
        return;
      }

      Matcher m = END_DIGIT.matcher(bigName);
      if (!m.matches()) {
        index = 0;
        name  = bigName;
        return;
      }

      index = Integer.parseInt(m.group(2));
      name  = m.group(1);
    }

    public String bigName() {
      if (index == 0) {
        return name;
      }
      return name + '.' + index;
    }

    @Override
    public String toString() {
      return "name = " + name + ", index = " + index;
    }
  }

  private String getStr(Map<String, List<Object>> map, Name name, StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) {
      throw new NoValue(prevPath, name.bigName());
    }

    int index = 0;
    for (Object object : list) {
      if (object instanceof String) {
        if (index == name.index) return (String) object;
        index++;
      }
    }

    {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new NoValue(prevPath);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Map<String, List<Object>> getMap(Map<String, List<Object>> map, Name name,
                                           StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) return null;

    int index = 0;
    for (Object object : list) {
      if (object instanceof Map) {
        if (index == name.index) return (Map) object;
        index++;
      }
    }

    if (prevPath != null) {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new NoValue("No such map index for " + prevPath);
    }
    return null;
  }

  public List<String> list(String path) {
    List<String>              ret = new ArrayList<>();
    Map<String, List<Object>> cur = data;
    if (path != null && path.length() > 0) {
      for (String name : path.split("/")) {
        cur = getMap(cur, new Name(name), null);
        if (cur == null) return ret;
      }
    }
    ret.addAll(cur.keySet());
    return ret;
  }

}
