/*
    SuShell

    <t0kt0ckus@gmail.com>
    (C) 2014

    License GPLv3
 */
package org.openmarl.tail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Daemon thread for our simplistic <code>tail</code>.
 *
 * <p>When the observed file is somewhat unavailable, {@link #getIoError()} will
 * answer an appropriate value.
 * </p>
 *
 * @author t0kt0ckus
 */
public class Taild extends Thread {

    private final File mFile;
    private final ArrayList<String> mLastReadLines = new ArrayList<String>();
    private final List<TailObserver> mObservers = new ArrayList<TailObserver>();

    private IOException mIoError;
    private boolean mInterrupted;

    /**
     * Creates a thread to monitor the given file.
     *
     * @param filePath The absolute file path.
     */
    public Taild(String filePath) {
        mFile = new File(filePath);
    }

    /**
     * Adds an observer for this <code>tail</code> events.
     *
     * @param observer An observer implementing {@link org.openmarl.tail.TailObserver}.
     *
     * @return The total numbers of observers.
     */
    public int addObserver(TailObserver observer) {
        mObservers.add(observer);
        return mObservers.size();
    }

    /**
     * Removes a no longer interested observer.
     *
     * @param observer The observer.
     *
     * @return The total numbers of observers.
     */
    public int removeObserver(TailObserver observer) {
        mObservers.remove(observer);
        return mObservers.size();
    }

    /**
     * Tells whether this <code>tail</code> has exited due to an I/O error.
     *
     * @return An I/O related exception, or <code>null</code> if no error.
     */
    public IOException getIoError() {
        return mIoError;
    }

    @Override
    public void run() {
        super.run();
        mInterrupted = false;

        try  {
            FileInputStream is = new FileInputStream(mFile);
            InputStreamReader streamReader = new InputStreamReader(is, "UTF-8");
            BufferedReader buffReader = new BufferedReader(streamReader);

            String lastLine;
            while (! mInterrupted) {
                if ((lastLine = buffReader.readLine()) != null) {
                    mLastReadLines.add(lastLine);
                }
                else {
                    if (mLastReadLines.size() > 0) {
                        String[] lastLines = mLastReadLines.toArray(new String[]{});
                        for (TailObserver observer : mObservers) {
                            observer.onLinesRead(lastLines);
                        }
                        mLastReadLines.clear();
                    }
                    try {
                        Thread.sleep(500, 0); // 500 ms
                    }
                    catch (InterruptedException e) {
                        mInterrupted = true;
                    }
                }
            }
            buffReader.close();
            streamReader.close();
            is.close();
        }
        catch(IOException e) {
            mIoError = e;
        }
    }

    /**
     * Stops this <code>tail</code>.
     */
    public void shutdown() {
        mInterrupted = true;
    }

}
