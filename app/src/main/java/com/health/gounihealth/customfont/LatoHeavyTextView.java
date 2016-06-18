package com.health.gounihealth.customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by LAL on 6/9/2016.
 */
public class LatoHeavyTextView extends TextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public LatoHeavyTextView(final Context context) {
        this(context, null);
    }

    public LatoHeavyTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LatoHeavyTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "lato_heavy.TTF");
        }
        setTypeface(mTypeface);
    }

}