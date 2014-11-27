package org.openmarl.aitk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.openmarl.aitk.R;
import org.openmarl.susrv.LibSusrv;

import java.util.Arrays;

/**
 * Created by chris on 14/11/14.
 */
public class ProcessChoiceFragment extends Fragment {

    private Spinner mProcessListSpinner;
    private ArrayAdapter<String> mProcessListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_process_choice, container, false);
        mProcessListSpinner = (Spinner) rootView.findViewById(R.id.spinner_process_choice);
        mProcessListAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        mProcessListSpinner.setAdapter(mProcessListAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshProcessList();
    }

    public String getSelectedProcess() {
        return mProcessListSpinner.getSelectedItem().toString();
    }

    public void refreshProcessList() {
        mProcessListAdapter.clear();

        String[] procNames = LibSusrv.getproclist();
        Arrays.sort(procNames);

        for (String procname : procNames) {
            mProcessListAdapter.add(procname);
        }
        mProcessListAdapter.notifyDataSetChanged();
    }
}
