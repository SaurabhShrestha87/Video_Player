package com.video.offline.videoplayer.gui.language;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.offline.videoplayer.R;

import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {
    final List<Country> countries;
    // region Variables
    private final OnItemClickListener listener;
    private final Context context;

    //region Constructor
    public CountriesAdapter(Context context, List<Country> countries, OnItemClickListener listener) {
        this.context = context;
        this.countries = countries;
        this.listener = listener;
    }

    // region Adapter Methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Country country = countries.get(position);
        holder.countryNameText.setText(country.getLocaleEntry());
        country.loadFlagByCode(context);
        if (country.getFlag() != -1) {
            holder.countryFlagImageView.setImageResource(country.getFlag());
        }
        holder.selectedRb.setChecked(country.isSelected());
        holder.rootView.setOnClickListener(v -> {
            countries.forEach(c -> c.setSelected(false));
            country.setSelected(true);
            listener.onItemClicked(country);
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    // region ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView countryFlagImageView;
        private final TextView countryNameText;
        private final LinearLayout rootView;
        private final RadioButton selectedRb;

        ViewHolder(View itemView) {
            super(itemView);
            countryFlagImageView = itemView.findViewById(R.id.country_flag);
            countryNameText = itemView.findViewById(R.id.country_title);
            rootView = itemView.findViewById(R.id.rootView);
            selectedRb = itemView.findViewById(R.id.selected_rb);
        }
    }
}