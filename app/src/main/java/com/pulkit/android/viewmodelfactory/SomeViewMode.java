package com.pulkit.android.viewmodelfactory;

import android.arch.lifecycle.ViewModel;
import com.pulkit.android.forge.api.FactoryInject;

@FactoryInject
public class SomeViewMode extends ViewModel{

  private final String sp;
  private final int x;

  SomeViewMode(String sp, int x){
    this.sp = sp;
    this.x = x;
  }
}
