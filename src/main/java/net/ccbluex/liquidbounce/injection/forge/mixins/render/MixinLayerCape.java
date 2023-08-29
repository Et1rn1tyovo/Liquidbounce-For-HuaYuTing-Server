package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.Cape;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LayerCape.class)
@SideOnly(Side.CLIENT)
public class MixinLayerCape implements LayerRenderer<AbstractClientPlayer> {
    @Shadow
    @Final
    private final RenderPlayer playerRenderer;
    @Overwrite
    public boolean shouldCombineTextures() {
        return false;
    }
    public MixinLayerCape(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }
    @Overwrite
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float v, float v1, float v2, float v3, float v4, float v5, float v6) {
        if (((AbstractClientPlayer) ((AbstractClientPlayer) entitylivingbaseIn)).hasPlayerInfo() && !((AbstractClientPlayer) entitylivingbaseIn).isInvisible() && ((AbstractClientPlayer) entitylivingbaseIn).getName().equals(Minecraft.getMinecraft().getSession().getUsername()) && ((AbstractClientPlayer) entitylivingbaseIn).isWearing(EnumPlayerModelParts.CAPE) && LiquidBounce.INSTANCE.getModuleManager().getModule(Cape.class).getState()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.playerRenderer.bindTexture(new ResourceLocation("liquidbounce/cape/cape.png"));
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 0.125f);
            double d0 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosX + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosX - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosX) * (double) v2 - (((AbstractClientPlayer) entitylivingbaseIn).prevPosX + (((AbstractClientPlayer) entitylivingbaseIn).posX - ((AbstractClientPlayer) entitylivingbaseIn).prevPosX) * (double) v2);
            double d1 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosY + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosY - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosY) * (double) v2 - (((AbstractClientPlayer) entitylivingbaseIn).prevPosY + (((AbstractClientPlayer) entitylivingbaseIn).posY - ((AbstractClientPlayer) entitylivingbaseIn).prevPosY) * (double) v2);
            double d2 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosZ + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosZ - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosZ) * (double) v2 - (((AbstractClientPlayer) entitylivingbaseIn).prevPosZ + (((AbstractClientPlayer) entitylivingbaseIn).posZ - ((AbstractClientPlayer) entitylivingbaseIn).prevPosZ) * (double) v2);
            float prevRenderYawOffset = MathHelper.wrapAngleTo180_float(((AbstractClientPlayer) entitylivingbaseIn).prevRenderYawOffset);
            float f = prevRenderYawOffset + (MathHelper.wrapAngleTo180_float(((AbstractClientPlayer) entitylivingbaseIn).renderYawOffset) - prevRenderYawOffset) * v2;
            double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            if (f2 > 165.0f) {
                f2 = 165.0f;
            }
            if (f1 < -5.0f) {
                f1 = -5.0f;
            }
            float f4 = ((AbstractClientPlayer) entitylivingbaseIn).prevCameraYaw + (((AbstractClientPlayer) entitylivingbaseIn).cameraYaw - ((AbstractClientPlayer) entitylivingbaseIn).prevCameraYaw) * v2;
            f1 += MathHelper.sin((((AbstractClientPlayer) entitylivingbaseIn).prevDistanceWalkedModified + (((AbstractClientPlayer) entitylivingbaseIn).distanceWalkedModified - ((AbstractClientPlayer) entitylivingbaseIn).prevDistanceWalkedModified) * v2) * 6.0f) * 32.0f * f4;
            if (((AbstractClientPlayer) entitylivingbaseIn).isSneaking()) {
                f1 += 25.0f;
                GL11.glTranslatef(0.0f, 0.142f, -0.0178f);
            }
            GL11.glRotatef(6.0f + f2 / 2.0f + f1, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(f3 / 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-f3 / 2.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            this.playerRenderer.getMainModel().renderCape(0.0625f);
            this.playerRenderer.bindTexture(new ResourceLocation("liquidbounce/cape/overlay.png"));
            int rgb = RenderUtils.getRainbowOpaque(2, HUD.INSTANCE.getS(),HUD.INSTANCE.getB(),1);
            float alpha = 0.3f;
            float red = (float)(rgb >> 16 & 0xFF) / 255.0f;
            float green = (float)(rgb >> 8 & 0xFF) / 255.0f;
            float blue = (float)(rgb & 0xFF) / 255.0f;
            GL11.glColor4f(red, green, blue, alpha);
            this.playerRenderer.getMainModel().renderCape(0.0625f);
            GL11.glPopMatrix();
            RenderUtils.resetColor();
        }
    }

}
