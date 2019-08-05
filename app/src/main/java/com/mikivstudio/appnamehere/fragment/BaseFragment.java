package com.mikivstudio.appnamehere.fragment;

import android.os.Bundle;

import com.mikivstudio.appnamehere.IMainManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Dariia Mshanetskaya  on 30.05.2019.
 */
public abstract class BaseFragment extends Fragment {
    protected IMainManager mainManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof IMainManager)
            mainManager = (IMainManager) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mainManager = null;
    }
}
