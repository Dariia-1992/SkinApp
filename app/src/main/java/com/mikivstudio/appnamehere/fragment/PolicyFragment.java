package com.mikivstudio.appnamehere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.utils.Helpers;

/**
 * Created by Dariia Mshanetskaya  on 29.07.2019.
 */
public class PolicyFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_polity,container, false);

        View backButton = view.findViewById(R.id.toolbar_back);
        backButton.setOnClickListener(v -> mainManager.closeScreen());

        View rateButton = view.findViewById(R.id.toolbar_rate);
        rateButton.setOnClickListener(v -> Helpers.goToStore(getContext()));

        WebView webView = view.findViewById(R.id.container);
        webView.loadUrl("file:///android_asset/policy.html");

        return view;
    }
}
