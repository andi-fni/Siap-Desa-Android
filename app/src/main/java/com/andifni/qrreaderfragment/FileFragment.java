package com.andifni.qrreaderfragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.shell.fab.ActionButton;

/**
 * Created by Andi Fajar on 28-Apr-15.
 */
public class FileFragment extends Fragment {
    private ActionButton actionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_file,container,false);
        actionButton = (ActionButton) v.findViewById(R.id.action_button);
        actionButton.setHideAnimation(ActionButton.Animations.ROLL_TO_DOWN);
        actionButton.setShowAnimation(ActionButton.Animations.ROLL_FROM_DOWN);
        actionButton.hide();
        return v;
    }

    public void showActionButton() {
//        actionButton = (ActionButton) getView().findViewById(R.id.action_button);
//        Log.e("FRAGMENT", ""+actionButton.getButtonColor());
        actionButton.show();
    }

    public void hideActionButton() {
//        actionButton = (ActionButton) getView().findViewById(R.id.action_button);
        actionButton.hide();
    }
}
