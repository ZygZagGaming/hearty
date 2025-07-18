package io.github.zygzaggaming.hearty.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zygzaggaming.hearty.api.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HeartyGuiLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Gui gui = mc.gui;

        GuiContext ctx = new GuiContext(mc, gui.getCameraPlayer(), gui, guiGraphics);

        mc.getProfiler().push("hearty_health");
        RenderSystem.enableBlend();

        Player player = (Player) mc.getCameraEntity();
        assert player != null;
        if (mc.gameMode == null || mc.gameMode.canHurtPlayer()) {
            int health = Mth.ceil(player.getHealth());
            //boolean highlight = gui.healthBlinkTime > (long) gui.tickCount && (gui.healthBlinkTime - (long) gui.tickCount) / 3L % 2L == 1L;

            if (health < gui.lastHealth && player.invulnerableTime > 0) {
                gui.lastHealthTime = Util.getMillis();
                gui.healthBlinkTime = gui.tickCount + 20;
            } else if (health > gui.lastHealth && player.invulnerableTime > 0) {
                gui.lastHealthTime = Util.getMillis();
                gui.healthBlinkTime = gui.tickCount + 10;
            }

            if (Util.getMillis() - gui.lastHealthTime > 1000L) {
                gui.lastHealth = health;
                gui.displayHealth = health;
                gui.lastHealthTime = Util.getMillis();
            }

            gui.lastHealth = health;
            int healthLast = gui.displayHealth;

            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            assert attrMaxHealth != null;
            float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
            int absorb = Mth.ceil(player.getAbsorptionAmount());

            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            gui.random.setSeed(gui.tickCount * 312871L);

            int left = guiGraphics.guiWidth() / 2 - 91;
            int top = guiGraphics.guiHeight() - gui.leftHeight;
            gui.leftHeight += (healthRows * rowHeight);
            if (rowHeight != 10) gui.leftHeight += 10 - rowHeight;

            //int regen = -1;
            //if (player.hasEffect(MobEffects.REGENERATION)) {
            //    regen = gui.tickCount % Mth.ceil(healthMax + 5.0F);
            //}

            renderHearts(ctx, left, top, rowHeight/*, regen, healthMax, health, healthLast, absorb, highlight*/);

            RenderSystem.disableBlend();
            mc.getProfiler().pop();
        }
    }

    public static void renderHearts(GuiContext ctx, int xMin, int yMin, int rowHeight/*, int regenHeartWiggle, float maxHealth, int ceilHealth, int displayHealth, int absorptionHearts, boolean isHealthBlinking*/) {
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
                int y = yMin - row * rowHeight;
                if (left || HeartyConfig.RENDER_DOUBLE_HEARTS.get()) {
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
