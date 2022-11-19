package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.core.Registry;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GuiRegistry extends Registry<CustomGuiType> {

    @Override
    public void register(@NotNull CustomGuiType customGuiType, @NotNull NamespacedKey id, @NotNull Plugin plugin) {
        super.register(customGuiType, id, plugin);

        customGuiType.setPlugin(plugin);
        try {
            customGuiType.getRenderer().onSetup(customGuiType);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to set up renderer for custom gui " + id, e);
        }
    }

}
