package com.android.monsoursaleh.missionalarm;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverter {
    @TypeConverter
    public static Uri toUri(String uri) {
        return uri != null ? Uri.parse(uri): null;
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
        return uri != null ? uri.toString(): null;
    }
}
