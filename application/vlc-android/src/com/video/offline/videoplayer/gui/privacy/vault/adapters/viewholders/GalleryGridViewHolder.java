package com.video.offline.videoplayer.gui.privacy.vault.adapters.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.offline.videoplayer.databinding.AdapterGalleryGridItemBinding;

public class GalleryGridViewHolder extends RecyclerView.ViewHolder {
    public final AdapterGalleryGridItemBinding binding;

    public GalleryGridViewHolder(@NonNull AdapterGalleryGridItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
