package ca.bkaw.praeter.plugin;

import ca.bkaw.praeter.core.ItemUtils;
import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.PraeterPlugin;
import ca.bkaw.praeter.core.resources.CustomModelDataStore;
import ca.bkaw.praeter.core.resources.PacksHolder;
import ca.bkaw.praeter.core.resources.ResourceEventListener;
import ca.bkaw.praeter.core.resources.ResourceManager;
import ca.bkaw.praeter.core.resources.ResourcePacksHolder;
import ca.bkaw.praeter.core.resources.apply.DefaultResourcePackApplier;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.JsonResource;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.pack.VanillaAssets;
import ca.bkaw.praeter.core.resources.pack.collision.ResourceCollisionException;
import ca.bkaw.praeter.core.resources.send.BuiltInTcpResourcePackSender;
import ca.bkaw.praeter.core.resources.send.HttpServerResourcePackSender;
import ca.bkaw.praeter.gui.GuiEventListener;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The plugin that loads the praeter classes into bukkit.
 */
public class PraeterImplPlugin extends JavaPlugin implements PraeterPlugin {
    /**
     * The directory that contains files that praeter needs to run. The name internal
     * indicates to the server admins that the folder should not be modified.
     */
    private final Path internalDirectory = this.getDataFolder().toPath().resolve("internal");

    @Override
    public void onEnable() {
        Praeter.get().setLogger(this.getLogger());
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        resourceManager.setResourcePacksFolder(
            this.internalDirectory.resolve("resourcepacks")
        );

        this.setupDirectories();
        this.setupMainResourcePack();
        this.setupVanillaAssets();
        this.includePluginAssets();
        this.setupResourcePackSender();

        // Bake the packs right before startup, after plugins have loaded
        this.getServer().getScheduler().runTaskLater(this, this::bakePacks, 1L);

        DefaultResourcePackApplier resourcePackApplier = new DefaultResourcePackApplier(resourceManager, this);
        resourceManager.setResourcePackApplier(resourcePackApplier);

        // Register event listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new GuiEventListener(this), this);
        pluginManager.registerEvents(new ResourceEventListener(resourceManager), this);
    }

    @Override
    public void onDisable() {
        Praeter.get().getResourceManager().getResourcePackSender().remove();
    }

    @Override
    public boolean isEnabledIn(World world) {
        // Praeter includes some common assets, like a transparent texture and model,
        // into all packs.
        return true;
    }

    @Override
    public void onIncludeAssets(ResourcePack resourcePack) {
        // Create the transparent item
        try {
            // Create the image
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Path texturePath = resourcePack.getTexturePath(ItemUtils.TRANSPARENT_ITEM);
            Files.createDirectories(texturePath.getParent());
            try (OutputStream stream = Files.newOutputStream(texturePath)) {
                ImageIO.write(image, "png", stream);
            }

            // Create the model
            JsonObject json = new JsonObject();
            json.addProperty("parent", "item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", ItemUtils.TRANSPARENT_ITEM.toString());
            json.add("textures", textures);

            // Save the model
            Path modelPath = resourcePack.getModelPath(ItemUtils.TRANSPARENT_ITEM);
            JsonResource resource = new JsonResource(resourcePack, modelPath, json);
            resource.save();

            // Add the redirect
            NamespacedKey vanillaModel = NamespacedKey.minecraft("item/paper");
            resourcePack.addCustomModelData(vanillaModel, ItemUtils.TRANSPARENT_ITEM);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create transparent item.", e);
        }
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
        Path storePath = this.internalDirectory.resolve("customModelData.json");
        CustomModelDataStore customModelDataStore;
        try {
            customModelDataStore = new CustomModelDataStore(storePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read custom model data store.", e);
        }
        ResourcePacksHolder holder = new ResourcePacksHolder(mainResourcePack, customModelDataStore);
        resourceManager.setPacks(holder);
    }

    /**
     * Set up the vanilla assets.
     */
    private void setupVanillaAssets() {
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
     * Include assets from plugins that implement {@link PraeterPlugin} into the
     * resource packs they affect.
     *
     * @see ResourceManager#getResourcePacks(Plugin)
     */
    private void includePluginAssets() {
        this.getLogger().info("Including plugin assets");
        ResourceManager resourceManager = Praeter.get().getResourceManager();
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            if (!(plugin instanceof PraeterPlugin praeterPlugin)) {
                continue;
            }
            if (!plugin.isEnabled()) {
                continue;
            }

            // Read the assets
            ResourcePack pluginAssets;
            try {
                Path jarPath = Path.of(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                pluginAssets = ResourcePack.loadZip(jarPath);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Failed to open plugin jar file.", e);
            }

            // Include assets into resource packs
            for (ResourcePack resourcePack : resourceManager.getResourcePacks(plugin)) {
                try {
                    resourcePack.include(pluginAssets, path -> path.startsWith("assets/"));
                } catch (ResourceCollisionException | IOException e) {
                    getLogger().severe("Failed to include assets from " + plugin.getName()
                            + " into resource pack " + resourceManager.getPacks().getId(resourcePack));
                    e.printStackTrace();
                    // Break out of this loop (continue to next plugin)
                    break;
                }
                try {
                    praeterPlugin.onIncludeAssets(resourcePack);
                } catch (Throwable e) {
                    getLogger().severe("Error in onIncludeAssets method from " + plugin.getName()
                        + " for resource pack " + resourceManager.getPacks().getId(resourcePack));
                    e.printStackTrace();
                    // Break out of this loop (continue to next plugin)
                    break;
                }
            }

            // Close assets
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
            resourceManager.setResourcePackSender(new BuiltInTcpResourcePackSender());
        } catch (ReflectiveOperationException e) {
            this.getLogger().info("Failed to set up built in TCP resource pack sender, using an external HTTP server instead.");
            e.printStackTrace();
            try {
                resourceManager.setResourcePackSender(new HttpServerResourcePackSender());
            } catch (IOException e2) {
                throw new RuntimeException("Failed to start HTTP server for sending resource packs.", e2);
            }
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

        // Save custom model data store
        try {
            resourceManager.getPacks().getCustomModelDataStore().save();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save custom model data store.", e);
        }

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

        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof PraeterPlugin praeterPlugin) {
                try {
                    praeterPlugin.onPacksBaked();
                } catch (Throwable e) {
                    getLogger().severe("Exception in onPacksBaked for plugin " + plugin.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}
