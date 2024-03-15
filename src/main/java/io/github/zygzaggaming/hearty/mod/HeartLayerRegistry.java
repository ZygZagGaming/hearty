package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.HeartContext;
import io.github.zygzaggaming.hearty.api.HeartLayer;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HeartLayerRegistry {
    public static final DeferredRegister<HeartLayer> REGISTER =
            DeferredRegister.create(HeartyRegistries.HEART_LAYER_KEY, HeartyMain.MODID);

    public static final Supplier<HeartLayer> HEART_WIGGLE = register(
            "heart_wiggle",
            1.0,
            ctx -> {
                int y = ctx.y();
                if (ctx.player().getHealth() + ctx.player().getAbsorptionAmount() <= 4) y += ctx.gui().random.nextInt(2);
                if (ctx.heartIndex() == ctx.regenHeartWiggle() || ctx.heartIndex() == ctx.regenHeartWiggle() - 1) y -= 2;
                return ctx.withY(y);
            }
    );

    public static Supplier<HeartLayer> register(String id, double priority, Function<HeartContext, HeartContext> apply) {
        return REGISTER.register(id, () -> new HeartLayer(HeartyMain.loc(id), priority) {
            @Override
            public HeartContext apply(HeartContext ctx) {
                return apply.apply(ctx);
            }
        });
    }
}
