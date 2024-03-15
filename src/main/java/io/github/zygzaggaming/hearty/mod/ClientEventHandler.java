package io.github.zygzaggaming.hearty.mod;

import io.github.zygzaggaming.hearty.api.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.TickEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = HeartyMain.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) { }

    @SubscribeEvent
    public static void preOverlayRenderEvent(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        GuiContext ctx = new GuiContext(mc, mc.gui.getCameraPlayer(), mc.gui, event.getGuiGraphics());
        if (event.getOverlay().id().getPath().equals("player_health") && ctx.shouldShowHealth()) {
            renderPlayerHealth(ctx);
            event.setCanceled(true);
        }
    }

    public static void renderPlayerHealth(GuiContext ctx) {
        Minecraft mc = ctx.minecraft();
        Gui gui = ctx.gui();
        Player player = ctx.player();
        GuiGraphics graphics = ctx.graphics();
        if (player != null) {
            int ceilHealth = Mth.ceil(player.getHealth());
            boolean isHealthBlinking = gui.healthBlinkTime > (long) gui.tickCount && (gui.healthBlinkTime - (long) gui.tickCount) / 3L % 2L == 1L;
            long t = Util.getMillis();
            if (ceilHealth < gui.lastHealth && player.invulnerableTime > 0) {
                gui.lastHealthTime = t;
                gui.healthBlinkTime = gui.tickCount + 20;
            } else if (ceilHealth > gui.lastHealth && player.invulnerableTime > 0) {
                gui.lastHealthTime = t;
                gui.healthBlinkTime = gui.tickCount + 10;
            }

            if (t - gui.lastHealthTime > 1000L) {
                gui.displayHealth = ceilHealth;
                gui.lastHealthTime = t;
            }

            gui.lastHealth = ceilHealth;
            int displayHealth = gui.displayHealth;
            gui.random.setSeed(gui.tickCount * 312871L);
            FoodData fooddata = player.getFoodData();
            int food = fooddata.getFoodLevel();
            int xMin = gui.screenWidth / 2 - 91;
            int xMax = gui.screenWidth / 2 + 91;
            int yMin = gui.screenHeight - 39;
            float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) Math.max(displayHealth, ceilHealth));
            int absorptionHearts = Mth.ceil(player.getAbsorptionAmount());
            int heartLayersTotal = Mth.ceil((maxHealth + (float) absorptionHearts) / 2.0F / 10.0F);
            int j2 = Math.max(10 - (heartLayersTotal - 2), 3);
            int k2 = yMin - (heartLayersTotal - 1) * j2 - 10;
            int yMax = yMin - 10;
            int armor = player.getArmorValue();
            int regenHeartWiggle = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                regenHeartWiggle = gui.tickCount % Mth.ceil(maxHealth + 5.0F);
            }

            mc.getProfiler().push("armor"); //region armor

            for (int k3 = 0; k3 < 10; ++k3) {
                if (armor > 0) {
                    int l3 = xMin + k3 * 8;
                    if (k3 * 2 + 1 < armor) {
                        graphics.blitSprite(Gui.ARMOR_FULL_SPRITE, l3, k2, 9, 9);
                    }

                    if (k3 * 2 + 1 == armor) {
                        graphics.blitSprite(Gui.ARMOR_HALF_SPRITE, l3, k2, 9, 9);
                    }

                    if (k3 * 2 + 1 > armor) {
                        graphics.blitSprite(Gui.ARMOR_EMPTY_SPRITE, l3, k2, 9, 9);
                    }
                }
            }
            // endregion

            mc.getProfiler().popPush("health"); // region health
            renderHearts(ctx, xMin, yMin, j2, regenHeartWiggle, maxHealth, ceilHealth, displayHealth, absorptionHearts, isHealthBlinking);
            // endregion

            LivingEntity vehicle = gui.getPlayerVehicleWithHealth();
            int l4 = gui.getVehicleMaxHearts(vehicle);
            if (l4 == 0) {
                mc.getProfiler().popPush("food"); // region food

                for (int i4 = 0; i4 < 10; ++i4) {
                    int j4 = yMin;
                    ResourceLocation empty, half, full;
                    if (player.hasEffect(MobEffects.HUNGER)) {
                        empty = Gui.FOOD_EMPTY_HUNGER_SPRITE;
                        half = Gui.FOOD_HALF_HUNGER_SPRITE;
                        full = Gui.FOOD_FULL_HUNGER_SPRITE;
                    } else {
                        empty = Gui.FOOD_EMPTY_SPRITE;
                        half = Gui.FOOD_HALF_SPRITE;
                        full = Gui.FOOD_FULL_SPRITE;
                    }

                    if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.tickCount % (food * 3 + 1) == 0) {
                        j4 += (gui.random.nextInt(3) - 1);
                    }

                    int k4 = xMax - i4 * 8 - 9;
                    graphics.blitSprite(empty, k4, j4, 9, 9);
                    if (i4 * 2 + 1 < food) {
                        graphics.blitSprite(full, k4, j4, 9, 9);
                    } else if (i4 * 2 + 1 == food) {
                        graphics.blitSprite(half, k4, j4, 9, 9);
                    }
                }

                yMax -= 10; // endregion
            }

            mc.getProfiler().popPush("air"); // region air
            int i5 = player.getMaxAirSupply();
            int j5 = Math.min(player.getAirSupply(), i5);
            if (player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) || j5 < i5) {
                int k5 = gui.getVisibleVehicleHeartRows(l4) - 1;
                yMax -= k5 * 10;
                int l5 = Mth.ceil((double) (j5 - 2) * 10.0 / (double) i5);
                int i6 = Mth.ceil((double) j5 * 10.0 / (double) i5) - l5;

                for (int j6 = 0; j6 < l5 + i5; ++j6) {
                    if (j6 < l5) {
                        graphics.blitSprite(Gui.AIR_SPRITE, xMin - j6 * 8 - 9, yMax, 9, 9);
                    } else {
                        graphics.blitSprite(Gui.AIR_BURSTING_SPRITE, xMin - j6 * 8 - 9, yMax, 9, 9);
                    }
                }
            } // endregion

            mc.getProfiler().pop();
        }
    }

    public static void renderHearts(GuiContext ctx, int xMin, int yMin, int c, int regenHeartWiggle, float maxHealth, int ceilHealth, int displayHealth, int absorptionHearts, boolean isHealthBlinking) {
        int i = 0;
        boolean left = true;
        HeartContext heartContext = null;
        int heart = 0;
        for (HeartType type : HeartyMain.HEART_TYPE_REGISTRY.stream().sorted().toList()) {
            int amt = type.getNumber(ctx);
            ResourceLocation baseTexture = type.getTexture(ctx);
            for (int k = i; i < k + amt; i++) {
                int row = i / (HeartyConfig.RENDER_DOUBLE_HEARTS.get() ? 10 : 20);
                int column = (i / (HeartyConfig.RENDER_DOUBLE_HEARTS.get() ? 1 : 2)) % 10;
                int x = xMin + column * 8;
                int y = yMin - row * c;
                if (left) {
                    heart++;
                    heartContext = new HeartContext(ctx, x, y, heart);
                    for (HeartLayer layer : HeartyMain.HEART_LAYER_REGISTRY.stream().sorted().toList()) heartContext = layer.apply(heartContext);
                }

                HalfHeartContext halfHeartContext = new HalfHeartContext(heartContext, type, baseTexture, k);
                for (HalfHeartLayer layer : HeartyMain.HALF_HEART_LAYER_REGISTRY.stream().sorted().toList()) halfHeartContext = layer.apply(halfHeartContext);

                if (HeartyConfig.RENDER_DOUBLE_HEARTS.get()) renderHeart(halfHeartContext);
                else if (left) renderLeftHalfHeart(halfHeartContext);
                else renderRightHalfHeart(halfHeartContext);
                left = !left;
            }
        }
    }

    public static void renderHeart(HalfHeartContext ctx) {
        ctx.graphics().blitSprite(ctx.sprite(), 16, 16, 0, 0, ctx.x(), ctx.y() - 7, 0, 9, 16);
    }

    public static void renderLeftHalfHeart(HalfHeartContext ctx) {
        ctx.graphics().blitSprite(ctx.sprite(), 16, 16, 0, 0, ctx.x(), ctx.y() - 7, 0, 5, 16);
    }

    public static void renderRightHalfHeart(HalfHeartContext ctx) {
        ctx.graphics().blitSprite(ctx.sprite(), 16, 16, 5, 0, ctx.x() + 5, ctx.y() - 7, 4, 16);
    }

    /*
    blitSprite(
        TextureAtlasSprite sprite,
        int scaleX,
        int scaleY,
        int offsetU,
        int offsetV,
        int pixelX,
        int pixelY,
        int z,
        int pixelSizeX,
        int pixelSizeY
    )
     */
}
