package br.com.movies_tek.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import br.com.movies_tek.ui.details.view.PosterLoadListener;


public class BindingAdapters {

    private BindingAdapters() {
    }

    @BindingAdapter({"colorScheme"})
    public static void setColorScheme(SwipeRefreshLayout view, int[] colorScheme) {
        view.setColorSchemeColors(colorScheme);
    }

    @BindingAdapter({"backdropUrl"})
    public static void loadBackdrop(ImageView view, String backdropUrl) {
        Glide.with(view.getContext())
                .load(backdropUrl)
                .into(view);
    }

    @BindingAdapter({"imageUrl", "fallback"})
    public static void loadMovieImage(ImageView view, String imageUrl, Drawable fallback) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .error(fallback)
                .crossFade()
                .into(view);
    }

    @BindingAdapter({"poster", "listener", "fallback"})
    public static void loadPosterWithListener(ImageView view,
                                              String imageUrl,
                                              final PosterLoadListener listener,
                                              Drawable fallback) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .error(fallback)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        listener.onPosterLoaded();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        listener.onPosterLoaded();
                        return false;
                    }
                })
                .into(view);
    }
}
