package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.HeartUnitContext;
import io.github.zygzaggaming.hearty.api.HeartUnitLayer;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HeartUnitLayerRegistry {
    public static final DeferredRegister<HeartUnitLayer> REGISTER =
            DeferredRegister.create(HeartyRegistries.HEART_UNIT_LAYER_KEY, HeartyMain.MODID);

    public static final Supplier<HeartUnitLayer> FROZEN = register(
            "frozen",
            1,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().isFullyFrozen() ?
                            ctx.withSprite(ResourceLocation.parse(ctx.sprite().toString().replace("full", "frozen"))) : ctx
    );
    public static final Supplier<HeartUnitLayer> POISON = register(
            "poison",
            2,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().hasEffect(MobEffects.POISON) ?
                            ctx.withSprite(ResourceLocation.parse(ctx.sprite().toString().replace("full", "poisoned"))) : ctx
    );
    public static final Supplier<HeartUnitLayer> WITHER = register(
            "wither",
            3,
            ctx -> ctx.heartType() == HeartTypeRegistry.HEALTH.get() && ctx.player().hasEffect(MobEffects.WITHER) ?
                            ctx.withSprite(ResourceLocation.parse(ctx.sprite().toString().replace("full", "withered"))) : ctx
    );
    public static final Supplier<HeartUnitLayer> HARDCORE = register(
            "hardcore",
            4,
            ctx -> ctx.level().getLevelData().isHardcore() && (ctx.heartType() == HeartTypeRegistry.HEALTH.get() || ctx.heartType() == HeartTypeRegistry.ABSORPTION.get()) ?
                            ctx.withSprite(ResourceLocation.parse(ctx.sprite() + "_hardcore")) : ctx
    );
    public static final Supplier<HeartUnitLayer> BLINK = register(
            "blink",
            5,
            ctx -> ctx.gui().healthBlinkTime > (long) ctx.gui().tickCount && (ctx.gui().healthBlinkTime - (long) ctx.gui().tickCount) / 3 % 2 == 1 ?
                            ctx.withSprite(ResourceLocation.parse(ctx.sprite() + "_blinking")) : ctx
    );

    public static Supplier<HeartUnitLayer> register(String id, double priority, Function<HeartUnitContext, HeartUnitContext> apply) {
        return REGISTER.register(id, () -> new HeartUnitLayer(HeartyMain.loc(id), priority) {
            @Override
            public HeartUnitContext apply(HeartUnitContext ctx) {
                return apply.apply(ctx);
            }
        });
    }
}
