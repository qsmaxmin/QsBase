package com.qsmaxmin.qsbase.common.utils.glideWithOkHttp;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/12/18 15:22
 * @Description
 */
@GlideModule
public class OkHttpAppGlideModule extends AppGlideModule {
    @Override public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
    }
}
