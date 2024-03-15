package io.github.zygzaggaming.hearty.mod;

import com.mojang.logging.LogUtils;
import io.github.zygzaggaming.hearty.api.HalfHeartLayer;
import io.github.zygzaggaming.hearty.api.HeartLayer;
import io.github.zygzaggaming.hearty.api.HeartType;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(HeartyMain.MODID)
public class HeartyMain {
    public static final String MODID = "hearty";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Registry<HalfHeartLayer> HALF_HEART_LAYER_REGISTRY = DeferredRegister.create(HeartyRegistries.HALF_HEART_LAYER_KEY, MODID).makeRegistry((builder) -> {});
    public static Registry<HeartLayer> HEART_LAYER_REGISTRY = DeferredRegister.create(HeartyRegistries.HEART_LAYER_KEY, MODID).makeRegistry((builder) -> {});
    public static Registry<HeartType> HEART_TYPE_REGISTRY = DeferredRegister.create(HeartyRegistries.HEART_TYPE_KEY, MODID).makeRegistry((builder) -> {});

    public HeartyMain(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);

        HeartTypeRegistry.REGISTER.register(modEventBus);
        HeartLayerRegistry.REGISTER.register(modEventBus);
        HalfHeartLayerRegistry.REGISTER.register(modEventBus);
        modEventBus.addListener((NewRegistryEvent evt) -> {
            evt.register(HEART_TYPE_REGISTRY);
            evt.register(HEART_LAYER_REGISTRY);
            evt.register(HALF_HEART_LAYER_REGISTRY);
        });

        HeartyConfig.register();
    }

    public void commonSetup(final FMLCommonSetupEvent event) {}

    public static ResourceLocation loc(String string) {
        return new ResourceLocation(MODID, string);
    }
}
