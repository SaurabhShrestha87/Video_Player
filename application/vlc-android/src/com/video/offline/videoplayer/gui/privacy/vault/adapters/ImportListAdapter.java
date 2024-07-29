package com.video.offline.videoplayer.gui.privacy.vault.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.offline.videoplayer.databinding.AdapterImportListItemBinding;
import com.video.offline.videoplayer.gui.privacy.vault.adapters.viewholders.ImportListViewHolder;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Dialogs;

import java.util.List;

public class ImportListAdapter extends RecyclerView.Adapter<ImportListViewHolder> {
    private final List<String> names;
    private final Dialogs.IOnPositionSelected onPositionSelected;

    public ImportListAdapter(List<String> names, Dialogs.IOnPositionSelected onPositionSelected) {
        this.names = names;
        this.onPositionSelected = onPositionSelected;
    }

    @NonNull
    @Override
    public ImportListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterImportListItemBinding binding = AdapterImportListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImportListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImportListViewHolder holder, int position) {
        holder.binding.text.setText(names.get(position));
        holder.binding.text.setOnClickListener(v -> onPositionSelected.onSelected(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
