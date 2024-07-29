package com.video.offline.videoplayer.gui.privacy.vault.adapters.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.offline.videoplayer.databinding.AdapterGalleryViewpagerItemBinding;
import com.video.offline.videoplayer.databinding.AdapterGalleryViewpagerItemDirectoryBinding;
import com.video.offline.videoplayer.databinding.AdapterGalleryViewpagerItemGifBinding;
import com.video.offline.videoplayer.databinding.AdapterGalleryViewpagerItemImageBinding;
import com.video.offline.videoplayer.databinding.AdapterGalleryViewpagerItemVideoBinding;


public class GalleryPagerViewHolder extends RecyclerView.ViewHolder {
    public final AdapterGalleryViewpagerItemBinding parentBinding;

    private GalleryPagerViewHolder(AdapterGalleryViewpagerItemBinding parentBinding) {
        super(parentBinding.getRoot());
        this.parentBinding = parentBinding;
    }

    public static class GalleryPagerImageViewHolder extends GalleryPagerViewHolder {
        public final AdapterGalleryViewpagerItemImageBinding binding;

        public GalleryPagerImageViewHolder(AdapterGalleryViewpagerItemBinding parentBinding, AdapterGalleryViewpagerItemImageBinding binding) {
            super(parentBinding);
            this.binding = binding;
        }
    }

    public static class GalleryPagerGifViewHolder extends GalleryPagerViewHolder {
        public final AdapterGalleryViewpagerItemGifBinding binding;

        public GalleryPagerGifViewHolder(AdapterGalleryViewpagerItemBinding parentBinding, @NonNull AdapterGalleryViewpagerItemGifBinding binding) {
            super(parentBinding);
            this.binding = binding;
        }
    }

    public static class GalleryPagerVideoViewHolder extends GalleryPagerViewHolder {
        public final AdapterGalleryViewpagerItemVideoBinding binding;

        public GalleryPagerVideoViewHolder(AdapterGalleryViewpagerItemBinding parentBinding, @NonNull AdapterGalleryViewpagerItemVideoBinding binding) {
            super(parentBinding);
            this.binding = binding;
        }

    }

    public static class GalleryPagerDirectoryViewHolder extends GalleryPagerViewHolder {
        public final AdapterGalleryViewpagerItemDirectoryBinding binding;

        public GalleryPagerDirectoryViewHolder(AdapterGalleryViewpagerItemBinding parentBinding, @NonNull AdapterGalleryViewpagerItemDirectoryBinding binding) {
            super(parentBinding);
            this.binding = binding;
        }

    }
}
