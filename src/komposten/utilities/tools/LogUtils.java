package komposten.utilities.tools;

import java.io.OutputStream;


/**
 * A convenience class to statically log messages using a {@link Logger}.
 * @author Komposten (Jakob Hjelm)
 */
public class LogUtils
{
  private static Logger logger_;
  
  static
  {
    logger_ = new Logger();
  }
  
  
  
  /**See {@link Logger#writeToFile(String)}.*/
  public static void writeToFile(String path)
  {
    logger_.writeToFile(path);
  }
  
  

  /**See {@link Logger#writeToStream(OutputStream)}.*/
  public static void writeToStream(OutputStream stream)
  {
    logger_.writeToStream(stream);
  }
  
  

  /**See {@link Logger#log(String, String, String, Exception, boolean)}.*/
  public static boolean log(String errorType, String className, String errorMsg, Exception e, boolean exceptionMsgOnly)
  {
    return logger_.log(errorType, className, errorMsg, e, exceptionMsgOnly);
  }
  
  

  /**See {@link Logger#logMsg(String)}.*/
  public static boolean logMsg(String message)
  {
    return logger_.logMsg(message);
  }
  
  

  /**See {@link Logger#logMsg(String, String)}.*/
  public static boolean logMsg(String label, String message)
  {
    return logger_.logMsg(label, message);
  }
}
