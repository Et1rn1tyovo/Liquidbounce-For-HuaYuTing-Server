/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.TeleportHit
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RegexUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox2
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.animations.Animation
import net.ccbluex.liquidbounce.utils.render.animations.ContinualAnimation
import net.ccbluex.liquidbounce.utils.render.animations.Direction
import net.ccbluex.liquidbounce.utils.render.animations.impl.DecelerateAnimation
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))


    private val old by BoolValue("Old",false)

    private var lastTarget: Entity? = null

    private val animation:Animation = DecelerateAnimation(175,1.0)
    private val anim: ContinualAnimation = ContinualAnimation()
    var width = 0f
    var height = 0f

    override fun drawElement(): Border {
        var target: Entity? = null

        if (!KillAura.state) {
            animation.setDirection(Direction.BACKWARDS)
        }
        if (KillAura.target != null) {
            target = KillAura.target
            animation.setDirection(Direction.FORWARDS)
        }
        if (KillAura.state && (KillAura.target == null)) {
            animation.setDirection(Direction.BACKWARDS)
        }
        if (mc.currentScreen is GuiChat || mc.currentScreen is GuiHudDesigner) {
            animation.setDirection(Direction.FORWARDS)
            target = mc.thePlayer
        }

        if (target is EntityPlayer && target != null) {

            // Draw rect box

            if (old){
                GlStateManager.pushMatrix()


                width = (38 + Fonts.minecraftFont.getStringWidth(target.name))
                    .coerceAtLeast(118)
                    .toFloat()
                height = 38F
                RenderUtils.scaleStart(-1F + width / 2,height / 2,animation.output.toFloat())
                // Draws the skeet rectangles.
                RenderUtils.drawRect(0F, -1F, width + 5, 38F, Color(0,0,0, 50).rgb)
//        RenderUtils.skeetRectSmall(0.0, -2.0, 124.0, 38.0, 1.0)

                // Draws name.
                Fonts.minecraftFont.drawString(target.name, 42.3f.toInt(), 0.3f.toInt(), -1)

                // Gets health.
                val health = target.health

                // Gets health and absorption
                val healthWithAbsorption = target.health + target.absorptionAmount

                // Color stuff for the healthBar.
                val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
                val colors = arrayOf(Color.RED, Color.YELLOW, Color.GREEN)

                // Max health.
                val progress = health / target.maxHealth

                // Color.
                val healthColor = if (health >= 0.0f) BlendUtils.blendColors(fractions, colors, progress).brighter() else Color.RED

                // Round.
                var cockWidth = 0.0
                cockWidth = RegexUtils.round(cockWidth, 5.0.toInt())
                if (cockWidth < 50.0) {
                    cockWidth = 50.0
                }

                // Healthbar + absorption
                val healthBarPos = cockWidth * progress.toDouble()
                RenderUtils.rectangle(42.5, 10.3, 103.0, 13.5, healthColor.darker().darker().darker().darker().rgb)
                RenderUtils.rectangle(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.rgb)
                if (target.absorptionAmount > 0.0f) {
                    RenderUtils.rectangle(97.5 - target.absorptionAmount.toDouble(), 10.3, 103.5, 13.5, Color(137, 112, 9).rgb)
                }
                // Draws rect around health bar.
                RenderUtils.rectangleBordered(42.0, 9.8, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.rgb)

                // Draws the lines between the healthbar to make it look like boxes.
                for (dist in 1..9) {
                    val cock = cockWidth / 8.5 * dist.toDouble()
                    RenderUtils.rectangle(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.rgb)
                }

                // Draw targets hp number and distance number.
                GlStateManager.scale(0.5, 0.5, 0.5)
                val distance = mc.thePlayer.getDistanceToEntity(target).toInt()
                val nice = "HP: " + healthWithAbsorption.toInt() + " | Dist: " + distance
                Fonts.minecraftFont.drawString(nice, 85.3f, 32.3f, -1, true)
                GlStateManager.scale(2.0, 2.0, 2.0)
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                GlStateManager.enableAlpha()
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                // Draw targets armor and tools and weapons and shows the enchants.
                drawEquippedShit(28, 20)
                GlStateManager.disableAlpha()
                GlStateManager.disableBlend()
                // Draws targets model.
                GlStateManager.scale(0.31, 0.31, 0.31)
                GlStateManager.translate(73.0f, 102.0f, 40.0f)
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                RenderUtils.drawModel(target.rotationYaw, target.rotationPitch, target)
                GlStateManager.popMatrix()
                RenderUtils.scaleEnd()
            }else{
                val font = Fonts.fontBold35
                width = 140F.coerceAtLeast(47F + font.getStringWidth(target.name))
                height = 45f
                RenderUtils.scaleStart(width / 2,height / 2,animation.output.toFloat())
                RenderUtils.drawExhiRect(0F, 0F, width, 45F, 1F)

                RenderUtils.drawRect(2.5F, 2.5F, 42.5F, 42.5F, (Color(60, 60, 60)).rgb)
                RenderUtils.drawRect(3F, 3F, 42F, 42F, (Color(20, 20, 20)).rgb)

                GL11.glColor4f(1f, 1f, 1f, 1f)
                RenderUtils.drawEntityOnScreen(22, 40, 16, target)

                font.drawString(target.name, 46, 5, -1)

                val barLength = 70F * (target.health / target.maxHealth).coerceIn(0F, 1F)
                RenderUtils.drawRect(45F, 14F, 45F + 70F, 18F, (BlendUtils.getHealthColor(target.health, target.maxHealth).darker()).rgb)
                RenderUtils.drawRect(45F, 14F, 45F + barLength, 18F, (BlendUtils.getHealthColor(target.health, target.maxHealth)).rgb)

                for (i in 0..9)
                    RenderUtils.drawRectBasedBorder(45F + i * 7F, 14F, 45F + (i + 1) * 7F, 18F, 0.5F, (Color(60, 60, 60)).rgb)
                GlStateManager.pushMatrix()
                val scale = 0.5
                GlStateManager.scale(scale,scale,scale)
                mc.fontRendererObj.drawStringWithShadow("HP:${target.health.toInt()} | Dist:${mc.thePlayer.getDistanceToEntityBox2(target).toInt()}", 45F * 2, 21F * 2, (-1))
                GlStateManager.popMatrix()
                GlStateManager.resetColor()
                GL11.glPushMatrix()
                GL11.glColor4f(1f, 1f, 1f, 1f)
                GlStateManager.enableRescaleNormal()
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                RenderHelper.enableGUIStandardItemLighting()

                val renderItem = mc.renderItem

                var x = 45
                var y = 28

                for (index in 3 downTo 0) {
                    val stack = target.inventory.armorInventory[index] ?: continue

                    renderItem.renderItemIntoGUI(stack, x, y)
                    renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
                    RenderUtils.drawExhiEnchants(stack, x.toFloat(), y.toFloat())

                    x += 16
                }

                val mainStack = target.heldItem

                renderItem.renderItemIntoGUI(mainStack, x, y)
                renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
                RenderUtils.drawExhiEnchants(mainStack, x.toFloat(), y.toFloat())


                RenderHelper.disableStandardItemLighting()
                GlStateManager.disableRescaleNormal()
                GlStateManager.enableAlpha()
                GlStateManager.disableBlend()
                GlStateManager.disableLighting()
                GlStateManager.disableCull()
                GL11.glPopMatrix()
                RenderUtils.scaleEnd()
            }
        }


        lastTarget = target

        return Border(0F, 0F, width, height)
    }
    private fun drawEquippedShit(x: Int, y: Int) {
        var target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        if (target == null || target !is EntityPlayer) return
        GL11.glPushMatrix()
        val stuff: MutableList<ItemStack> = ArrayList()
        var cock = -2
        for (geraltOfNigeria in 3 downTo 0) {
            val armor = (target as EntityPlayer).getCurrentArmor(geraltOfNigeria)
            if (armor != null) {
                stuff.add(armor)
            }
        }
        if ((target as EntityPlayer).heldItem != null) {
            stuff.add((target as EntityPlayer).heldItem)
        }
        for (yes in stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting()
                cock += 16
            }
            GlStateManager.pushMatrix()
            GlStateManager.disableAlpha()
            GlStateManager.clear(256)
            GlStateManager.enableBlend()
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(yes, cock + x, y)
            RenderUtils.renderEnchantText(yes, cock + x, y + 0.5f)
            GlStateManager.disableBlend()
            GlStateManager.scale(0.5, 0.5, 0.5)
            GlStateManager.disableDepth()
            GlStateManager.disableLighting()
            GlStateManager.enableDepth()
            GlStateManager.scale(2.0f, 2.0f, 2.0f)
            GlStateManager.enableAlpha()
            GlStateManager.popMatrix()
            yes.enchantmentTagList
        }
        GL11.glPopMatrix()
    }
    protected fun renderPlayer2D(x: Float, y: Float, width: Float, height: Float, player: AbstractClientPlayer) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(player.locationSkin)
        Gui.drawScaledCustomSizeModalRect(
            x.toInt(),
            y.toInt(),
            8.0.toFloat(),
            8.0.toFloat(),
            8,
            8,
            width.toInt(),
            height.toInt(),
            64.0f,
            64.0f
        )
    }

}