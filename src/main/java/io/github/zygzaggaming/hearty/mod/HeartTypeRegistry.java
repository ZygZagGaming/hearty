package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.GuiContext;
import io.github.zygzaggaming.hearty.api.HeartType;
import io.github.zygzaggaming.hearty.api.HeartyRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HeartTypeRegistry {
    public static final DeferredRegister<HeartType> REGISTER =
            DeferredRegister.create(HeartyRegistries.HEART_TYPE_KEY, HeartyMain.MODID);

    public static final Supplier<HeartType> HEALTH = registerBasic(
            "health",
            1,
            ctx -> Mth.ceil(ctx.player().getHealth()),
            HeartyMain.loc("hud/hearts/vanilla/full")
    );
    public static final Supplier<HeartType> EMPTY = registerBasic(
            "empty",
            2,
            ctx -> Mth.ceil(ctx.player().getMaxHealth()) - Mth.ceil(ctx.player().getHealth()),
            HeartyMain.loc("hud/hearts/vanilla/empty")
    );
    public static final Supplier<HeartType> ABSORPTION = registerBasic(
            "absorption",
            3,
            ctx -> Mth.ceil(ctx.player().getAbsorptionAmount()),
            HeartyMain.loc("hud/hearts/vanilla/absorption")
    );

    public static Supplier<HeartType> registerBasic(String id, double priority, Function<GuiContext, Integer> getNumber, ResourceLocation texture) {
        return REGISTER.register(id, () -> HeartType.basic(
                HeartyMain.loc(id),
                priority,
                getNumber,
                texture
        ));
    }
}