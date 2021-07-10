package com.cow.liucy.hdxm.libcommon.logger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {
    private static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);
    private static final String LOG_PREFIX = "my-log";
    private static final String FILE_SEP = System.getProperty("file.separator");
    public FileLoggingTree(Context context) {
         String logDirectory ="";// context.getFilesDir() + "/logs";

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null)
            logDirectory = context.getExternalCacheDir() + FILE_SEP + "logs" + FILE_SEP;
        else {
            logDirectory = context.getCacheDir() + FILE_SEP + "logs" + FILE_SEP;
        }

        configureLogger(logDirectory);
    }

    private void configureLogger(String logDirectory) {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender <>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setFile(logDirectory + "/" + LOG_PREFIX + "-latest.html");

        SizeAndTimeBasedFNATP<ILoggingEvent> fileNamingPolicy = new SizeAndTimeBasedFNATP <>();
        fileNamingPolicy.setContext(loggerContext);
        fileNamingPolicy.setMaxFileSize("30MB");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(logDirectory + "/" + LOG_PREFIX + ".%d{yyyy-MM-dd}.%i.html");
        rollingPolicy.setMaxHistory(100);
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(fileNamingPolicy);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.start();

        HTMLLayout htmlLayout = new HTMLLayout();
        htmlLayout.setContext(loggerContext);
        htmlLayout.setPattern("%d{HH:mm:ss.SSS}%level%thread%msg");
        htmlLayout.start();

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setLayout(htmlLayout);
        encoder.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
        root.addAppender(rollingFileAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(loggerContext);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE) {
            return;
        }

        String logMessage = tag + ": " + message;
        switch (priority) {
            case Log.DEBUG:
                mLogger.debug(logMessage);
                break;
            case Log.INFO:
                mLogger.info(logMessage);
                break;
            case Log.WARN:
                mLogger.warn(logMessage);
                break;
            case Log.ERROR:
                mLogger.error(logMessage);
                break;
        }
    }
}