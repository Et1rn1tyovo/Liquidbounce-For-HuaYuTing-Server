/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.addNotification
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.deltaTime
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glColor4f
import java.awt.Color
import kotlin.math.max

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example","Example Notification",NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val notifications = mutableListOf<Notification>()
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index)) {
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-150F, -30F, 0F, 0F)
        }

        return null
    }

}

class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    val animeTime: Int = 500,
) {
    val width = 100.coerceAtLeast(
        Fonts.font35.getStringWidth(this.title)
            .coerceAtLeast(Fonts.font35.getStringWidth(this.content)) + 12
    )
    private val notifyDir = "liquidbounce/noti/" + type.name
    val image = ResourceLocation(notifyDir + ".png")
    val height = 30
    private var firstY = 0f
    private var firstYz = 0
    var x = 0f
    var textLength = Fonts.minecraftFont.getStringWidth(content) + 10

    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(index: Int): Boolean {
        val nowTime = System.currentTimeMillis()
        val realY = (index + 1) * height
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
        GL11.glTranslatef(-width.toFloat(), 0F, 0F)
        Gui.drawRect(-22, -60, width.toInt(), height.toInt() - 60, Color(32, 32, 32,255).rgb)
        //RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 100))
        drawRect(
            -22F,
            height - 2F - 60,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
            height.toFloat() - 60,
            type.renderColor.darker()
        )
        drawRect(
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
            height - 2F - 60,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F) + 7,
            height.toFloat() - 60,
            type.renderColor.darker().darker().darker().darker()
        )
        drawRect(
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
            height - 2F - 60,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F) + 5,
            height.toFloat() - 60,
            type.renderColor.darker().darker().darker()
        )
        drawRect(
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
            height - 2F - 60,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F) + 3,
            height.toFloat() - 60,
            type.renderColor.darker().darker()
        )
        Fonts.fontTahoma.drawString(title, 6F, 4F- 60, -1)
        Fonts.font35.drawString(content, 6F, 17F- 60, -1)
        RenderUtils.drawImage(image, -19, 5- 60, 18, 18)
        GlStateManager.resetColor()
        return false
    }

}
enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(20, 250, 90)),
    ERROR(Color(255, 30, 30)),
    WARNING(Color(0xF5FD00)),
    INFO( Color(106, 106, 220));
}
enum class FadeState { IN, STAY, OUT, END }


