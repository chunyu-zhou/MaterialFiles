/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import me.zhanghai.android.files.AppApplication;
import me.zhanghai.android.files.compat.PreferenceManagerCompat;

public abstract class SettingLiveData<T> extends LiveData<T> {

    @NonNull
    private final SharedPreferences mSharedPreferences;
    @NonNull
    private final String mKey;
    private final T mDefaultValue;

    public SettingLiveData(@Nullable String name, @NonNull String key,
                           @AnyRes int defaultValueRes) {
        mSharedPreferences = getSharedPreferences(name);
        mKey = key;
        mDefaultValue = getDefaultValue(defaultValueRes);

        loadValue();
        // Only a weak reference is stored so we don't need to worry about unregistering.
        mSharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key2) -> {
            if (Objects.equals(key2, mKey)) {
                loadValue();
            }
        });
    }

    public SettingLiveData(@StringRes int keyRes, @AnyRes int defaultValueRes) {
        this(null, AppApplication.getInstance().getString(keyRes), defaultValueRes);
    }

    @NonNull
    private static SharedPreferences getSharedPreferences(@Nullable String nameSuffix) {
        Context context = AppApplication.getInstance();
        if (nameSuffix == null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            String name = PreferenceManagerCompat.getDefaultSharedPreferencesName(context) + '_'
                    + nameSuffix;
            int mode = PreferenceManagerCompat.getDefaultSharedPreferencesMode();
            return context.getSharedPreferences(name, mode);
        }
    }

    protected abstract T getDefaultValue(@AnyRes int defaultValueRes);

    private void loadValue() {
        setValue(getValue(mSharedPreferences, mKey, mDefaultValue));
    }

    protected abstract T getValue(@NonNull SharedPreferences sharedPreferences, @NonNull String key,
                                  T defaultValue);

    public final void putValue(T value) {
        putValue(mSharedPreferences, mKey, value);
    }

    protected abstract void putValue(@NonNull SharedPreferences sharedPreferences,
                                     @NonNull String key, T value);
}
