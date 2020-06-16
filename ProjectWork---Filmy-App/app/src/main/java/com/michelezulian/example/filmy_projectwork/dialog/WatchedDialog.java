package com.michelezulian.example.filmy_projectwork.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class WatchedDialog extends DialogFragment {

    public interface IwatchedDialog {
        void onResponse(boolean aResponse, long aId, String TAG);
    }

    IwatchedDialog mListener;
    String mTitle, mMessage;
    long mId;
    private String TAG;

    public WatchedDialog(String aTitle, String aMessage, long aId, String tag) {
        mTitle = aTitle;
        mMessage = aMessage;
        mId = aId;
        TAG = tag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());
        vBuilder.setTitle(mTitle);
        vBuilder.setMessage(mMessage);

        vBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mListener.onResponse(true, mId, TAG);
            }
        });

        vBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onResponse(false, mId, TAG);
            }
        });
        return vBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IwatchedDialog) {
            mListener = (IwatchedDialog) activity;
        } else {
            mListener = null;
        }
    }

}
