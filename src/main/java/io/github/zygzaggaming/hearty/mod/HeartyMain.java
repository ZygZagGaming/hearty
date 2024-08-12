package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.HalfHeartLayer;
import io.github.zygzaggaming.hearty.api.HeartLayer;
import io.github.zygzaggaming.hearty.api.HeartType;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(HeartyMain.MODID)
public class HeartyMain {
    public static final String MODID = "hearty";
    //public static final Logger LOGGER = LogUtils.getLogger();

    public static final Registry<HalfHeartLayer> HALF_HEART_LAYER_REGISTRY = DeferredRegister.create(HeartyRegistries.HALF_HEART_LAYER_KEY, MODID).makeRegistry((builder) -> {});
    public static Registry<HeartLayer> HEART_LAYER_REGISTRY = DeferredRegister.create(HeartyRegistries.HEART_LAYER_KEY, MODID).makeRegistry((builder) -> {});
    public static Registry<HeartType> HEART_TYPE_REGISTRY = DeferredRegister.create(HeartyRegistries.HEART_TYPE_KEY, MODID).makeRegistry((builder) -> {});

    public HeartyMain(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            HeartTypeRegistry.REGISTER.register(modEventBus);
            HeartLayerRegistry.REGISTER.register(modEventBus);
            HalfHeartLayerRegistry.REGISTER.register(modEventBus);
            modEventBus.addListener((NewRegistryEvent evt) -> {
                evt.register(HEART_TYPE_REGISTRY);
                evt.register(HEART_LAYER_REGISTRY);
                evt.register(HALF_HEART_LAYER_REGISTRY);
            });

            modEventBus.addListener((RegisterGuiLayersEvent event) -> event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, ResourceLocation.fromNamespaceAndPath(MODID, "hearty_health"), new HeartyGuiLayer()));

            NeoForge.EVENT_BUS.addListener((RenderGuiLayerEvent.Pre event) -> {
                if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) event.setCanceled(true);
            });

            modContainer.registerConfig(ModConfig.Type.CLIENT, HeartyConfig.SPEC);
        }
    }

    public static ResourceLocation loc(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }
}
