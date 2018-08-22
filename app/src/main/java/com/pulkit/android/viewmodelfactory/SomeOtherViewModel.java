package com.pulkit.android.viewmodelfactory;

import android.arch.lifecycle.ViewModel;
import com.pulkit.android.forge.api.Forge;

public class SomeOtherViewModel extends ViewModel {

  private final int p;

  @Forge
  public SomeOtherViewModel(int p) {
    this.p = p;
  }

}
