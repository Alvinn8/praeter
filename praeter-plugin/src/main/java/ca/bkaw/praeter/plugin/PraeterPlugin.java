package ca.bkaw.praeter.plugin;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.PacksHolder;
import ca.bkaw.praeter.core.resources.ResourceEventListener;
import ca.bkaw.praeter.core.resources.ResourceManager;
import ca.bkaw.praeter.core.resources.ResourcePacksHolder;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.pack.VanillaAssets;
import ca.bkaw.praeter.core.resources.pack.collision.ResourceCollisionException;
import ca.bkaw.praeter.core.resources.send.HttpServerResourcePackSender;
import ca.bkaw.praeter.gui.GuiEventListener;
import ca.bkaw.praeter.gui.PraeterGui;
import ca.bkaw.praeter.plugin.test.TestGui;
import ca.bkaw.praeter.plugin.test.TestingCommand;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The plugin that loads the praeter classes into bukkit.
 */
public class PraeterPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Praeter.get().setLogger(this.getLogger());
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        resourceManager.setResourcePacksFolder(
            this.getDataFolder().toPath().resolve("internal/resourcepacks")
        );

        this.setupDirectories();
        this.setupMainResourcePack();
        this.setupVanillaAssets();
        this.includePluginAssets();
        this.setupResourcePackSender();

        // Bake the packs right before startup, after plugins have loaded
        this.getServer().getScheduler().runTaskLater(this, this::bakePacks, 1L);

        // Register event listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new GuiEventListener(), this);
        pluginManager.registerEvents(new ResourceEventListener(resourceManager, this), this);

        // Testing
        // Register testing command
        Objects.requireNonNull(this.getCommand("praetertest")).setExecutor(
            new TestingCommand()
        );

        PraeterGui.get().getGuiRegistry().register(TestGui.TYPE,
            new NamespacedKey("praetertest", "test1"),
            this);
    }

    /**
     * Create directories.
     */
    private void setupDirectories() {
        try {
            Files.createDirectories(Praeter.get().getResourceManager().getResourcePacksFolder());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories.", e);
        }
    }

    /**
     * Set up the main resource pack.
     */
    private void setupMainResourcePack() {
        this.getLogger().info("Setting up resource packs");
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        Path resourcePacksFolder = Praeter.get().getResourceManager().getResourcePacksFolder();
        Path path = resourcePacksFolder.resolve("main.zip");
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

    /**
     * Set up the vanilla assets.
     */
    private void setupVanillaAssets() {
        // TODO only do this setup if praeter-resources is actually used?
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        Path resourcePacksFolder = Praeter.get().getResourceManager().getResourcePacksFolder();
        Path vanillaAssetsPath = resourcePacksFolder.resolve("vanilla.zip");
        try {
            resourceManager.getPacks().setVanillaAssets(VanillaAssets.readOrExtract(vanillaAssetsPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or extract vanilla assets.", e);
        }
    }

    /**
     * Include assets from plugins into the resource packs they affect.
     *
     * @see ResourceManager#getResourcePacks(Plugin)
     */
    private void includePluginAssets() {
        this.getLogger().info("Including plugin assets");
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            if (plugin != this) continue; // TODO
            ResourcePack pluginAssets;
            try {
                Path jarPath = Path.of(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                pluginAssets = ResourcePack.loadZip(jarPath);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Failed to open plugin jar file.", e);
            }
            for (ResourcePack resourcePack : resourceManager.getResourcePacks(plugin)) {
                System.out.println("resourcePack = " + resourcePack);
                try {
                    resourcePack.include(pluginAssets, path -> path.startsWith("assets/"));
                } catch (ResourceCollisionException | IOException e) {
                    getLogger().severe("Failed to include assets from " + plugin.getName()
                            + " into resource pack " + resourceManager.getPacks().getId(resourcePack));
                    e.printStackTrace();
                    // Break out of this loop (continue to next plugin)
                    break;
                }
            }
            try {
                pluginAssets.getRoot().getFileSystem().close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close plugin jar file.", e);
            }
        }
    }

    /**
     * Set up the {@link ca.bkaw.praeter.core.resources.send.ResourcePackSender}.
     */
    private void setupResourcePackSender() {
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        try {
            resourceManager.setResourcePackSender(new HttpServerResourcePackSender());
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server for sending resource packs.", e);
        }
    }

    /**
     * Bake the resource packs.
     * <p>
     * After this method is called, packs will no longer be accessible from the
     * resource manager and baked packs will become available.
     */
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
