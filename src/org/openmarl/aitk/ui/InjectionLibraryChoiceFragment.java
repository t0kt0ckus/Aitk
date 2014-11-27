package org.openmarl.aitk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.openmarl.aitk.R;

/**
 * Created by chris on 14/11/14.
 */
public class InjectionLibraryChoiceFragment extends Fragment {

    private Spinner mLibraryListSpinner;
    private ArrayAdapter<CharSequence> mLibraryListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_injlib_choice, container, false);
        mLibraryListSpinner = (Spinner) rootView.findViewById(R.id.spinner_injlib_choice);
        mLibraryListAdapter = ArrayAdapter.createFromResource(getActivity(),
                                                              R.array.aitk_std_libs,
                                                              android.R.layout.simple_spinner_item);
        mLibraryListSpinner.setAdapter(mLibraryListAdapter);

        return rootView;
    }

    public String getSelectedLibrary() {
        return mLibraryListSpinner.getSelectedItem().toString();
    }

}
