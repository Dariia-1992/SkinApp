package com.mikivstudio.appnamehere.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.mikivstudio.appnamehere.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Dariia Mshanetskaya  on 01.06.2019.
 */
public class SavedToGalleryDialog extends DialogFragment {
    public interface OnFinishListener {
        void onFinish();
    }

    private OnFinishListener listener;

    public static SavedToGalleryDialog createDialog(OnFinishListener listener) {
        SavedToGalleryDialog dialog = new SavedToGalleryDialog();
        dialog.listener = listener;

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getContext();
        if (context == null)
            return super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_skin_save, null, false);
        view.findViewById(R.id.ok_button).setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(context)
                .setView(view)
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null)
            listener.onFinish();
    }
}
