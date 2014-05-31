/*
 * Copyright (c) 2014 Jakob Hjelm 
 */
package komposten.utilities.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Calendar;

import javax.swing.JOptionPane;



/**
 * This class holds a static method for logging exceptions in the file "log.txt"
 * (which is created at the top of the classpath).
 * @version
 * <b>1.2.0</b> <br />
 * <ul>
 * <li>Added <code>logMsg(String)</code> and <code>logMsg(String, String)</code>.</li>
 * </ul>
 * <b>Older</b> <br />
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
   * The path to the log file utilised by this class.
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
	
	
	
	private Logger() {}
	
	
	
	private static String getProgramDir()
	{
    String path = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    
    try
    {
      path = URLDecoder.decode(path, "UTF-8");
    
//      if (path.endsWith(".jar"))
//      {
        path  = new File(path).getParent();
//      }
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
   * Logs the given <code>Exception</code> (which can be <code>null</code>) together with the time and message in 
   * the file specified by <code>Logger.FILEPATH</code>.
   * @param errorType - A <code>String</code> saying what kind of error occurred (e.g. "WRITE ERROR"). A set 
   * of standard messages can be found as constants in this class.
   * @param errorMsg  - The message to be displayed after the error type.
   * @param e         - An <code>Exception</code> (can be <code>null</code>), from which additional information of the error 
   * will be taken.
   * @param exceptionMsgOnly - If only the <code>Exception</code>'s message should be written, and not the 
   * entire stack trace.
   * @return True if the message was successfully logged, false otherwise.
   */
	@Deprecated
  public static boolean log(String errorType, String errorMsg, Exception e, boolean exceptionMsgOnly)
  {
    return log(errorType, "<unspecified>", errorMsg, e, exceptionMsgOnly);
  }
	
	
	
	/**
	 * Logs the given <code>Exception</code> (which can be <code>null</code>) together with the time and message in 
	 * the file specified by <code>Logger.FILEPATH</code>.
	 * @param errorType - A <code>String</code> saying what kind of error occurred (e.g. "WRITE ERROR"). A set 
	 * of standard messages can be found as constants in this class.
	 * @param className - The name of the class within which the error occurred (can be null or zero-length).
	 * @param errorMsg  - The message to be displayed after the error type.
	 * @param e         - An <code>Exception</code> (can be <code>null</code>), from which additional information of the error 
	 * will be taken.
	 * @param exceptionMsgOnly - If only the <code>Exception</code>'s message should be written, and not the 
	 * entire stack trace.
	 * @return True if the message was successfully logged, false otherwise.
	 */
	public static boolean log(String errorType, String className, String errorMsg, Exception e, boolean exceptionMsgOnly)
	{
		try
		{
			File log = new File(FILEPATH);
			
			if (!log.exists())
				if (!log.createNewFile())
				  JOptionPane.showMessageDialog(null, "Could not create the log file!", "File Creation Error", JOptionPane.ERROR_MESSAGE);
			
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
			logMsg.append(" " + errorMsg);
			
			if (e != null)
			{
				logMsg.append(newLine + "|-|" + e.toString());
				
				if (!exceptionMsgOnly)
				{
					for (StackTraceElement st : e.getStackTrace())
						logMsg.append(newLine + "|---->" + st.toString());
					
					while (e.getCause() != null)
					{
					  if ((e.getCause() instanceof Exception))
					  {
  					  e = (Exception) e.getCause();
  					  
  					  logMsg.append(newLine + "|-|" + CAUSED_BY);
  					  logMsg.append(newLine + "|-|" + e.toString());
  					  
  					  for (StackTraceElement st : e.getStackTrace())
  					    logMsg.append(newLine + "|---->" + st.toString());
					  }
					  else
					  {
              logMsg.append(newLine + "|-|" + CAUSED_BY);
              logMsg.append(newLine + "|-|" + e.getCause().toString());
              
              for (StackTraceElement st : e.getCause().getStackTrace())
                logMsg.append(newLine + "|----->" + st.toString());
              
              break;
					  }
					}
				}
				else
				{
				  while (e.getCause() != null)
				  {
				    e = (Exception) e.getCause();
				    logMsg.append(newLine);
  	        logMsg.append("|-|" + CAUSED_BY);
  	        logMsg.append(newLine);
            logMsg.append("|-|" + e.toString());
            logMsg.append("|");
				  }
				  
				  logMsg.append(newLine + "---->At: " + e.getStackTrace()[0]);
				}
			}
			
			logMsg.append(newLine);
			
			FileWriter writer = new FileWriter(log, true);
			writer.write(logMsg.toString());
			writer.flush();
			writer.close();
		}
		catch (IOException ioe)
		{
		  System.out.println("Logger encountered an exception while writing to \"" + FILEPATH + "\": ");
		  ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * Prints the specified message to the log file (see {@link #FILEPATH}).
	 * @param message The message to log.
	 * @return True if the message was logged, false otherwise.
	 */
	public static boolean logMsg(String message)
	{
	  return log("INFO", "", message, null, true);
	}
	
	
	
	/**
   * Prints the specified message to the log file (see {@link #FILEPATH}).
   * @param message The message to log.
   * @param label The label for the message (e.g. "INFO" or "WARNING").
   * @return True if the message was logged, false otherwise.
	 */
	public static boolean logMsg(String label, String message)
	{
	  return log(label, "", message, null, true);
	}
}