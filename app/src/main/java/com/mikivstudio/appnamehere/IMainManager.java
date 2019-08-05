package com.mikivstudio.appnamehere;

import com.mikivstudio.appnamehere.fragment.BaseFragment;

import androidx.fragment.app.DialogFragment;

/**
 * Created by Dariia Mshanetskaya  on 30.05.2019.
 */
public interface IMainManager {
    void addScreen(BaseFragment fragment); // Add new screen (prev is moved to stack)
    void setScreen(BaseFragment fragment); // Remove prev screens and set new one
    void closeScreen();

    void showDialog(DialogFragment dialog);
}