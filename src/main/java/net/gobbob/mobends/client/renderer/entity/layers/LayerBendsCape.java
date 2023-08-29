package net.gobbob.mobends.client.renderer.entity.layers;

import net.gobbob.mobends.client.renderer.entity.RenderBendsPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerBendsCape implements LayerRenderer<AbstractClientPlayer>
{
    private final RenderBendsPlayer playerRenderer;

    public LayerBendsCape(RenderBendsPlayer playerRendererIn)
    {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (((AbstractClientPlayer) entitylivingbaseIn).hasPlayerInfo() && !((AbstractClientPlayer) entitylivingbaseIn).isInvisible() && ((AbstractClientPlayer) entitylivingbaseIn).isWearing(EnumPlayerModelParts.CAPE) && ((AbstractClientPlayer) entitylivingbaseIn).getLocationCape() != null)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.playerRenderer.bindTexture(((AbstractClientPlayer) entitylivingbaseIn).getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double d0 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosX + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosX - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosX) * (double)partialTicks - (((AbstractClientPlayer) entitylivingbaseIn).prevPosX + (((AbstractClientPlayer) entitylivingbaseIn).posX - ((AbstractClientPlayer) entitylivingbaseIn).prevPosX) * (double)partialTicks);
            double d1 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosY + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosY - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosY) * (double)partialTicks - (((AbstractClientPlayer) entitylivingbaseIn).prevPosY + (((AbstractClientPlayer) entitylivingbaseIn).posY - ((AbstractClientPlayer) entitylivingbaseIn).prevPosY) * (double)partialTicks);
            double d2 = ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosZ + (((AbstractClientPlayer) entitylivingbaseIn).chasingPosZ - ((AbstractClientPlayer) entitylivingbaseIn).prevChasingPosZ) * (double)partialTicks - (((AbstractClientPlayer) entitylivingbaseIn).prevPosZ + (((AbstractClientPlayer) entitylivingbaseIn).posZ - ((AbstractClientPlayer) entitylivingbaseIn).prevPosZ) * (double)partialTicks);
            float f = ((AbstractClientPlayer) entitylivingbaseIn).prevRenderYawOffset + (((AbstractClientPlayer) entitylivingbaseIn).renderYawOffset - ((AbstractClientPlayer) entitylivingbaseIn).prevRenderYawOffset) * partialTicks;
            double d3 = MathHelper.sin(f * (float)Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float)Math.PI / 180.0F);
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }

            float f4 = ((AbstractClientPlayer) entitylivingbaseIn).prevCameraYaw + (((AbstractClientPlayer) entitylivingbaseIn).cameraYaw - ((AbstractClientPlayer) entitylivingbaseIn).prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((((AbstractClientPlayer) entitylivingbaseIn).prevDistanceWalkedModified + (((AbstractClientPlayer) entitylivingbaseIn).distanceWalkedModified - ((AbstractClientPlayer) entitylivingbaseIn).prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (((AbstractClientPlayer) entitylivingbaseIn).isSneaking())
            {
                f1 += 25.0F;
            }

            GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}