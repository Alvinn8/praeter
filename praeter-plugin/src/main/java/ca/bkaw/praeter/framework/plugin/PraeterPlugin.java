package ca.bkaw.praeter.framework.plugin;

import ca.bkaw.praeter.framework.plugin.test.TestingCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin that loads the praeter classes into bukkit.
 */
public class PraeterPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("praetertest").setExecutor(new TestingCommand());
    }
}
