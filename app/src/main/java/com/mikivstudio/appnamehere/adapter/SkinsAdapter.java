package com.mikivstudio.appnamehere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikivstudio.appnamehere.BuildConfig;
import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.model.Skin;
import com.mikivstudio.appnamehere.utils.BaseURL;
import com.mikivstudio.appnamehere.utils.ConstantFunctions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SkinsAdapter extends RecyclerView.Adapter<SkinsAdapter.ViewHolder>{
    public interface OnItemSelected{
        void onItemSelected(Skin skin);
    }

    private Context context;
    private ArrayList<Skin> originalList;
    private OnItemSelected listener;

    public SkinsAdapter(ArrayList<Skin> originalList, OnItemSelected listener, Context context) {
        this.originalList = originalList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_skinlist_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Skin skin = originalList.get(i);
        String imagePath = BaseURL.createThumbnailPath(skin.getNumber());

        viewHolder.skinName.setText(String.format(context.getString(R.string.skin_name_formatted), skin.getName()));
        viewHolder.viewsCount.setText("123456");
        viewHolder.downloadsCount.setText("12345");
        ConstantFunctions.loadImage(imagePath, viewHolder.skinImage);
        viewHolder.itemView.setOnClickListener(v -> {
            if (listener != null){
                listener.onItemSelected(skin);
            }
        });

        viewHolder.bottomBackground.setVisibility(BuildConfig.SERVER_UI_ENABLED ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return originalList != null ? originalList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView skinName;
        ImageView skinImage;
        TextView viewsCount;
        TextView downloadsCount;
        View bottomBackground;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            skinName = itemView.findViewById(R.id.skin_name);
            skinImage = itemView.findViewById(R.id.skinImage);
            viewsCount = itemView.findViewById(R.id.views_count);
            downloadsCount = itemView.findViewById(R.id.downloads_count);
            bottomBackground = itemView.findViewById(R.id.bottom_background);
        }
    }
}
