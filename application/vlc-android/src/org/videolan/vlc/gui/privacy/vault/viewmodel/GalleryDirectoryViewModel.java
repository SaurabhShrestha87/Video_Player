/*
 * Valv-Android
 * Copyright (C) 2023 Arctosoft AB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.videolan.vlc.gui.privacy.vault.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import org.videolan.vlc.gui.privacy.vault.data.GalleryFile;

import java.util.LinkedList;
import java.util.List;

public class GalleryDirectoryViewModel extends ViewModel {
    private static final String TAG = "GalleryDirectoryViewMod";

    private final List<GalleryFile> galleryFiles = new LinkedList<>();
    private int currentPosition = 0;
    private boolean viewPagerVisible = false;
    private boolean initialised = false;

    public boolean isInitialised() {
        return initialised;
    }

    @NonNull
    public List<GalleryFile> getGalleryFiles() {
        return galleryFiles;
    }

    public void setInitialised(List<GalleryFile> galleryFiles) {
        //Log.e(TAG, "setInitialised: " + galleryFiles.size());
        if (initialised) {
            return;
        }
        this.initialised = true;
        this.galleryFiles.addAll(galleryFiles);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean isViewpagerVisible() {
        return viewPagerVisible;
    }

    public void setViewpagerVisible(boolean fullscreen) {
        viewPagerVisible = fullscreen;
    }
}
