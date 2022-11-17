package ca.bkaw.praeter.plugin;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.PacksHolder;
import ca.bkaw.praeter.core.resources.ResourceEventListener;
import ca.bkaw.praeter.core.resources.ResourceManager;
import ca.bkaw.praeter.core.resources.ResourcePacksHolder;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.pack.VanillaAssets;
import ca.bkaw.praeter.core.resources.send.HttpServerResourcePackSender;
import ca.bkaw.praeter.gui.GuiEventListener;
import ca.bkaw.praeter.plugin.test.TestGui;
import ca.bkaw.praeter.plugin.test.TestingCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The plugin that loads the praeter classes into bukkit.
 */
public class PraeterPlugin extends JavaPlugin {
    private static PraeterPlugin instance;
    private final Path resourcePacksFolder = getDataFolder().toPath().resolve("internal/resourcepacks");

    /**
     * Get the {@link PraeterPlugin} instance.
     *
     * @return The instance.
     */
    public static PraeterPlugin get() {
        if (instance == null) {
            throw new IllegalStateException("The praeter plugin has not been enabled yet.");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Praeter.get().setLogger(this.getLogger());

        this.setupDirectories();
        this.setupMainResourcePack();
        this.setupVanillaAssets();
        this.setupResourcePackSender();

        // Bake the packs right before startup, after plugins have loaded
        this.getServer().getScheduler().runTaskLater(this, this::bakePacks, 1L);

        // Register event listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new GuiEventListener(), this);
        pluginManager.registerEvents(new ResourceEventListener(Praeter.get().getResourceManager(), this), this);

        // Testing
        // Register testing command
        this.getCommand("praetertest").setExecutor(new TestingCommand());

        TestGui.TYPE.setPlugin(this);
        TestGui.TYPE.getRenderer().onSetup(TestGui.TYPE);
    }

    private void setupDirectories() {
        try {
            Files.createDirectories(this.resourcePacksFolder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories.", e);
        }
    }

    private void setupVanillaAssets() {
        // TODO only do this setup if praeter-resources is actually used?
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        Path vanillaAssetsPath = this.resourcePacksFolder.resolve("vanilla.zip");
        try {
            resourceManager.getPacks().setVanillaAssets(VanillaAssets.readOrExtract(vanillaAssetsPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or extract vanilla assets.", e);
        }
    }

    private void setupMainResourcePack() {
        this.getLogger().info("Setting up resource packs");
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        Path path = this.resourcePacksFolder.resolve("main.zip");
        ResourcePack mainResourcePack;
        try {
            Files.deleteIfExists(path);
            mainResourcePack = ResourcePack.loadZip(path);
            mainResourcePack.create("Praeter-managed resource pack");
        } catch (IOException e) {
            throw new RuntimeException("Failed to set up the main resource pack.", e);
        }
        ResourcePacksHolder holder = new ResourcePacksHolder(mainResourcePack);
        resourceManager.setPacks(holder);
    }

    private void setupResourcePackSender() {
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        try {
            resourceManager.setResourcePackSender(new HttpServerResourcePackSender());
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server for sending resource packs.", e);
        }
    }

    private void bakePacks() {
        this.getLogger().info("Baking and closing packs");
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        // Get the packs before removing the PacksHolder
        ResourcePack pack = resourceManager.getPacks().getMain();
        ResourcePack vanillaAssets = resourceManager.getPacks().getVanillaAssets();
        // Remove the PacksHolder for resource packs
        resourceManager.setPacks(null);

        try {
            vanillaAssets.getRoot().getFileSystem().close();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to close vanilla assets.", e);
        }

        BakedResourcePack baked;
        try {
            baked = BakedResourcePack.bake(pack);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to bake main resource pack.", e);
        }

        // Create the PacksHolder for baked resource packs
        resourceManager.setBakedPacks(new PacksHolder<>(baked));

        try {
            pack.getRoot().getFileSystem().close();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to close main resource pack.", e);
        }

        this.getLogger().info("All packs have been baked and closed.");
    }
}
