package com.android.functiontest.deviceinfo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.preference.Preference;

import com.android.functiontest.R;

/**
 * Created by yt on 2017/3/24.
 */
public class StorageItemPreference extends Preference {
    public final int color;
    public StorageItemPreference(Context context, int titleRes, int colorRes) {
        this(context, context.getText(titleRes), colorRes);
    }

    public StorageItemPreference(Context context, CharSequence title, int colorRes) {
        super(context);
        if (colorRes != 0) {
            this.color = context.getResources().getColor(colorRes);
            final Resources res = context.getResources();
            final int width = 32;
            final int height = 60;
            setIcon(createRectShape(width, height, this.color));
        }else {
            this.color = Color.MAGENTA;
        }
        setTitle(title);
        setSummary(R.string.memory_calculating_size);
    }

    private static ShapeDrawable createRectShape(int width, int height, int color) {
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setIntrinsicHeight(height);
        shape.setIntrinsicWidth(width);
        shape.getPaint().setColor(color);
        return shape;
    }
}
