package com.mikivstudio.appnamehere.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.mikivstudio.appnamehere.BuildConfig;
import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.adapter.SkinsAdapter;
import com.mikivstudio.appnamehere.customview.GridAutofitLayoutManager;
import com.mikivstudio.appnamehere.model.Skin;
import com.mikivstudio.appnamehere.utils.BaseURL;
import com.mikivstudio.appnamehere.utils.FavoritesManager;
import com.mikivstudio.appnamehere.utils.Helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkinsFragment extends BaseFragment {
    private static final String KEY_MODE = "key_mode";

    private final ArrayList<Skin> allSkinsList = new ArrayList<>();
    private final ArrayList<Skin> filteredSkinsList = new ArrayList<>();

    public static final int MODE_LATEST = 0;
    public static final int MODE_FAVORITE = 1;
    public static final int MODE_DOWNLOADS = 2;
    public static final int MODE_VIEWS = 3;

    private View mView;
    private SkinsAdapter adapter;
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private boolean unsort = true;
    private String searchStr;

    private DrawerLayout drawerLayout;

    public static SkinsFragment createInstance(int mode) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MODE, mode);

        SkinsFragment fragment = new SkinsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        if (getContext() == null)
            return mView;

        NavigationView navigationView = mView.findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(itemListener);

        drawerLayout = mView.findViewById(R.id.drawer_layout);

        View menuView = mView.findViewById(R.id.toolbar_menu);
        menuView.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else
                drawerLayout.closeDrawers();
        });

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);

        adapter = new SkinsAdapter(filteredSkinsList,
                mOnItemSelected,
                getContext());

        recyclerView = mView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

//        View sortButton = mView.findViewById(R.id.sort_image);
//        sortButton.setOnClickListener(v -> {
//            unsort = !unsort;
//            filterData();
//        });

        View searchButton = mView.findViewById(R.id.toolbar_search);
        searchButton.setOnClickListener(v -> setupSearch(true));

        initMode();
        initData();
        setupSearch(false);

        return mView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate_app: {
                Helpers.goToStore(getContext());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearch(boolean show) {
        View searchButton = mView.findViewById(R.id.toolbar_search);
        View toolbarText = mView.findViewById(R.id.toolbar_name);

        SearchView searchView = mView.findViewById(R.id.search);
        searchView.setOnCloseListener(() -> { setupSearch(false); return false; });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchStr = newText;
                filterData();
                return false;
            }
        });

        searchButton.setVisibility(show ? View.GONE : View.VISIBLE);
        toolbarText.setVisibility(show ? View.GONE : View.VISIBLE);
        searchView.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        }
    }

    private void descendingSorting(ArrayList<Skin> descendList){
        Collections.sort(descendList, (l, r) -> {
            Skin left = unsort ? l : r;
            Skin right = unsort ? r : l;

            if (left.isNumber() && right.isNumber())
                return Integer.compare(right.getIntName(), left.getIntName());
            else if (!left.isNumber() && !right.isNumber())
                return right.getName().compareToIgnoreCase(left.getName());
            else if (!left.isNumber() && right.isNumber())
                return -1;
            else
                return 1;
        });
    }

    private void initMode() {
        TextView latestButton = mView.findViewById(R.id.button_latest);
        TextView favoritesButton = mView.findViewById(R.id.button_favorite);
        TextView downloadsButton = mView.findViewById(R.id.button_most_downloads);
        TextView viewsButton = mView.findViewById(R.id.button_most_views);

        latestButton.setOnClickListener(v -> mainManager.setScreen(SkinsFragment.createInstance(MODE_LATEST)));
        favoritesButton.setOnClickListener(v -> mainManager.setScreen(SkinsFragment.createInstance(MODE_FAVORITE)));
        downloadsButton.setOnClickListener(v -> mainManager.setScreen(SkinsFragment.createInstance(MODE_DOWNLOADS)));
        viewsButton.setOnClickListener(v -> mainManager.setScreen(SkinsFragment.createInstance(MODE_VIEWS)));

        int mode = getMode();
        setCheckedState(latestButton, mode == MODE_LATEST);
        setCheckedState(favoritesButton, mode == MODE_FAVORITE);
        setCheckedState(downloadsButton, mode == MODE_DOWNLOADS);
        setCheckedState(viewsButton, mode == MODE_VIEWS);

        downloadsButton.setVisibility(BuildConfig.SERVER_UI_ENABLED ? View.VISIBLE : View.GONE);
        viewsButton.setVisibility(BuildConfig.SERVER_UI_ENABLED ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        allSkinsList.clear();

        Context context = getContext();
        int mode = getMode();
        for (int index = 0; index <= getResources().getInteger(R.integer.show_all); index++) {
            Skin skin = new Skin(index);
            switch (mode) {
                case MODE_FAVORITE: {
                    if (FavoritesManager.getInstance().isFavorite(context, skin.getNumber()))
                        allSkinsList.add(skin);
                    break;
                }

                // TODO: max count for downloads and views?
                case MODE_LATEST:
                case MODE_DOWNLOADS:
                case MODE_VIEWS:
                    allSkinsList.add(skin);
                    break;
            }
//            if (!isFavoriteMode || FavoritesManager.getInstance().isFavorite(context, skin.getNumber()))
//                allSkinsList.add(skin);
        }

        filterData();
    }

    private void filterData() {
        filteredSkinsList.clear();

        String part = getString(R.string.skin_name_formatted);

        // Search
        for (Skin skin : allSkinsList)
            if (containsIgnoreCase(String.format(part, skin.getName()), searchStr))
                filteredSkinsList.add(skin);

        // Sorting
        descendingSorting(filteredSkinsList);
        // TODO: sort by downloads
        // TODO: sort by views

        adapter.notifyDataSetChanged();
    }

    private int getMode() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return MODE_LATEST;

        return bundle.getInt(KEY_MODE, MODE_LATEST);
    }

    private void setCheckedState(TextView textView, boolean isChecked) {
        if (getContext() == null)
            return;

        textView.setTextColor(ContextCompat.getColor(getContext(), isChecked ? R.color.black : R.color.white));
        textView.setBackgroundResource(isChecked ? R.drawable.background_tab_button : 0);
    }

    private static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null)
            return false;

        if (searchStr == null)
            return true;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    private void showAlert(@StringRes int messageId) {
        if (getContext() == null)
            return;

        new AlertDialog.Builder(getContext())
            .setMessage(messageId)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    private SkinsAdapter.OnItemSelected mOnItemSelected = skin -> {
         if (!Helpers.isNetworkAvailable(getContext())) {
             showAlert(R.string.no_internet_connection);
             return;
         }

         File path = getContext().getCacheDir();
         new LoadImageTask(skin, path).execute();
    };

    private class LoadImageTask extends AsyncTask<Void, Void, Boolean> {
        private Skin skin;
        private File localPath;

        LoadImageTask(Skin skin, File localPath) {
            this.skin = skin;
            this.localPath = localPath;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = new File(localPath, String.format("%s.png", skin.getNumber()));
            String url = BaseURL.createSkinPath(skin.getNumber());

            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }

                return true;

            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            if (success){
                mainManager.addScreen(SkinDetailsFragment.createInstance(skin));
            } else {
                showAlert(R.string.skin_loading_error);
            }
        }
    }

    private final NavigationView.OnNavigationItemSelectedListener itemListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.action_favorite: mainManager.setScreen(SkinsFragment.createInstance(MODE_FAVORITE)); break;
            case R.id.action_rate: Helpers.goToStore(getContext()); break;
            case R.id.action_policy: mainManager.addScreen(new PolicyFragment()); break;
        }

        drawerLayout.closeDrawers();
        return true;
    };

}