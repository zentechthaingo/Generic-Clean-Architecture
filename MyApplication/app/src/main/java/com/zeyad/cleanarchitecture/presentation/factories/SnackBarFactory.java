package com.grability.rappitendero.presentation.factories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.grability.rappitendero.R;
import com.grability.rappitendero.presentation.components.widgets.ColoredSnackbar;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SnackBarFactory {

    @Retention(SOURCE)
    @StringDef({TYPE_INFO, TYPE_ERROR})
    public @interface SnackBarType {
    }

    public static final String TYPE_INFO = "type_info";

    public static final String TYPE_ERROR = "type_error";

    public static Snackbar getSnackBar(@SnackBarType String snackBarType, @NonNull View view, @StringRes int stringId, int duration) {
        Snackbar snackbar = Snackbar.make(view, stringId, duration);
        return createSnackBar(snackBarType, snackbar, view.getContext());
    }

    public static Snackbar getSnackBar(@SnackBarType String snackBarType, @NonNull View view, @NonNull CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        return createSnackBar(snackBarType, snackbar, view.getContext());
    }

    private static Snackbar createSnackBar(@SnackBarType String snackBarType, Snackbar snackbar, Context context) {
        switch (snackBarType) {
            case TYPE_INFO:
                return ColoredSnackbar.info(snackbar, ContextCompat.getColor(context, R.color.rappi_green));
            case TYPE_ERROR:
                return ColoredSnackbar.error(snackbar, ContextCompat.getColor(context, R.color.rappi_red));
            default:
                return snackbar;
        }
    }
}