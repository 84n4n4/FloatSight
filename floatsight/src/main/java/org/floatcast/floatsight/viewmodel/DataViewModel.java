package org.floatcast.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public abstract class DataViewModel<T> extends ViewModel {

    public abstract LiveData<T> getLiveData();

    public abstract MutableLiveData<T> getMutableLiveData();

    public abstract boolean containsValidData();
}
