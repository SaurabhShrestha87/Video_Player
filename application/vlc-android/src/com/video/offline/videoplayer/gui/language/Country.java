package com.video.offline.videoplayer.gui.language;

import android.content.Context;
import android.text.TextUtils;

import java.util.Locale;

public class Country {
    // region Variables
    private String code;
    private String countryName;
    private String localeEntry;
    private String localeEntryValue;
    private int flag;
    private boolean isSelected = false;

    // region Constructors
    Country() {

    }


    Country(String code, String countryName, int flag) {
        this.countryName = countryName;
        this.code = code;
        this.flag = flag;
    }

    Country(String code, String countryName, String localeEntry, String localeEntryValue, int flag) {
        this.countryName = countryName;
        this.code = code;
        this.localeEntry = localeEntry;
        this.localeEntryValue = localeEntryValue;
        this.flag = flag;
    }
    // endregion

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        if (TextUtils.isEmpty(localeEntryValue)) {
            localeEntryValue = new Locale("", code).getDisplayName();
        }
    }

    public String getLocaleEntry() {
        return localeEntry;
    }

    public void setLocaleEntry(String localeEntry) {
        this.localeEntry = localeEntry;
    }

    public String getLocaleEntryValue() {
        return localeEntryValue;
    }

    public void setLocaleEntryValue(String localeEntryValue) {
        this.localeEntryValue = localeEntryValue;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void loadFlagByCode(Context context) {
        if (this.flag != -1) {
            return;
        }

        try {
            this.flag = context.getResources().getIdentifier("flag_" + this.code.toLowerCase(Locale.ENGLISH), "drawable", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            this.flag = -1;
        }
    }
    // endregion


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}