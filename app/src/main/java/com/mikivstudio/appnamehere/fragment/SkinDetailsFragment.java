package com.mikivstudio.appnamehere.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikivstudio.appnamehere.BuildConfig;
import com.mikivstudio.appnamehere.MainActivity;
import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.dialogs.ConfirmDialog;
import com.mikivstudio.appnamehere.dialogs.SavedToGalleryDialog;
import com.mikivstudio.appnamehere.dialogs.SavedToMinecraftDialog;
import com.mikivstudio.appnamehere.model.Skin;
import com.mikivstudio.appnamehere.myapplication.Myapplication;
import com.mikivstudio.appnamehere.utils.BaseURL;
import com.mikivstudio.appnamehere.utils.ConstantFunctions;
import com.mikivstudio.appnamehere.utils.FavoritesManager;
import com.mikivstudio.appnamehere.utils.Helpers;
import com.mikivstudio.appnamehere.utils.LocalStorage;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created by Dariia Mshanetskaya  on 29.07.2019.
 */
public class SkinDetailsFragment extends BaseFragment {
    private enum ActionType { Gallery, Application }
    private static final int WRITE_PERMISSION_CODE = 10256;

    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_NAME = "extra_name";

    private View view;
    private Skin skin;
    private ActionType actionType;

    private boolean alreadyLoaded;

    public static SkinDetailsFragment createInstance(Skin skin) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_POSITION, skin.getNumber());
        bundle.putString(EXTRA_NAME, skin.getName());

        SkinDetailsFragment fragment = new SkinDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_skin_details,container, false);

        skin = getSkin();
        if (skin == null)
            throw new IllegalArgumentException("Missing skin data");

        // Load skin image
        ConstantFunctions.loadImageWithoutThumbnail(
                BaseURL.createThumbnailPath(skin.getNumber()),
                view.findViewById(R.id.skin_container));

        View backButton = view.findViewById(R.id.toolbar_back);
        backButton.setOnClickListener(v -> mainManager.closeScreen());

        View rateButton = view.findViewById(R.id.toolbar_rate);
        rateButton.setOnClickListener(v -> Helpers.goToStore(getContext()));

        View favoritesButton = view.findViewById(R.id.skin_button_favorite);
        favoritesButton.setOnClickListener(onFavoriteClicked);

        View previewButton = view.findViewById(R.id.skin_button_3d);
        previewButton.setOnClickListener(v -> mainManager.addScreen(Skin3dFragment.createInstance(skin)));

        View saveButton = view.findViewById(R.id.skin_button_save);
        saveButton.setOnClickListener(v -> showConfirmIfEnabled(() -> {
            actionType = ActionType.Gallery;
            requestOrRun(this::processActions);
        }));

        View exportButton = view.findViewById(R.id.skin_button_export);
        exportButton.setOnClickListener(v -> showConfirmIfEnabled(() -> {
            actionType = ActionType.Application;
            requestOrRun(this::processActions);
        }));

        TextView nameView = view.findViewById(R.id.skin_name);
        nameView.setText(String.format(getString(R.string.skin_name_formatted), skin.getName()));

        updateFavoriteState();
        updateSkinInfo();

        if (!alreadyLoaded) {
            int showsWithoutAdCount = LocalStorage.getOpensWithoutAd(getContext());
            if (showsWithoutAdCount >= BuildConfig.SHOWS_WITHOUT_AD_COUNT || showsWithoutAdCount < 0) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).showInterstitialAd();
            } else
                LocalStorage.setOpensWithoutAd(getContext(), showsWithoutAdCount + 1);
        }

        View infoContainer = view.findViewById(R.id.info_container);
        infoContainer.setVisibility(BuildConfig.SERVER_UI_ENABLED ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null && !alreadyLoaded)
            alreadyLoaded = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != WRITE_PERMISSION_CODE)
            return;

        if (permissions.length == 1 && grantResults.length == 1)
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permissions[0])
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                processActions();
    }

    private void updateFavoriteState() {
        ImageView image = view.findViewById(R.id.skin_button_favorite);
        image.setImageResource(FavoritesManager.getInstance().isFavorite(getContext(), skin.getNumber())
                ? R.drawable.skin_button_favorite_on
                : R.drawable.skin_button_favorite_off);
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

    private void updateSkinInfo() {
        TextView views = view.findViewById(R.id.skin_views_count);
        TextView downloads = view.findViewById(R.id.skin_downloads_count);
        TextView size = view.findViewById(R.id.skin_size_count);

        views.setText("345567");
        downloads.setText("3456");
        size.setText("64x64");
    }

    private void requestOrRun(@NonNull Runnable onGranted) {
        if (getActivity() == null)
            return;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_PERMISSION_CODE);
        } else
            onGranted.run();
    }

    private void processActions() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.showVideoAd(this::startAction);
        }
    }

    private void startAction() {
        if (actionType == null || skin == null)
            return;

        try {
            File savedFile = getCachedImage();
            switch (actionType) {
                case Gallery: {
                    File newFile = Helpers.createFileInGallery(getContext(), skin.getNumber());
                    Helpers.copyFile(savedFile, newFile);
                    MediaScannerConnection.scanFile(Myapplication.getContext(), new String[]{ newFile.getPath() }, null, null);
                    alertAfterSaved();
                    break;
                }
                case Application: {
                    File newFile = Helpers.createFileInApplication("custom");
                    Helpers.copyFile(savedFile, newFile);
                    alertAfterInstalled();
                    break;
                }
            }
        } catch (Exception ex) {
            if (getContext() != null)
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmIfEnabled(@NonNull Runnable callback) {
        if (BuildConfig.CONFIRM_ENABLED) {
            ConfirmDialog dialog = ConfirmDialog.createDialog(callback::run);
            dialog.show(getChildFragmentManager(), "dialog");
        } else
            callback.run();
    }

    private File getCachedImage() {
        File cacheFolder = getContext().getCacheDir();
        return new File(cacheFolder, String.format("%s.png", skin.getNumber()));
    }

    private void alertAfterInstalled() {
        SavedToMinecraftDialog dialog = SavedToMinecraftDialog.createDialog(null);
        mainManager.showDialog(dialog);
    }

    private void alertAfterSaved() {
        SavedToGalleryDialog dialog = SavedToGalleryDialog.createDialog(null);
        mainManager.showDialog(dialog);
    }

    private final View.OnClickListener onFavoriteClicked = v -> {
        if (getContext() == null || skin == null)
            return;

        FavoritesManager manager = FavoritesManager.getInstance();
        boolean isFavorite = manager.isFavorite(getContext(), skin.getNumber());
        if (isFavorite)
            manager.removeFavorite(getContext(), skin.getNumber());
        else
            manager.addToFavorite(getContext(), skin.getNumber());

        updateFavoriteState();
    };
}
