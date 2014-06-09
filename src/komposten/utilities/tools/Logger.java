/*
 * Copyright (c) 2014 Jakob Hjelm 
 */
package komposten.utilities.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.util.Calendar;

import javax.swing.JOptionPane;



/**
 * A utility class for logging messages and exceptions to either a file or a stream.
 * @see {@link LogUtils}
 * @author Jakob Hjelm
 * @version
 * <b>1.3.1</b> <br />
 * <ul>
 * <li>Replaced <code>Exception</code> with <code>Throwable</code>.</li>
 * <li>Added support for newlines in the exception message.</li>
 * <li>The first line of each cause is now always written.</li>
 * </ul>
 * <b>Older</b> <br />
 * 1.3.0 <br />
 * <ul>
 * <li>Removed <code>static</code> from all log methods.</li>
 * <li>Added support for changing log file, or to use a stream.</li>
 * <li>Added <code>write(String, String, OutputStream)</code>.</li>
 * <li>Added <code>writeToFile(String)</code> and <code>writeToStream(OutputStream)</code>.</li>
 * <li>Added <code>dispose()</code>.</li>
 * <li>Updated JavaDoc to match the new system.</li>
 * </ul>
 * 1.2.0 <br />
 * <ul>
 * <li>Added <code>logMsg(String)</code> and <code>logMsg(String, String)</code>.</li>
 * </ul>
 * 1.1.0 <br />
 * <ul>
 * <li>Added a <code>log()</code> method with a <code>className</code> parameter.</li>
 * <li>Marked old<code>log()</code> as deprecated.</li>
 * </ul>
 * 1.0.2 <br />
 * <ul>
 * <li>Improved exception handling.</li>
 * </ul>
 * 1.0.1 <br />
 * <ul>
 * <li>Removed a call to <code>log()</code> from the catch clause in <code>getProgramDir()</code>.</li>
 * </ul>
 * 1.0.0  <br />
 * <ul>
 * <li>Updated the look of the logged data.</li>
 * </ul>
 * 0.5.0  <br />
 * <ul>
 * <li>Created the class, the <code>log()</code> method and the error constants.</li>
 * <li>Added support for nested throwables.</li>
 * </ul>
 */
public final class Logger
{
  /**
   * The path to the default log file utilised by this class.
   */
	public static final String FILEPATH = getProgramDir() + File.separator + "log.txt";

	/** Error type constant. */
  public  static final String LOADERROR   = "LOAD ERROR";
  /** Error type constant. */
  public  static final String READERROR   = "READ ERROR";
  /** Error type constant. */
  public  static final String WRITEERROR  = "WRITE ERROR";
  /** Error type constant. */
  public  static final String CREATEERROR = "CREATION ERROR";
  private static final String CAUSED_BY   = "Caused by: ";
	
	private static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May",
						"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	private static String newLine = System.getProperty("line.separator");
	
	
	private String       filePath_;
	private OutputStream stream_;
	
	private static String getProgramDir()
	{
    String path = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    
    try
    {
      path = URLDecoder.decode(path, "UTF-8");
      path = new File(path).getParent();
    }
    catch (IOException e)
    {
      String msg = "An unexpected exception ocurred while reading the program's .jar-archive's path.";
      
      System.out.println("Logger - getProgramDir(): " + msg);
      System.out.println(e.getMessage());
      
      JOptionPane.showMessageDialog(null, msg + "\nThe logger may not work properly without this information!", "Load Error", JOptionPane.ERROR_MESSAGE);
    }
    
    return path;
	}
  
  
  /**
   * Creates a new Logger that writes to the default file (see {@link #FILEPATH}}.
   */
  public Logger()
  {
    this(FILEPATH);
  }
  
  /**
   * Creates a new Logger that writes to the specified file.
   * @param filePath The path to the file the Logger logs to.
   */
  public Logger(String filePath)
  {
    filePath_ = filePath;
  }
  
  /**
   * Creates a new Logger that writes to the specified stream.
   * <br /><b>Note:</b> The stream will not be closed until {@link #dispose()} is invoked,
   * or the stream is closed manually.
   * @param stream The stream to write to.
   */
  public Logger(OutputStream stream)
  {
    stream_ = stream;
  }
  
  
  
  /**
   * Sets this Logger to write to the file with the specified path.
   * <br /><b>Note:</b> If this Logger previously has been assigned an <code>OutputStream</code>,
   * that stream will not be closed! In order to close it, invoke {@link #dispose()} prior to
   * invoking this method.
   * @param path The new target file.
   */
  public void writeToFile(String path)
  {
    filePath_ = path;
    stream_   = null;
  }
  
  

  /**
   * Sets this Logger to write to the provided stream.
   * @param stream The new target stream.
   * <br /><b>Note:</b> If this Logger previously has been assigned an <code>OutputStream</code>,
   * that stream will not be closed! In order to close it, invoke {@link #dispose()} prior to
   * invoking this method.
   */
  public void writeToStream(OutputStream stream)
  {
    filePath_ = null;
    stream_   = stream;
  }
  
  
  
  /**
   * Logs the given <code>Exception</code> (which can be <code>null</code>) together with the time and message in 
   * the log file or stream.
   * @param errorType - A <code>String</code> saying what kind of error occurred (e.g. "WRITE ERROR"). A set 
   * of standard messages can be found as constants in this class.
   * @param errorMsg  - The message to be displayed after the error type  (may contain new lines).
   * @param t         - A <code>Throwable</code> from which additional information of the error 
   * will be taken. (May be <code>null</code>)
   * @param throwableMsgOnly - If only the <code>Throwable</code>'s message should be written, and not the 
   * entire stack trace.
   * @return True if the message was successfully logged, false otherwise.
   */
	@Deprecated
  public boolean log(String errorType, String errorMsg, Throwable t, boolean throwableMsgOnly)
  {
    return log(errorType, "<unspecified>", errorMsg, t, throwableMsgOnly);
  }
	
	
	
	/**
	 * Logs the given <code>Exception</code> (which can be <code>null</code>) together with the time and message in 
	 * the log file or stream.
	 * @param errorType - A <code>String</code> saying what kind of error occurred (e.g. "WRITE ERROR"). A set 
	 * of standard messages can be found as constants in this class.
	 * @param className - The name of the class within which the error occurred (can be null or zero-length).
	 * @param errorMsg  - The message to be displayed after the error type (may contain new lines).
	 * @param t         - A <code>Throwable</code> from which additional information of the error 
	 * will be taken. (May be <code>null</code>)
	 * @param throwableMsgOnly - If only the <code>Throwable</code>'s message should be written, and not the 
	 * entire stack trace.
	 * @return True if the message was successfully logged, false otherwise.
	 */
	public boolean log(String errorType, String className, String errorMsg, Throwable t, boolean throwableMsgOnly)
	{
		StringBuilder logMsg = new StringBuilder();
		Calendar      date   = Calendar.getInstance();

		String month  = MONTHS[date.get(Calendar.MONTH)];
		String day    = Integer.toString(date.get(Calendar.DAY_OF_MONTH));
		String year   = Integer.toString(date.get(Calendar.YEAR));
		String hour   = Integer.toString(date.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(date.get(Calendar.MINUTE));
		String second = Integer.toString(date.get(Calendar.SECOND));

		if (hour.length() == 1)
			hour = "0" + hour;
		if (minute.length() == 1)
			minute = "0" + minute;
		if (second.length() == 1)
			second = "0" + second;
		
		logMsg.append(newLine + "/=|" + month + " " + day +
				", " + year + " " + hour + ":" + minute + ":" + second);
		if (className != null && className.length() > 0)
		  logMsg.append(" - Class: " + className);
		logMsg.append("|");
		logMsg.append(newLine + "|-|" + errorType.toUpperCase() + ":");
		logMsg.append(" " + errorMsg.replaceAll("\n\r|\r\n|\r|\n|" + newLine + "", newLine + "|----|"));
		
		if (t != null)
		{
			logMsg.append(newLine + "|-|" + t.toString());
			
			if (!throwableMsgOnly)
			{
				for (StackTraceElement st : t.getStackTrace())
					logMsg.append(newLine + "|---->" + st.toString());
				
				while (t.getCause() != null)
				{
				  t = t.getCause();
				  
				  logMsg.append(newLine + "|-|" + CAUSED_BY);
				  logMsg.append(newLine + "|-|" + t.toString());
				  
				  for (StackTraceElement st : t.getStackTrace())
				    logMsg.append(newLine + "|---->" + st.toString());
				}
			}
			else
			{
        logMsg.append(newLine + "|---->" + t.getStackTrace()[0]);
        
			  while (t.getCause() != null)
			  {
			    t = t.getCause();
			    logMsg.append(newLine);
	        logMsg.append("|-|" + CAUSED_BY);
	        logMsg.append(newLine);
          logMsg.append("|-|" + t.toString());
          logMsg.append(newLine + "|---->" + t.getStackTrace()[0]);
			  }
			  
			}
		}
		
		logMsg.append(newLine);
		
		return write(logMsg.toString(), filePath_, stream_);
	}
	
	
	
	/**
	 * Prints the specified message to the log file or stream.
	 * @param message The message to log.
	 * @return True if the message was logged, false otherwise.
	 */
	public boolean logMsg(String message)
	{
	  return log("INFO", "", message, null, true);
	}
	
	
	
	/**
   * Prints the specified message to the log file or stream.
   * @param message The message to log.
   * @param label The label for the message (e.g. "INFO" or "WARNING").
   * @return True if the message was logged, false otherwise.
	 */
	public boolean logMsg(String label, String message)
	{
	  return log(label, "", message, null, true);
	}
	
	
	
	/**
	 * Writes a message to the file specified by <code>filePath</code> or to the stream
	 * if <code>filePath == null</code>.
	 * @param filePath The path to a file, or <code>null</code> if the stream should be used.
	 * @param stream A stream to write to, or <code>null</code> if <code>filePath</code> should be used.
	 * @return True if the data was successfully written to either a file or a stream, false
	 * otherwise.
	 */
	private boolean write(String data, String filePath, OutputStream stream)
	{
	  if (filePath != null)
	  {
      try
      {
        File log = new File(filePath);
        
        if (!log.exists())
          if (!log.createNewFile())
            System.err.println("Logger - write(): Could not create the log file!");
        
        FileWriter writer = new FileWriter(log, true);
        writer.write(data.toString());
        writer.flush();
        writer.close();
      }
      catch (IOException e)
      {
        System.out.println("Logger encountered an exception while writing to \"" + filePath + "\": ");
        e.printStackTrace();
        return false;
      }
      
      return true;
	  }
	  else if (stream != null)
	  {
	    PrintStream print = new PrintStream(stream);
	    
	    print.println(data);
	    print.flush();
	    
	    return true;
	  }
	  else
	    return false;
	}
	
	
	
	/**
	 * Closes this Logger's <code>OutputStream</code> if either {@link #Logger2(OutputStream)}
	 * or {@link #writeToStream(OutputStream)} has been used.
	 */
	public void dispose()
	{
	  try
	  {
  	  if (stream_ != null)
  	    stream_.close();
	  }
	  catch (IOException e)
	  {
      System.out.println("Logger encountered an exception while closing its stream: ");
      e.printStackTrace();
	  }
	}
}