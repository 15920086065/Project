/* 
 * Copyright(C)
 * @author yt
 */
package com.android.crlibrary;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {
	public static String TAG = "测试APP";

	private static final byte LOG_LEVEL_VERBOSE = 0;
	private static final byte LOG_LEVEL_DEBUG = 1;
	private static final byte LOG_LEVEL_INFO = 2;
	private static final byte LOG_LEVEL_WARN = 3;
	private static final byte LOG_LEVEL_ERROR = 4;
	private static final byte LOG_LEVEL_SILENT = 5;
	
	private static byte logLevel = LOG_LEVEL_VERBOSE;
	
	private static boolean bolWriteLog = false; /* if need write log to file */
	
	public static void setTAG(String tag){
		TAG = tag;
	}
    public Logger() {
    	System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

    public static int getLevel() {
		return logLevel;
    }
    /**
     * Send a VERBOSE log message.
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
    	if(LOG_LEVEL_VERBOSE >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.v(TAG, buildMessage(msg));
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("VERBOSE", msg));
    		}
    	}
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
    	if(LOG_LEVEL_VERBOSE >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.v(TAG, buildMessage(msg), thr);
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("VERBOSE", msg));
    		}
    	}
    }

    /**
     * Send a DEBUG log message.
     * @param msg
     */
    public static void d(String msg) {
    	if(LOG_LEVEL_DEBUG >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.d(TAG, buildMessage(msg));
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("DEBUG", msg));
    		}
    	}
    }

    /**
     * Send a DEBUG log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void d(String msg, Throwable thr) {
    	if(LOG_LEVEL_DEBUG >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.d(TAG, buildMessage(msg), thr);
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("DEBUG", msg));
    		}
    	}
    }

    /**
     * Send an INFO log message.
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
    	if(LOG_LEVEL_INFO >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.i(TAG, buildMessage(msg));
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("INFO", msg));
    		}
    	}
    }

    /**
     * Send a INFO log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
    	if(LOG_LEVEL_INFO >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.i(TAG, buildMessage(msg), thr);
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("INFO", msg));
    		}
    	}
    }

    /**
     * Send a WARN log message
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
    	if(LOG_LEVEL_WARN >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.w(TAG, buildMessage(msg));
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("WARN", msg));
    		}
    	}
    }

    /**
     * Send a WARN log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
    	if(LOG_LEVEL_WARN >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.w(TAG, buildMessage(msg), thr);
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("WARN", msg));
    		}
    	}
    }
    
    /**
     * Send an ERROR log message.
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
    	if(LOG_LEVEL_ERROR >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.e(TAG, buildMessage(msg));
    		}
    		else
    		{
    			writeLog(buildMessageForWrite("ERROR", msg));
    		}
    	}
    }
    
    /**
     * Send an ERROR log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
    	if(LOG_LEVEL_ERROR >= logLevel)
    	{
    		if(!bolWriteLog)
    		{
    			Log.e(TAG, buildMessage(msg), thr);
    		}else
    		{
    			writeLog(buildMessageForWrite("ERROR", msg));
    		}
    	}
    }

    /**
     * Building Message
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];

        return new StringBuilder()
        			.append("[")
//					.append(caller.getClassName() + ".")
					.append(caller.getMethodName())
					.append("(" + caller.getFileName() + ":" + caller.getLineNumber() + ")] ")
					.append(msg).toString();
    }
    
    /**
     * Building Message write to log file
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessageForWrite(String logLevel,String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");      
        Date curDate = new Date(System.currentTimeMillis());
        String timeStr = formatter.format(curDate);   
        return new StringBuilder()
        			.append(timeStr + " "+ logLevel + " " + TAG + "  [")
//					.append(caller.getClassName() + ".")
					.append(caller.getMethodName())
					.append("(" + caller.getFileName() + ":" + caller.getLineNumber() + ")] ")
					.append(msg).toString();
    }
    
    public static void writeLog(String logInfo){ 
        FileOutputStream fOut = null; 
        OutputStreamWriter osw = null; 
        
        try{
			File dlogFile = new File(Environment.getExternalStorageDirectory()+"/CR001/");
			Log.i("xx",""+dlogFile.toString()+" "+dlogFile.exists());
			if (!dlogFile.exists()){
				dlogFile.mkdir();
			}
			File logFile = new File(dlogFile,"log.txt");
			if (!logFile.exists()){
				logFile.createNewFile();
			}
        	
            osw = new OutputStreamWriter(fOut); 
            osw.write(logInfo+"\n"); 
            osw.flush();
        }catch (Exception e) {       
        	e.printStackTrace(); 
        } 
		finally { 
		   try {
			   osw.close(); 
			   fOut.close(); 
		   }catch (IOException e) { 
			   e.printStackTrace(); 
		   } 
		} 
	}
    
    public static void pullLogCatInfo(){
    	StringBuilder log = new StringBuilder();
        try {
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add( "logcat");
            commandLine.add( "-d");
            commandLine.add( "-v");
            commandLine.add( "time");
            commandLine.add( "-s");
            commandLine.add( TAG+":v");
//            commandLine.add( "-f");
//            commandLine.add( "/data/data/com.rockchip.ebookreader.activity/logcat.txt");
//            Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
            
            Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
            String line = bufferedReader.readLine();
            log.append("################################B E G I N################################\n");
            while ( line != null) {
                log.append(line+"\n");
                line = bufferedReader.readLine();
            }
            log.append("##################################E N D##################################\n\n\n");
            Runtime.getRuntime().exec("logcat -c"); 
            writeLog(log.toString());
        } catch ( IOException e) {
        }
    }
}
