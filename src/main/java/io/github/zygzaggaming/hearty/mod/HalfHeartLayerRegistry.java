package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.HalfHeartContext;
import io.github.zygzaggaming.hearty.api.HalfHeartLayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HalfHeartLayerRegistry {
    public static final ResourceKey<Registry<HalfHeartLayer>> HALF_HEART_LAYER_KEY =
            ResourceKey.createRegistryKey(HeartyMain.loc("half_heart_layer"));
    public static final DeferredRegister<HalfHeartLayer> REGISTER =
            DeferredRegister.create(HALF_HEART_LAYER_KEY, HeartyMain.MODID);

    public static final Supplier<HalfHeartLayer> FROZEN = register(
            "frozen",
            1,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().isFullyFrozen() ?
                            ctx.withSprite(new ResourceLocation(ctx.sprite().toString().replace("full", "frozen"))) : ctx
    );
    public static final Supplier<HalfHeartLayer> POISON = register(
            "poison",
            2,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().hasEffect(MobEffects.POISON) ?
                            ctx.withSprite(new ResourceLocation(ctx.sprite().toString().replace("full", "poisoned"))) : ctx
    );
    public static final Supplier<HalfHeartLayer> WITHER = register(
            "wither",
            3,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().hasEffect(MobEffects.WITHER) ?
                            ctx.withSprite(new ResourceLocation(ctx.sprite().toString().replace("full", "withered"))) : ctx
    );
    public static final Supplier<HalfHeartLayer> HARDCORE = register(
            "hardcore",
            4,
            ctx -> ctx.level().getLevelData().isHardcore() && (ctx.heartType() == HeartTypeRegistry.HEALTH.get() || ctx.heartType() == HeartTypeRegistry.ABSORPTION.get()) ?
                            ctx.withSprite(new ResourceLocation(ctx.sprite() + "_hardcore")) : ctx
    );
    public static final Supplier<HalfHeartLayer> BLINK = register(
            "blink",
            5,
            ctx -> ctx.gui().healthBlinkTime > (long) ctx.gui().tickCount && (ctx.gui().healthBlinkTime - (long) ctx.gui().tickCount) / 3 % 2 == 1 ?
                            ctx.withSprite(new ResourceLocation(ctx.sprite() + "_blinking")) : ctx
    );

    public static Supplier<HalfHeartLayer> register(String id, double priority, Function<HalfHeartContext, HalfHeartContext> apply) {
        return REGISTER.register(id, () -> new HalfHeartLayer(HeartyMain.loc(id), priority) {
            @Override
            public HalfHeartContext apply(HalfHeartContext ctx) {
                return apply.apply(ctx);
            }
        });
    }
}
