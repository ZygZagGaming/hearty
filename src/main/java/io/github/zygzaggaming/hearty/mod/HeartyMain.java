package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.HalfHeartLayer;
import io.github.zygzaggaming.hearty.api.HeartLayer;
import io.github.zygzaggaming.hearty.api.HeartType;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
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

    public HeartyMain(IEventBus modEventBus) {
        if (FMLEnvironment.dist.isClient()) {
            HeartTypeRegistry.REGISTER.register(modEventBus);
            HeartLayerRegistry.REGISTER.register(modEventBus);
            HalfHeartLayerRegistry.REGISTER.register(modEventBus);
            modEventBus.addListener((NewRegistryEvent evt) -> {
                evt.register(HEART_TYPE_REGISTRY);
                evt.register(HEART_LAYER_REGISTRY);
                evt.register(HALF_HEART_LAYER_REGISTRY);
            });

            modEventBus.addListener((RegisterGuiOverlaysEvent event) -> event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), new ResourceLocation(MODID, "hearty_health"), new HeartyGuiOverlay()));

            NeoForge.EVENT_BUS.addListener((RenderGuiOverlayEvent.Pre event) -> {
                if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) event.setCanceled(true);
            });

            HeartyConfig.register();
        }
    }

    public static ResourceLocation loc(String string) {
        return new ResourceLocation(MODID, string);
    }
}
