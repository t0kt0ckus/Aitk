package org.openmarl.aitk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.openmarl.aitk.ui.InjectionLibraryChoiceFragment;
import org.openmarl.aitk.ui.ProcessChoiceFragment;
import org.openmarl.libaitk.AitkContext;
import org.openmarl.libaitk.AitkContextAsyncInit;
import org.openmarl.libaitk.AitkContextObserver;
import org.openmarl.susrv.SuShell;
import org.openmarl.susrv.SuShellInvalidatedException;
import org.openmarl.tail.TailFragment;


public class HomeActivity extends Activity implements AitkContextObserver, View.OnClickListener {

    private AitkContext mAitkContext;

    private ProcessChoiceFragment mProcessChoice;
    private InjectionLibraryChoiceFragment mLibraryChoice;

    private TailFragment mTailConsole;

    private Button mHijackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_home);

        mProcessChoice = (ProcessChoiceFragment)
                getFragmentManager().findFragmentById(R.id.fragment_process_choice);
        mLibraryChoice = (InjectionLibraryChoiceFragment)
                getFragmentManager().findFragmentById(R.id.fragment_injlib_choice);
        mTailConsole = (TailFragment)
                getFragmentManager().findFragmentById(R.id.fragment_tail);

        mHijackBtn = (Button) findViewById(R.id.btn_hijack);
        mHijackBtn.setOnClickListener(this);

        invalidateAitkContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContextReady(AitkContext ctx) {
        mAitkContext = ctx;

        if (mAitkContext == null) {
            Log.w(TAG, "Aitk context initialization failed, retry ...");
            invalidateAitkContext();
        }
        else {
            SuShell shell = SuShell.getInstance();
            mTailConsole.openTty(SuShell.getInstance().getTtyPath());
            mHijackBtn.setEnabled(true);
            Log.i(TAG, "Aitk context initialized");
        }
    }

    @Override
    public void onContextDisconnected() {
        Log.w(TAG, "Aitk context invalidated");
        invalidateAitkContext();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_hijack)
        {
            String procName = mProcessChoice.getSelectedProcess();
            String injectionLib = mLibraryChoice.getSelectedLibrary();
            AitkInjection injection = new AitkInjection(procName,injectionLib);

            boolean bHijack = false;
            try {
                bHijack = mAitkContext.inject(injection.getTargetPid(), injection.getInjectionLibrary());
            }
            catch (SuShellInvalidatedException e) {
                invalidateAitkContext();
            }

            if (bHijack)
            {
                Toast.makeText(this, "Injected", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void invalidateAitkContext() {
        mHijackBtn.setEnabled(false);
        mAitkContext = null;
        new AitkContextAsyncInit(this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAitkContext != null)
            mAitkContext.release();

        mTailConsole.closeTty();
    }

    private static final String TAG = HomeActivity.class.getSimpleName();
}
