package com.easysoftware.tetris.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

public class AutoDismissDlgFragment extends DialogFragment {
    public static final String TITLE_KEY = "dlg_title";
    public static final String MSG_KEY = "dlg_msg";
    public static final String DURATION_KEY = "dlg_duration";

    // event listener
    public interface OnDismissListener {
        void onDismiss();
    }

    private OnDismissListener mOnDismissListener;

    public AutoDismissDlgFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AutoDismissDlgFragment newInstance(String title, String message,
                                                     int durationInMilliSecond) {
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MSG_KEY, message);
        args.putInt(DURATION_KEY, durationInMilliSecond);

        AutoDismissDlgFragment fragment = new AutoDismissDlgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnDismissListener = (OnDismissListener)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE_KEY);
        String message = getArguments().getString(MSG_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        TextView textView = Utils.createDlgTitle(getActivity(), title);
        builder.setCustomTitle(textView);

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);

        int durationInMilliSecond = getArguments().getInt(DURATION_KEY);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Dialog dlg = getDialog();
                if (dlg != null && dlg.isShowing()) {
                    dismiss();
                    if (mOnDismissListener != null) {
                        mOnDismissListener.onDismiss();
                    }
                }
            }
        }, durationInMilliSecond);
    }
}
