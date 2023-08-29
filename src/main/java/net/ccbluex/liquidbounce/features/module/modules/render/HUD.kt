/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.realmsclient.gui.ChatFormatting
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_NAME
import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.boss.BossStatus
import net.minecraft.util.ResourceLocation
import java.text.SimpleDateFormat

object HUD : Module("HUD", ModuleCategory.RENDER, defaultInArray = false) {
    val blackHotbar by BoolValue("BlackHotbar", true)
    val inventoryParticle by BoolValue("InventoryParticle", false)
    val blur by BoolValue("Blur", false)
    val shadow by BoolValue("Shadow", false)
    val clientName by BoolValue("Name", true)
    val s by FloatValue("Saturation",0.45f,0f..1f)
    val b by FloatValue("Brightness",1f,0f..1f)
    val fontChat by BoolValue("FontChat", false)
    val font by FontValue("Font", Fonts.minecraftFont)


    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner)
            return
        if (clientName){
            font.drawStringWithShadow("E",2f,2f,RenderUtils.getRainbowOpaque(2, s,b,1))
            font.drawStringWithShadow(
                "xhibition "+ChatFormatting.GRAY + "[" + ChatFormatting.RESET + "1.8.x" + ChatFormatting.GRAY + "] [" + ChatFormatting.RESET + Minecraft.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] [" + ChatFormatting.RESET + SimpleDateFormat(
                    "HH:mm"
                ).format(
                    System.currentTimeMillis()
                ) + ChatFormatting.GRAY + "]",2f + font.getStringWidth("E"),2f,-1)
            font.drawStringWithShadow(
                ChatFormatting.GRAY.toString() + "Release Build - " + ChatFormatting.RESET + "1.0" + ChatFormatting.GRAY.toString() + " User - " + ChatFormatting.RESET + mc.session.playerID,
                ScaledResolution(mc).getScaledWidth() - font.getStringWidth(ChatFormatting.GRAY.toString() + "Release Build - " + ChatFormatting.RESET + "1.0" + ChatFormatting.GRAY.toString() + " User - " + ChatFormatting.RESET + mc.session.playerID).toFloat(),
                ScaledResolution(mc).getScaledHeight() - font.FONT_HEIGHT - 1f,
                -1
            )
            mc.fontRendererObj.drawStringWithShadow(
                "WatchDog Inactive",
                ScaledResolution(mc).getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth("WatchDog Inactive") / 2f,
                (if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) 47 else 30) - mc.fontRendererObj.FONT_HEIGHT - 2f,
                -1
            )
            val time = String.format("%dh %dm %ds",
                (System.currentTimeMillis() - LiquidBounce.startTime) / (1000 * 60 * 60) % 24, (System.currentTimeMillis() - LiquidBounce.startTime) / (1000 * 60) % 60, (System.currentTimeMillis() -LiquidBounce.startTime) / 1000 % 60
            )

            mc.fontRendererObj.drawStringWithShadow(
                time,
                ScaledResolution(mc).getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth(time) / 2f,
                if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) 47f else 30f,
                -1
            )
        }
        hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) = hud.update()

    @EventTarget
    fun onKey(event: KeyEvent) = hud.handleKey('a', event.key)

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blur && !mc.entityRenderer.isShaderActive && event.guiScreen != null &&
                !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner)) mc.entityRenderer.loadShader(
            ResourceLocation(CLIENT_NAME.lowercase() + "/blur.json")
        ) else if (mc.entityRenderer.shaderGroup != null &&
            "liquidbounce/blur.json" in mc.entityRenderer.shaderGroup.shaderGroupName) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}