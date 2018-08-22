package com.pulkit.android.viewmodelfactory;

import android.arch.lifecycle.ViewModel;
import com.pulkit.android.forge.api.Forge;
import com.pulkit.android.forge.api.Type;

public class SomeOtherViewModel extends ViewModel {

  private final int p;

  @Forge(type = Type.NEW_INSTANCE_FACTORY)
  public SomeOtherViewModel(int p) {
    this.p = p;
  }

}
