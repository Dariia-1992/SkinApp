package com.mikivstudio.appnamehere.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.mikivstudio.appnamehere.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Dariia Mshanetskaya  on 02.08.2019.
 */
public class ConfirmDialog extends DialogFragment {
    public interface OnFinishListener {
        void onFinish();
    }

    private OnFinishListener listener;

    public static ConfirmDialog createDialog(OnFinishListener listener){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.listener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getContext();
        if (context == null)
            return super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null, false);
        view.findViewById(R.id.ok_button).setOnClickListener(v -> {
            if (listener != null)
                listener.onFinish();
            dismiss();
        });
        view.findViewById(R.id.no_button).setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(context)
                .setView(view)
                .create();
    }
}
