package com.ivan_prokofyev.currencies.support;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;

/**
 * Created by Prokofyev Ivan on 05.08.16.
 */
public class FontManager {
    Context ctx;
    private String fontPathRegular;
    private String fontPathBold;
    private String fontPathMediumItalic;
    private String fontPathBlack;
    public Typeface typefaceReg;
    public Typeface typefaceBold;
    public Typeface typefaceMI;
    public Typeface typefaceBlack;

    public FontManager(Context ctx) {
        this.ctx = ctx;
        loadFonts();
    }

    private void loadFonts() {
        fontPathRegular = "fonts/Lato-Regular.ttf";
        fontPathBold = "fonts/Lato-Bold.ttf";
        fontPathMediumItalic = "fonts/Lato-MediumItalic.ttf";
        fontPathBlack = "fonts/Lato-Black.ttf";

        typefaceReg = Typeface.createFromAsset(ctx.getResources().getAssets(), fontPathRegular);
        typefaceBold = Typeface.createFromAsset(ctx.getResources().getAssets(), fontPathBold);
        typefaceMI = Typeface.createFromAsset(ctx.getResources().getAssets(), fontPathMediumItalic);
        typefaceBlack = Typeface.createFromAsset(ctx.getResources().getAssets(), fontPathBlack);
    }
}
