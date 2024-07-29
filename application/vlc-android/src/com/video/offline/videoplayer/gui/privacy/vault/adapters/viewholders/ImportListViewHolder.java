package com.video.offline.videoplayer.gui.privacy.vault.adapters.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.offline.videoplayer.databinding.AdapterImportListItemBinding;


public class ImportListViewHolder extends RecyclerView.ViewHolder {
    public final AdapterImportListItemBinding binding;

    public ImportListViewHolder(@NonNull AdapterImportListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
