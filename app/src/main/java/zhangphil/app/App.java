package zhangphil.app;

import android.app.Application;

import me.kareluo.imaging.core.util.TUIUtils;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        TUIUtils.getInstance().init(this);
    }

    public static App getInstance() {
        return instance;
    }
}
