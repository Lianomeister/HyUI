package au.ellie.hyui;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class HyUIPlugin extends JavaPlugin {

    private static HyUIPluginLogger instance;
    
    public static HyUIPluginLogger getLog() {
        if (instance == null)
            instance = new HyUIPluginLogger();
        return instance;
    }

    public HyUIPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        if (instance == null)
            instance = new HyUIPluginLogger();
    }

    @Override
    protected void setup() {
        if (HyUIPluginLogger.LOGGING_ENABLED) {
            instance.logInfo("Setting up plugin " + this.getName());
            this.getCommandRegistry().registerCommand(new au.ellie.hyui.commands.HyUITestGuiCommand());
        }
    }
}
