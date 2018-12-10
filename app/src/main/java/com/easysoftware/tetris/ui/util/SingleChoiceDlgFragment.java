package com.easysoftware.tetris.ui.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easysoftware.tetris.R;

public class SingleChoiceDlgFragment extends DialogFragment {
    public static final String TITLE_KEY = "dlg_title";
    public static final String ITEMS_KEY = "dlg_items";

    // event listener
    public interface OnChooseListener {
        void onChoose(int which);
        void onCancel();
    }

    private OnChooseListener mOnChooseListener;

    public SingleChoiceDlgFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SingleChoiceDlgFragment newInstance(String title, String[] items,
                                                      OnChooseListener listener) {
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putStringArray(ITEMS_KEY, items);

        SingleChoiceDlgFragment fragment = new SingleChoiceDlgFragment();
        fragment.setArguments(args);
        fragment.setOnChooseListener(listener);
        return fragment;
    }

    public void setOnChooseListener(OnChooseListener listener) {
        mOnChooseListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE_KEY);
        String[] items = getArguments().getStringArray(ITEMS_KEY);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the current view as a TextView
                TextView tv = (TextView) super.getView(position, convertView, parent);
                // Set the item text gravity to right/end and vertical center
                tv.setGravity(Gravity.CENTER);

                position %= 4;
                int cv = 255 - (position+1) * 24;
                tv.setBackgroundColor(Color.rgb(0, cv, 0));

                // Return the view
                return tv;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        if (mOnChooseListener != null) {
                            mOnChooseListener.onCancel();
                        }
                    }
                })
                .setSingleChoiceItems(adapter, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                                if (mOnChooseListener != null) {
                                    mOnChooseListener.onChoose(which);
                                }
                            }
                        })
                .setCancelable(false);

        TextView textView = Utils.createDlgTitle(getActivity(), title);
        builder.setCustomTitle(textView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
