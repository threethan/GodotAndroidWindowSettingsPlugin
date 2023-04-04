package com.threethan.godot.window;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

import java.util.Arrays;
import java.util.List;

public class GodotAndroidWindowSettings extends GodotPlugin {
    private Window window;
    private Activity activity;

    // Vars used to reset when setTransparentBars(false)
    private int statusBarColor = Color.BLACK;
    private int navigationBarColor = Color.BLACK;
    private boolean enableCustomBarColors = false;


    // Godot setup
    public GodotAndroidWindowSettings(Godot godot) {
        super(godot);
        window = godot.getActivity().getWindow();
        activity = godot.getActivity();
        // Vars used to reset when setTransparentBars(false)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor = window.getStatusBarColor();
        navigationBarColor = window.getNavigationBarColor();
        enableCustomBarColors = getEnableCustomBarColors();
    }
    @NonNull
    @Override
    public String getPluginName() {
        return "GodotAndroidWindowSettings";
    }
    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "setTransparentBars","getTransparentBars",
                "setEnableCustomBarColors","getEnableCustomBarColors",
                "setStatusBarColor","getStatusBarColor",
                "setNavigationBarColor","getNavigationBarColor",
                "setLightStatusBar","getLightStatusBar",
                "setLightNavigationBar","getLightNavigationBar",
                "setImmersive","getImmersive",
                "setStatusBarVisible",
                "setNavigationBarVisible",
                "setCutoutVisible",
                "getInsetTop", "getInsetBottom", "getInsetLeft", "getInsetRight",
                "getApiLevel"
        );
    }

    // Quick and easy way to set bars to be transparent
    public void setTransparentBars(final boolean transparentBars) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (transparentBars) {
                    window.setStatusBarColor(Color.TRANSPARENT);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else {
                    window.setStatusBarColor(statusBarColor);
                    window.setNavigationBarColor(navigationBarColor);
                    setEnableCustomBarColors(enableCustomBarColors);
                }
            }
        });
    }
    public boolean getTransparentBars() {
        return window.getStatusBarColor() == Color.TRANSPARENT
                && window.getNavigationBarColor() == Color.TRANSPARENT
                && getEnableCustomBarColors();
    }

    // Sets immersive mode
    public void setImmersive(final boolean immersive) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (immersive) {
                        window.getInsetsController().hide(WindowInsets.Type.navigationBars());
                        window.getInsetsController().hide(WindowInsets.Type.statusBars());
                        window.getInsetsController().hide(WindowInsets.Type.displayCutout());
                    } else {
                        window.getInsetsController().show(WindowInsets.Type.navigationBars());
                        window.getInsetsController().show(WindowInsets.Type.statusBars());
                        window.getInsetsController().show(WindowInsets.Type.displayCutout());
                    }
                } else {
                    if (immersive) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
                    } else {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }
        });
    }
    public boolean getImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !window.getDecorView().getRootWindowInsets().hasInsets();
        } else {
            return window.getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_IMMERSIVE ||
                    window.getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
    }
    // API 30+ Inset controller tweaks
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void setStatusBarVisible(final boolean visible) {
        activity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {
                if (visible) {
                    window.getInsetsController().show(WindowInsets.Type.statusBars());
                } else {
                    window.getInsetsController().hide(WindowInsets.Type.statusBars());
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void setNavigationBarVisible(final boolean visible) {
        activity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {
                if (visible) {
                    window.getInsetsController().show(WindowInsets.Type.navigationBars());
                } else {
                    window.getInsetsController().hide(WindowInsets.Type.navigationBars());
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void setCutoutVisible(final boolean visible) {
        activity.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {
                if (visible) {
                    window.getInsetsController().show(WindowInsets.Type.displayCutout());
                } else {
                    window.getInsetsController().hide(WindowInsets.Type.displayCutout());
                }
            }
        });
    }

    // Enable or disable custom bars, by toggling the flag to let window draw system bar backgrounds
    public void setEnableCustomBarColors(final boolean newEnableCustomBars) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableCustomBarColors = newEnableCustomBars;
                if (newEnableCustomBars) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                }
            }
        });
    }
    public boolean getEnableCustomBarColors() {
        return hasBit(window.getAttributes().flags, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
    // Status Bar Color
    public void setStatusBarColor(final String newColorString) {
        int newStatusBarColor = Color.parseColor(newColorString);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusBarColor = newStatusBarColor;
                window.setStatusBarColor(newStatusBarColor);
            }
        });
    }
    public int getStatusBarColor() {
        return window.getStatusBarColor();
    }
    // Navigation Bar Color
    public void setNavigationBarColor(final String newColorString) {
        int newNavigationBarColor = Color.parseColor(newColorString);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationBarColor = newNavigationBarColor;
                window.setNavigationBarColor(newNavigationBarColor);
            }
        });
    }
    public int getNavigationBarColor() {
        return window.getNavigationBarColor();
    }
    //Light status and navigation bars (with insets controller)
    public void setLightStatusBar(final boolean lightStatusBarBar) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (lightStatusBarBar) {
                        window.getInsetsController().setSystemBarsAppearance(
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                    } else {
                        window.getInsetsController().setSystemBarsAppearance(
                                0,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                    }
                } else {
                    View decorView = window.getDecorView();
                    int systemUiVisibility = decorView.getSystemUiVisibility();
                    if (lightStatusBarBar) {
                        systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                    decorView.setSystemUiVisibility(systemUiVisibility);
                }
            }
        });
    }
    public boolean getLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return hasBit(window.getInsetsController().getSystemBarsAppearance(), WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else {
            return hasBit(window.getDecorView().getSystemUiVisibility(), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public void setLightNavigationBar(final boolean lightNavigationBar) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (lightNavigationBar) {
                        window.getInsetsController().setSystemBarsAppearance(
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
                    } else {
                        window.getInsetsController().setSystemBarsAppearance(
                                0,
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
                    }
                } else {
                    View decorView = window.getDecorView();
                    int systemUiVisibility = decorView.getSystemUiVisibility();
                    if (lightNavigationBar) {
                        systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    } else {
                        systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    }
                    decorView.setSystemUiVisibility(systemUiVisibility);
                }
            }
        });
    }
    public boolean getLightNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return hasBit(window.getInsetsController().getSystemBarsAppearance(), WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
        } else {
            return hasBit(window.getDecorView().getSystemUiVisibility(), View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public float getInsetTop() {
        return window.getDecorView().getRootWindowInsets().getStableInsetTop();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public float getInsetBottom() {
        return window.getDecorView().getRootWindowInsets().getStableInsetBottom();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public float getInsetLeft() {
        return window.getDecorView().getRootWindowInsets().getStableInsetLeft();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public float getInsetRight() {
        return window.getDecorView().getRootWindowInsets().getStableInsetRight();
    }
    public int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }
    // Helper
    private boolean hasBit(final int bitmask, final int bit) {
        return (bit & bitmask) == bit;
    }
}
