package com.video.offline.videoplayer.gui.language;

import android.content.Context;
import android.text.TextUtils;
import java.util.Locale;

public class Country {
  // region Variables
  private String code;
  private String name;
  private int flag;

  // region Constructors
  Country() {

  }

  Country(String code, String name, int flag) {
    this.code = code;
    this.name = name;
    this.flag = flag;
  }
  // endregion

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
    if (TextUtils.isEmpty(name)) {
      name = new Locale("", code).getDisplayName();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
      this.flag = context.getResources()
          .getIdentifier("flag_" + this.code.toLowerCase(Locale.ENGLISH), "drawable",
              context.getPackageName());
    } catch (Exception e) {
      e.printStackTrace();
      this.flag = -1;
    }
  }
  // endregion
}