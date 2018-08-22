package com.pulkit.android.viewmodelfactory;

import android.arch.lifecycle.ViewModel;
import com.pulkit.android.forge.api.Forge;

public class SomeViewModel extends ViewModel{

  private final String sp;
  private final int x;

  @Forge
  public SomeViewModel(String sp, int x){
    this.sp = sp;
    this.x = x;
  }
}
