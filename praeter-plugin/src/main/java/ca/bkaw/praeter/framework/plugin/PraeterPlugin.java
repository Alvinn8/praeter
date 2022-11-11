package ca.bkaw.praeter.framework.plugin;

import ca.bkaw.praeter.framework.gui.GuiEventListener;
import ca.bkaw.praeter.framework.plugin.test.TestingCommand;
import ca.bkaw.praeter.framework.resources.PraeterResources;
import ca.bkaw.praeter.framework.resources.ResourceManager;
import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import ca.bkaw.praeter.framework.resources.pack.VanillaAssets;
import ca.bkaw.praeter.framework.resources.pack.send.HttpServerResourcePackSender;
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

        this.setupDirectories();
        this.setupVanillaAssets();
        this.setupMainResourcePack();
        this.setupResourcePackSender();

        // Bake the packs right before startup, after plugins have loaded
        this.getServer().getScheduler().runTaskLater(this, this::bakePacks, 1L);

        // Register testing command
        getCommand("praetertest").setExecutor(new TestingCommand());

        // Register event listener
        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);
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
        ResourceManager resourceManager = PraeterResources.get().getResourceManager();
        Path vanillaAssetsPath = this.resourcePacksFolder.resolve("vanilla.zip");
        try {
            resourceManager.setVanillaAssets(VanillaAssets.readOrExtract(vanillaAssetsPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or extract vanilla assets.", e);
        }
    }

    private void setupMainResourcePack() {
        ResourceManager resourceManager = PraeterResources.get().getResourceManager();
        Path path = this.resourcePacksFolder.resolve("main.zip");
        ResourcePack mainResourcePack;
        try {
            Files.deleteIfExists(path);
            mainResourcePack = ResourcePack.loadZip(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to set up the main resource pack.", e);
        }
        resourceManager.setMainResourcePack(mainResourcePack);
    }

    private void setupResourcePackSender() {
        ResourceManager resourceManager = PraeterResources.get().getResourceManager();
        try {
            resourceManager.setResourcePackSender(new HttpServerResourcePackSender());
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server for sending resource packs.", e);
        }
    }

    private void bakePacks() {
        ResourceManager resourceManager = PraeterResources.get().getResourceManager();
        ResourcePack pack = resourceManager.getMainResourcePack();
        resourceManager.setMainResourcePack(null);

        try {
            resourceManager.getVanillaAssets().getRoot().getFileSystem().close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close vanilla assets.", e);
        }
        resourceManager.setVanillaAssets(null);

        BakedResourcePack baked;
        try {
            baked = BakedResourcePack.bake(pack);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bake main resource pack.", e);
        }
        resourceManager.setMainBakedResourcePack(baked);

        try {
            pack.getRoot().getFileSystem().close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close main resource pack.", e);
        }
    }
}
