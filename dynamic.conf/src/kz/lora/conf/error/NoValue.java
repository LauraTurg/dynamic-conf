package kz.lora.conf.error;

public class NoValue extends RuntimeException {

  public final String path;

  public NoValue(CharSequence path) {
    super("" + path);
    this.path = path == null ? null : path.toString();
  }

  public NoValue(StringBuilder prevPath, String name) {
    this(prevPath == null || prevPath.toString().trim().length() == 0 ? name : prevPath.toString() + '/' + name);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

}
