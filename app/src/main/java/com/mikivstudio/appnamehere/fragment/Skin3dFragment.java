package com.mikivstudio.appnamehere.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.model.Skin;

import java.io.File;

/**
 * Created by Dariia Mshanetskaya  on 29.07.2019.
 */
public class Skin3dFragment extends BaseFragment {
    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_NAME = "extra_name";

    private View view;
    private Skin skin;

    public static Skin3dFragment createInstance(Skin skin) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_POSITION, skin.getNumber());
        bundle.putString(EXTRA_NAME, skin.getName());

        Skin3dFragment fragment = new Skin3dFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3d_preview, container, false);

        skin = getSkin();
        if (skin == null)
            throw new IllegalArgumentException("Missing skin data");

        TextView nameView = view.findViewById(R.id.skin_name);
        nameView.setText(String.format(getString(R.string.skin_name_formatted), skin.getName()));

        View backButton = view.findViewById(R.id.toolbar_close);
        backButton.setOnClickListener(v -> mainManager.closeScreen());

        WebView webview = view.findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.clearCache(true);
        webview.clearHistory();
        webview.loadUrl("");
        webview.loadUrl("file:///android_asset/index.html?url=" + getCachedImage().getAbsolutePath());

        return view;
    }

    private Skin getSkin() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return null;

        String number = bundle.getString(EXTRA_POSITION, null);
        String name = bundle.getString(EXTRA_NAME, null);
        if (number == null || name == null)
            return null;

        return new Skin(number, name);
    }

    private File getCachedImage() {
        File cacheFolder = getContext().getCacheDir();
        return new File(cacheFolder, String.format("%s.png", skin.getNumber()));
    }
}
