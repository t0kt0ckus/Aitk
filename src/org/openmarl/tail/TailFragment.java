/*
    SuShell

    <t0kt0ckus@gmail.com>
    (C) 2014

    License GPLv3
 */
package org.openmarl.tail;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmarl.aitk.R;

/**
 * Fragment to monitor a file like <code>tail</code> does.
 *
 * @author t0kt0ckus
 */
public class TailFragment extends Fragment implements TailObserver {

    private Taild mTaild;
    private TextView mTtyView;
    private String mTtyPath;

    private static final String KEY_TTY_PATH = "TAIL_TTY_PATH";


    /**
     * Required default constructor.
     */
    public TailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_tail, container, false);

        mTtyView = (TextView) rootView.findViewById(R.id.text_tty);
        mTtyView.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onLinesRead(final String[] lines)
    {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addTtyLines(lines);
                }
            });
        }
        else
            Log.w(TAG, "Ignored onLinesRead(), should not be an observer at this stage");
    }

    public boolean openTty(String filePath)
    {
        if (mTaild == null)
        {
            mTaild = new Taild(filePath);
            mTaild.addObserver(this);
            mTaild.start(); // starts taild thread

            if (mTaild.getIoError() != null)
            {
                Log.w(TAG, String.format("Failed to open tty (%s): %s",
                        filePath,
                        mTaild.getIoError().toString()));
                return false;
            }

            Log.i(TAG, String.format("Taild opened tty: %s", filePath));
        }
        else
            Log.w(TAG, "Ignored re-openTty()");

        return true;
    }

    public void closeTty()
    {
        if (mTaild != null) {
            mTaild.removeObserver(this);
            mTaild.shutdown();

            try {
                mTaild.join();
                Log.d(TAG, "Taild thread joined");
            }
            catch (InterruptedException e) {
                Log.w(TAG, "Failed to join Taild thread, expect zombie thread");
            }

            mTaild = null;
            Log.i(TAG, "Taild closed tty");
        }
    }

    void addTtyLines(final String[] lines) {
        for (String line : lines) {
            mTtyView.append(line);
            mTtyView.append("\n");
        }
        final int scroll = mTtyView.getLayout().getLineTop(mTtyView.getLineCount())
                - mTtyView.getHeight();
        if (scroll > 0)
            mTtyView.scrollTo(0, scroll);
    }

    private static final String TAG = TailFragment.class.getSimpleName();
}
