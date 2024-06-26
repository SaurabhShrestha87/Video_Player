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

package org.videolan.vlc.gui.privacy.vault.encryption;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.datasource.DataSource;

public class MyDataSourceFactory implements DataSource.Factory {
    private final Context context;

    public MyDataSourceFactory(Context context) {
        this.context = context;
    }

    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    @NonNull
    @Override
    public DataSource createDataSource() {
        return new MyDataSource(context);
    }
}
