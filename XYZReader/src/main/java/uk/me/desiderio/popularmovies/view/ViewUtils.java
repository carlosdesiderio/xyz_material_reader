package uk.me.desiderio.popularmovies.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.xyzreader.R;

import uk.me.desiderio.popularmovies.network.ConnectivityUtils.ConnectivityState;

import static uk.me.desiderio.popularmovies.network.ConnectivityUtils.CONNECTED;
import static uk.me.desiderio.popularmovies.network.ConnectivityUtils.DISCONNECTED;

/**
 * View utility class to generate {@link Snackbar }
 * Reused code from the Popular Movies Project
 */

public class ViewUtils {

    /**
     * returns instance of {@link Snackbar}
     * the bar is customised depending on the connectivity state provided as parameter
     */
    @Nullable
    public static Snackbar getSnackbar(@ConnectivityState int connectivityState, @NonNull View
            anchorView) {
        Context context = anchorView.getContext();
        String message;
        int duration = Snackbar.LENGTH_SHORT;

        switch (connectivityState) {
            case CONNECTED:
                message = context.getString(R.string.snackbar_connected_message);
                return Snackbar.make(anchorView, message, duration);
            case DISCONNECTED:
                message = context.getString(R.string.snackbar_no_connection_message);
                return Snackbar.make(anchorView, message, duration);
            default:
                return null;
        }

    }
}