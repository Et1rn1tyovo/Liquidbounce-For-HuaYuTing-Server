/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.hitBox
import net.ccbluex.liquidbounce.utils.extensions.toRadians
import net.ccbluex.liquidbounce.utils.gl.GLUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.*
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

object RenderUtils : MinecraftInstance() {
    private val glCapMap = mutableMapOf<Int, Boolean>()
    private val DISPLAY_LISTS_2D = IntArray(4)
    var deltaTime = 0
    @JvmStatic
    fun disableRender3D(enableDepth: Boolean) {
        if (enableDepth) {
            glDepthMask(true)
            glEnable(2929)
        }
        glEnable(3553)
        glDisable(3042)
        glEnable(3008)
        glDisable(2848)
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
    @JvmStatic
    fun enableRender3D(disableDepth: Boolean) {
        if (disableDepth) {
            glDepthMask(false)
            glDisable(2929)
        }
        glDisable(3008)
        glEnable(3042)
        glDisable(3553)
        glBlendFunc(770, 771)
        glEnable(2848)
        glHint(3154, 4354)
        glLineWidth(1.0f)
    }
    @JvmStatic
    fun setColor(colorHex: Int) {
        val alpha = (colorHex shr 24 and 255).toFloat() / 255.0f
        val red = (colorHex shr 16 and 255).toFloat() / 255.0f
        val green = (colorHex shr 8 and 255).toFloat() / 255.0f
        val blue = (colorHex and 255).toFloat() / 255.0f
        glColor4f(red, green, blue, alpha)
    }
    init {
        for (i in DISPLAY_LISTS_2D.indices) {
            DISPLAY_LISTS_2D[i] = glGenLists(1)
        }

        glNewList(DISPLAY_LISTS_2D[0], GL_COMPILE)
        quickDrawRect(-7f, 2f, -4f, 3f)
        quickDrawRect(4f, 2f, 7f, 3f)
        quickDrawRect(-7f, 0.5f, -6f, 3f)
        quickDrawRect(6f, 0.5f, 7f, 3f)
        glEndList()
        glNewList(DISPLAY_LISTS_2D[1], GL_COMPILE)
        quickDrawRect(-7f, 3f, -4f, 3.3f)
        quickDrawRect(4f, 3f, 7f, 3.3f)
        quickDrawRect(-7.3f, 0.5f, -7f, 3.3f)
        quickDrawRect(7f, 0.5f, 7.3f, 3.3f)
        glEndList()
        glNewList(DISPLAY_LISTS_2D[2], GL_COMPILE)
        quickDrawRect(4f, -20f, 7f, -19f)
        quickDrawRect(-7f, -20f, -4f, -19f)
        quickDrawRect(6f, -20f, 7f, -17.5f)
        quickDrawRect(-7f, -20f, -6f, -17.5f)
        glEndList()
        glNewList(DISPLAY_LISTS_2D[3], GL_COMPILE)
        quickDrawRect(7f, -20f, 7.3f, -17.5f)
        quickDrawRect(-7.3f, -20f, -7f, -17.5f)
        quickDrawRect(4f, -20.3f, 7.3f, -20f)
        quickDrawRect(-7.3f, -20.3f, -4f, -20f)
        glEndList()
    }
    @JvmStatic
    fun drawEntityOnScreen(posX: Int, posY: Int, scale: Int, entity: EntityLivingBase?) {
        drawEntityOnScreen(
            posX.toDouble(),
            posY.toDouble(), scale.toFloat(), entity
        )
    }
    fun drawEntityOnScreen(posX: Double, posY: Double, scale: Float, entity: EntityLivingBase?) {
        pushMatrix()
        enableColorMaterial()
        translate(posX, posY, 50.0)
        scale(-scale, scale, scale)
        rotate(180f, 0f, 0f, 1f)
        rotate(135f, 0f, 1f, 0f)
        RenderHelper.enableStandardItemLighting()
        rotate(-135f, 0f, 1f, 0f)
        translate(0.0, 0.0, 0.0)
        val rendermanager: RenderManager = mc.getRenderManager()
        rendermanager.setPlayerViewY(180f)
        rendermanager.isRenderShadow = false
        rendermanager.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0f, 1f)
        rendermanager.isRenderShadow = true
        popMatrix()
        RenderHelper.disableStandardItemLighting()
        disableRescaleNormal()
        setActiveTexture(OpenGlHelper.lightmapTexUnit)
        disableTexture2D()
        setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
    @JvmStatic
    fun drawRectBasedBorder(x: Float, y: Float, x2: Float, y2: Float, width: Float, color1: Int) {
        drawRect(x - width / 2f, y - width / 2f, x2 + width / 2f, y + width / 2f, color1)
        drawRect(x - width / 2f, y + width / 2f, x + width / 2f, y2 + width / 2f, color1)
        drawRect(x2 - width / 2f, y + width / 2f, x2 + width / 2f, y2 + width / 2f, color1)
        drawRect(x + width / 2f, y2 - width / 2f, x2 - width / 2f, y2 + width / 2f, color1)
    }
    fun drawExhiEnchants(stack: ItemStack, x: Float, y: Float) {
        var y = y
        RenderHelper.disableStandardItemLighting()
        disableDepth()
        disableBlend()
        GlStateManager.resetColor()
        val darkBorder = -0x1000000
        if (stack.item is ItemArmor) {
            val prot = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)
            val unb = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            val thorn = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)
            if (prot > 0) {
                RenderUtils.drawExhiOutlined(
                    prot.toString() + "",
                    RenderUtils.drawExhiOutlined("P", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(prot),
                    RenderUtils.getMainColor(prot),
                    true
                )
                y += 4f
            }
            if (unb > 0) {
                RenderUtils.drawExhiOutlined(
                    unb.toString() + "",
                    RenderUtils.drawExhiOutlined("U", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(unb),
                    RenderUtils.getMainColor(unb),
                    true
                )
                y += 4f
            }
            if (thorn > 0) {
                RenderUtils.drawExhiOutlined(
                    thorn.toString() + "",
                    RenderUtils.drawExhiOutlined("T", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(thorn),
                    RenderUtils.getMainColor(thorn),
                    true
                )
                y += 4f
            }
        }
        if (stack.item is ItemBow) {
            val power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack)
            val punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack)
            val flame = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack)
            val unb = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            if (power > 0) {
                RenderUtils.drawExhiOutlined(
                    power.toString() + "",
                    RenderUtils.drawExhiOutlined("Pow", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(power),
                    RenderUtils.getMainColor(power),
                    true
                )
                y += 4f
            }
            if (punch > 0) {
                RenderUtils.drawExhiOutlined(
                    punch.toString() + "",
                    RenderUtils.drawExhiOutlined("Pun", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(punch),
                    RenderUtils.getMainColor(punch),
                    true
                )
                y += 4f
            }
            if (flame > 0) {
                RenderUtils.drawExhiOutlined(
                    flame.toString() + "",
                    RenderUtils.drawExhiOutlined("F", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(flame),
                    RenderUtils.getMainColor(flame),
                    true
                )
                y += 4f
            }
            if (unb > 0) {
                RenderUtils.drawExhiOutlined(
                    unb.toString() + "",
                    RenderUtils.drawExhiOutlined("U", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(unb),
                    RenderUtils.getMainColor(unb),
                    true
                )
                y += 4f
            }
        }
        if (stack.item is ItemSword) {
            val sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack)
            val kb = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack)
            val fire = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack)
            val unb = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            if (sharp > 0) {
                RenderUtils.drawExhiOutlined(
                    sharp.toString() + "",
                    RenderUtils.drawExhiOutlined("S", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(sharp),
                    RenderUtils.getMainColor(sharp),
                    true
                )
                y += 4f
            }
            if (kb > 0) {
                RenderUtils.drawExhiOutlined(
                    kb.toString() + "",
                    RenderUtils.drawExhiOutlined("K", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(kb),
                    RenderUtils.getMainColor(kb),
                    true
                )
                y += 4f
            }
            if (fire > 0) {
                RenderUtils.drawExhiOutlined(
                    fire.toString() + "",
                    RenderUtils.drawExhiOutlined("F", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(fire),
                    RenderUtils.getMainColor(fire),
                    true
                )
                y += 4f
            }
            if (unb > 0) {
                RenderUtils.drawExhiOutlined(
                    unb.toString() + "",
                    RenderUtils.drawExhiOutlined("U", x, y, 0.35f, darkBorder, -1, true),
                    y,
                    0.35f,
                    RenderUtils.getBorderColor(unb),
                    RenderUtils.getMainColor(unb),
                    true
                )
                y += 4f
            }
        }
        enableDepth()
        RenderHelper.enableGUIStandardItemLighting()
    }
    private fun drawExhiOutlined(
        text: String,
        x: Float,
        y: Float,
        borderWidth: Float,
        borderColor: Int,
        mainColor: Int,
        drawText: Boolean
    ): Float {
        Fonts.fontTahomaSmall!!.drawString(text, x, y - borderWidth, borderColor)
        Fonts.fontTahomaSmall!!.drawString(text, x, y + borderWidth, borderColor)
        Fonts.fontTahomaSmall!!.drawString(text, x - borderWidth, y, borderColor)
        Fonts.fontTahomaSmall!!.drawString(text, x + borderWidth, y, borderColor)
        if (drawText) Fonts.fontTahomaSmall!!.drawString(text, x, y, mainColor)
        return x + Fonts.fontTahomaSmall!!.getWidth(text) - 2f
    }
    private fun getMainColor(level: Int): Int {
        return if (level == 4) -0x560000 else -1
    }

    private fun getBorderColor(level: Int): Int {
        if (level == 2) return 0x7055FF55
        if (level == 3) return 0x7000AAAA
        if (level == 4) return 0x70AA0000
        return if (level >= 5) 0x70FFAA00 else 0x70FFFFFF
    }

    @JvmStatic
    fun getRainbowOpaque(
        seconds: Int,
        saturation: Float,
        brightness: Float,
        index: Int
    ): Int {
        val hue =
            (System.currentTimeMillis() + index) % (seconds * 1000) / (seconds * 1000).toFloat()
        return Color.HSBtoRGB(hue, saturation, brightness)
    }
    @JvmStatic
    fun drawExhiRect(x: Float, y: Float, x2: Float, y2: Float, alpha: Float) {
        drawRect(x - 3.5f, y - 3.5f, x2 + 3.5f, y2 + 3.5f, Color(0f, 0f, 0f, alpha).rgb)
        drawRect(x - 3f, y - 3f, x2 + 3f, y2 + 3f, Color(50f / 255f, 50f / 255f, 50f / 255f, alpha).rgb)
        drawRect(x - 2.5f, y - 2.5f, x2 + 2.5f, y2 + 2.5f, Color(26f / 255f, 26f / 255f, 26f / 255f, alpha).rgb)
        drawRect(x - 0.5f, y - 0.5f, x2 + 0.5f, y2 + 0.5f, Color(50f / 255f, 50f / 255f, 50f / 255f, alpha).rgb)
        drawRect(x, y, x2, y2, Color(18f / 255f, 18 / 255f, 18f / 255f, alpha).rgb)
    }
    fun color(color: Int, alpha: Float) {
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        color(r, g, b, alpha)
    }
    fun drawModel(yaw: Float, pitch: Float, entityLivingBase: EntityLivingBase) {
        GlStateManager.resetColor()
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        enableColorMaterial()
        pushMatrix()
        translate(0.0f, 0.0f, 50.0f)
        scale(-50.0f, 50.0f, 50.0f)
        rotate(180.0f, 0.0f, 0.0f, 1.0f)
        val renderYawOffset = entityLivingBase.renderYawOffset
        val rotationYaw = entityLivingBase.rotationYaw
        val rotationPitch = entityLivingBase.rotationPitch
        val prevRotationYawHead = entityLivingBase.prevRotationYawHead
        val rotationYawHead = entityLivingBase.rotationYawHead
        rotate(135.0f, 0.0f, 1.0f, 0.0f)
        RenderHelper.enableStandardItemLighting()
        rotate(-135.0f, 0.0f, 1.0f, 0.0f)
        rotate((-Math.atan((pitch / 40.0f).toDouble()) * 20.0).toFloat(), 1.0f, 0.0f, 0.0f)
        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4f
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2f
        entityLivingBase.rotationPitch = pitch
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw
        translate(0.0f, 0.0f, 0.0f)
        val renderManager: RenderManager = mc.getRenderManager()
        renderManager.setPlayerViewY(180.0f)
        renderManager.isRenderShadow = false
        entityLivingBase.alwaysRenderNameTag
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f)
        renderManager.isRenderShadow = true
        entityLivingBase.renderYawOffset = renderYawOffset
        entityLivingBase.rotationYaw = rotationYaw
        entityLivingBase.rotationPitch = rotationPitch
        entityLivingBase.prevRotationYawHead = prevRotationYawHead
        entityLivingBase.rotationYawHead = rotationYawHead
        popMatrix()
        RenderHelper.disableStandardItemLighting()
        disableRescaleNormal()
        setActiveTexture(OpenGlHelper.lightmapTexUnit)
        disableTexture2D()
        setActiveTexture(OpenGlHelper.defaultTexUnit)
        GlStateManager.resetColor()
    }
    @JvmStatic
    fun rectangleBordered(
        x: Double, y: Double, x1: Double, y1: Double, width: Double, internalColor: Int,
        borderColor: Int
    ) {
        RenderUtils.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor)
        color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderUtils.rectangle(x + width, y, x1 - width, y + width, borderColor)
        color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderUtils.rectangle(x, y, x + width, y1, borderColor)
        color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderUtils.rectangle(x1 - width, y, x1, y1, borderColor)
        color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderUtils.rectangle(x + width, y1 - width, x1 - width, y1, borderColor)
        color(1.0f, 1.0f, 1.0f, 1.0f)
    }
    @JvmStatic
    fun rectangle(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glColor(color)
        glBegin(GL_QUADS)
        glVertex2d(x2, y)
        glVertex2d(x, y)
        glVertex2d(x, y2)
        glVertex2d(x2, y2)
        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
    }
    fun renderEnchantText(stack: ItemStack, x: Int, y: Float) {
        RenderHelper.disableStandardItemLighting()
        var enchantmentY = y + 24f
        if (stack.item is ItemArmor) {
            val protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)
            val unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            val thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)
            if (protectionLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "P" + ColorUtils.getColor(protectionLevel) + protectionLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
            if (unbreakingLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
            if (thornLevel > 0) {
                RenderUtils.drawEnchantTag("T" + ColorUtils.getColor(thornLevel) + thornLevel, x * 2, enchantmentY)
                enchantmentY += 8f
            }
        }
        if (stack.item is ItemBow) {
            val powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack)
            val punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack)
            val flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack)
            val unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            if (powerLevel > 0) {
                RenderUtils.drawEnchantTag("Pow" + ColorUtils.getColor(powerLevel) + powerLevel, x * 2, enchantmentY)
                enchantmentY += 8f
            }
            if (punchLevel > 0) {
                RenderUtils.drawEnchantTag("Pun" + ColorUtils.getColor(punchLevel) + punchLevel, x * 2, enchantmentY)
                enchantmentY += 8f
            }
            if (flameLevel > 0) {
                RenderUtils.drawEnchantTag("F" + ColorUtils.getColor(flameLevel) + flameLevel, x * 2, enchantmentY)
                enchantmentY += 8f
            }
            if (unbreakingLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
        }
        if (stack.item is ItemSword) {
            val sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack)
            val knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack)
            val fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack)
            val unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)
            if (sharpnessLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "S" + ColorUtils.getColor(sharpnessLevel) + sharpnessLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
            if (knockbackLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "K" + ColorUtils.getColor(knockbackLevel) + knockbackLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
            if (fireAspectLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "F" + ColorUtils.getColor(fireAspectLevel) + fireAspectLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
            if (unbreakingLevel > 0) {
                RenderUtils.drawEnchantTag(
                    "U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel,
                    x * 2,
                    enchantmentY
                )
                enchantmentY += 8f
            }
        }
        if (stack.rarity == EnumRarity.EPIC) {
            pushMatrix()
            disableDepth()
            glScalef(0.5f, 0.5f, 0.5f)
            RenderUtils.drawOutlinedStringCock(
                Minecraft.getMinecraft().fontRendererObj,
                "God",
                (x * 2).toFloat(),
                enchantmentY,
                Color(255, 255, 0).rgb,
                Color(100, 100, 0, 200).rgb
            )
            glScalef(1.0f, 1.0f, 1.0f)
            enableDepth()
            popMatrix()
        }
    }
    fun drawOutlinedStringCock(fr: FontRenderer, s: String?, x: Float, y: Float, color: Int, outlineColor: Int) {
        fr.drawString(stripColor(s!!), (x - 1.0f).toInt(), y.toInt(), outlineColor)
        fr.drawString(stripColor(s), x.toInt(), (y - 1.0f).toInt(), outlineColor)
        fr.drawString(stripColor(s), (x + 1.0f).toInt(), y.toInt(), outlineColor)
        fr.drawString(stripColor(s), x.toInt(), (y + 1.0f).toInt(), outlineColor)
        fr.drawString(s, x.toInt(), y.toInt(), color)
    }
    private fun drawEnchantTag(text: String, x: Int, y: Float) {
        pushMatrix()
        disableDepth()
        glScalef(0.5f, 0.5f, 0.5f)
        RenderUtils.drawOutlinedStringCock(
            Minecraft.getMinecraft().fontRendererObj,
            text,
            x.toFloat(),
            y,
            -1,
            Color(0, 0, 0, 220).darker().rgb
        )
        glScalef(1.0f, 1.0f, 1.0f)
        enableDepth()
        popMatrix()
    }
    // Colors the next texture without a specified alpha value
    fun color(color: Int) {
        color(color, (color shr 24 and 255).toFloat() / 255.0f)
    }
    fun scaleStart(x: Float, y: Float, scale: Float) {
        glPushMatrix()
        glTranslatef(x, y, 0f)
        glScalef(scale, scale, 1f)
        glTranslatef(-x, -y, 0f)
    }
    fun renderRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Int) {
        drawGoodCircle(
            (x + radius).toDouble(),
            (y + radius).toDouble(),
            radius,
            color
        )
        drawGoodCircle(
            (x + width - radius).toDouble(),
            (y + radius).toDouble(),
            radius,
            color
        )
        drawGoodCircle(
            (x + radius).toDouble(),
            (y + height - radius).toDouble(),
            radius,
            color
        )
        drawGoodCircle(
            (x + width - radius).toDouble(),
            (y + height - radius).toDouble(),
            radius,
            color
        )
        drawRect2(x + radius, y, width - radius * 2, height, color)
        drawRect2(x, y + radius, width, height - radius * 2, color)
    }
    fun drawGoodCircle(x: Double, y: Double, radius: Float, color: Int) {
        color(color)
        GLUtils.setup2DRendering()
        glEnable(GL_POINT_SMOOTH)
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST)
        glPointSize(radius * (2 * mc.gameSettings.guiScale))
        glBegin(GL_POINTS)
        glVertex2d(x, y)
        glEnd()
        GLUtils.end2DRendering()
    }
    @JvmStatic
    fun scaleEnd() {
        glPopMatrix()
    }
    fun drawBlockBox(blockPos: BlockPos, color: Color, outline: Boolean) {
        val renderManager = mc.renderManager
        val timer = mc.timer

        val x = blockPos.x - renderManager.renderPosX
        val y = blockPos.y - renderManager.renderPosY
        val z = blockPos.z - renderManager.renderPosZ

        var axisAlignedBB = AxisAlignedBB.fromBounds(x, y, z, x + 1.0, y + 1.0, z + 1.0)
        val block = getBlock(blockPos)
        if (block != null) {
            val player = mc.thePlayer
            val posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * timer.renderPartialTicks.toDouble()
            val posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * timer.renderPartialTicks.toDouble()
            val posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * timer.renderPartialTicks.toDouble()
            block.setBlockBoundsBasedOnState(mc.theWorld, blockPos)
            axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
                .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
                .offset(-posX, -posY, -posZ)
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        enableGlCap(GL_BLEND)
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST)
        glDepthMask(false)
        glColor(color.red, color.green, color.blue, if (color.alpha != 255) color.alpha else if (outline) 26 else 35)
        drawFilledBox(axisAlignedBB)

        if (outline) {
            glLineWidth(1f)
            enableGlCap(GL_LINE_SMOOTH)
            glColor(color)
            drawSelectionBoundingBox(axisAlignedBB)
        }

        glColor4f(1f, 1f, 1f, 1f)
        glDepthMask(true)
        resetCaps()
    }
    @JvmStatic
    fun resetColor(){
        color(1f, 1f, 1f, 1f)
    }

    fun drawSelectionBoundingBox(boundingBox: AxisAlignedBB) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        worldRenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION)

        // Lower Rectangle
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()

        // Upper Rectangle
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()

        // Upper Rectangle
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        tessellator.draw()
    }
    @JvmStatic
    fun createFrameBuffer(framebuffer: Framebuffer?): Framebuffer? {
        return createFrameBuffer(framebuffer, false)
    }
    @JvmStatic
    fun createFrameBuffer(framebuffer: Framebuffer?, depth: Boolean): Framebuffer? {
        if (needsNewFramebuffer(framebuffer)) {
            framebuffer?.deleteFramebuffer()
            return Framebuffer(
                mc.displayWidth,
                mc.displayHeight,
                depth
            )
        }
        return framebuffer
    }
    @JvmStatic
    fun bindTexture(texture: Int) {
        glBindTexture(GL_TEXTURE_2D, texture)
    }
    @JvmStatic
    fun needsNewFramebuffer(framebuffer: Framebuffer?): Boolean {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight
    }
    @JvmStatic
    fun setAlphaLimit(limit: Float) {
        enableAlpha()
        alphaFunc(GL_GREATER, (limit * .01).toFloat())
    }
    fun drawEntityBox(entity: Entity, color: Color, outline: Boolean) {
        val renderManager = mc.renderManager
        val timer = mc.timer
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        enableGlCap(GL_BLEND)
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST)
        glDepthMask(false)
        val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks
                - renderManager.renderPosX)
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks
                - renderManager.renderPosY)
        val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks
                - renderManager.renderPosZ)
        val entityBox = entity.hitBox
        val axisAlignedBB = AxisAlignedBB.fromBounds(
            entityBox.minX - entity.posX + x - 0.05,
            entityBox.minY - entity.posY + y,
            entityBox.minZ - entity.posZ + z - 0.05,
            entityBox.maxX - entity.posX + x + 0.05,
            entityBox.maxY - entity.posY + y + 0.15,
            entityBox.maxZ - entity.posZ + z + 0.05
        )
        if (outline) {
            glLineWidth(1f)
            enableGlCap(GL_LINE_SMOOTH)
            glColor(color.red, color.green, color.blue, 95)
            drawSelectionBoundingBox(axisAlignedBB)
        }
        glColor(color.red, color.green, color.blue, if (outline) 26 else 35)
        drawFilledBox(axisAlignedBB)
        glColor4f(1f, 1f, 1f, 1f)
        glDepthMask(true)
        resetCaps()
    }

    fun drawAxisAlignedBB(axisAlignedBB: AxisAlignedBB, color: Color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_BLEND)
        glLineWidth(2f)
        glDisable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glColor(color)
        drawFilledBox(axisAlignedBB)
        glColor4f(1f, 1f, 1f, 1f)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
        glDisable(GL_BLEND)
    }

    fun drawPlatform(y: Double, color: Color, size: Double) {
        val renderManager = mc.renderManager
        val renderY = y - renderManager.renderPosY
        drawAxisAlignedBB(AxisAlignedBB.fromBounds(size, renderY + 0.02, size, -size, renderY, -size), color)
    }

    fun drawPlatform(entity: Entity, color: Color) {
        val renderManager = mc.renderManager
        val timer = mc.timer
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        val axisAlignedBB = entity.entityBoundingBox
            .offset(-entity.posX, -entity.posY, -entity.posZ)
            .offset(x, y, z)
        drawAxisAlignedBB(
            AxisAlignedBB.fromBounds(
                axisAlignedBB.minX,
                axisAlignedBB.maxY + 0.2,
                axisAlignedBB.minZ,
                axisAlignedBB.maxX,
                axisAlignedBB.maxY + 0.26,
                axisAlignedBB.maxZ
            ), color
        )
    }
    var ticks = 0.0
    var lastFrame: Long = 0
    fun drawTargetCapsule(entity: Entity, partialTicks: Float, rad: Double, color: Int, alpha: Float) {
        /*Got this from the people i made the Gui for*/
        ticks += .004 * (System.currentTimeMillis() - lastFrame)
        lastFrame = System.currentTimeMillis()
        glPushMatrix()
        glDisable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)
        color(1f, 1f, 1f, 1f)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glShadeModel(GL_SMOOTH)
        disableCull()
        val x: Double =
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().renderPosX
        val y: Double =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().renderPosY+ Math.sin(System.currentTimeMillis() / 2E+2) + 1
        val z: Double =
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().renderPosZ
        glBegin(GL_TRIANGLE_STRIP)
        run {
            var i = 0f
            while (i < Math.PI * 2) {
                val vecX = x + rad * Math.cos(i.toDouble())
                val vecZ = z + rad * Math.sin(i.toDouble())
                color(color, 0f)
                glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ)
                color(color, .52f * alpha)
                glVertex3d(vecX, y, vecZ)
                i += (Math.PI * 2 / 64f).toFloat()
            }
        }
        glEnd()
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        glLineWidth(1.5f)
        glBegin(GL_LINE_STRIP)
        color(1f, 1f, 1f, 1f)
        color(color, .5f * alpha)
        for (i in 0..180) {
            glVertex3d(x - Math.sin(i * Math.PI * 2f / 90) * rad, y, z + Math.cos(i * Math.PI * 2f / 90) * rad)
        }
        glEnd()
        glShadeModel(GL_FLAT)
        glDepthMask(true)
        glEnable(GL_DEPTH_TEST)
        enableCull()
        glDisable(GL_LINE_SMOOTH)
        glEnable(GL_TEXTURE_2D)
        glPopMatrix()
        glColor4f(1f, 1f, 1f, 1f)
    }
    fun renderBoundingBox(entityLivingBase: EntityLivingBase?, color: Color, alpha: Float) {
        val bb = ESPUtil.getInterpolatedBoundingBox(entityLivingBase)
        pushMatrix()
        GLUtils.setup2DRendering()
        GLUtils.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glLineWidth(3f)
        glColor4f(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), alpha)
        color(color.rgb, alpha)
        renderCustomBoundingBox(bb, false, true)
        glDepthMask(true)
        glEnable(GL_DEPTH_TEST)
        GLUtils.disableCaps()
        GLUtils.end2DRendering()
        popMatrix()
    }
    fun renderCustomBoundingBox(bb: AxisAlignedBB, outline: Boolean, filled: Boolean) {
        if (outline) {
            glBegin(GL_LINE_STRIP)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glEnd()
            glBegin(GL_LINE_STRIP)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glEnd()
            glBegin(GL_LINES)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glEnd()
        }
        if (filled) {
            glBegin(7)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glEnd()
            glBegin(7)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glEnd()
            glBegin(7)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glEnd()
            glBegin(7)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glEnd()
            glBegin(7)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glEnd()
            glBegin(7)
            glVertex3d(bb.minX, bb.maxY, bb.maxZ)
            glVertex3d(bb.minX, bb.minY, bb.maxZ)
            glVertex3d(bb.minX, bb.maxY, bb.minZ)
            glVertex3d(bb.minX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.minZ)
            glVertex3d(bb.maxX, bb.minY, bb.minZ)
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ)
            glVertex3d(bb.maxX, bb.minY, bb.maxZ)
            glEnd()
        }
    }
    fun drawFilledBox(axisAlignedBB: AxisAlignedBB) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        worldRenderer.begin(7, DefaultVertexFormats.POSITION)
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        tessellator.draw()
    }

    fun quickDrawRect(x: Float, y: Float, x2: Float, y2: Float) {
        glBegin(GL_QUADS)
        glVertex2d(x2.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
    }
    @JvmStatic
    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glColor(color)
        glBegin(GL_QUADS)
        glVertex2f(x2, y)
        glVertex2f(x, y)
        glVertex2f(x, y2)
        glVertex2f(x2, y2)
        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
    }
    fun drawRect2(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        drawRect(x,y,x+x2,y+y2,color)
    }
    fun drawRect(x: Int, y: Int, x2: Int, y2: Int, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glColor(color)
        glBegin(GL_QUADS)
        glVertex2i(x2, y)
        glVertex2i(x, y)
        glVertex2i(x, y2)
        glVertex2i(x2, y2)
        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
    }

    /**
     * Like [.drawRect], but without setup
     */
    fun quickDrawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        glColor(color)
        glBegin(GL_QUADS)
        glVertex2d(x2.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
    }

    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Color) = drawRect(x, y, x2, y2, color.rgb)

    fun drawBorderedRect(x: Float, y: Float, x2: Float, y2: Float, width: Float, color1: Int, color2: Int) {
        drawRect(x, y, x2, y2, color2)
        drawBorder(x, y, x2, y2, width, color1)
    }

    fun drawBorderedRect(x: Int, y: Int, x2: Int, y2: Int, width: Int, borderColor: Int, rectColor: Int) {
        drawRect(x, y, x2, y2, rectColor)
        drawBorder(x, y, x2, y2, width, borderColor)
    }

    fun drawBorder(x: Float, y: Float, x2: Float, y2: Float, width: Float, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glColor(color)
        glLineWidth(width)
        glBegin(GL_LINE_LOOP)
        glVertex2d(x2.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
    }

    fun drawBorder(x: Int, y: Int, x2: Int, y2: Int, width: Int, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glColor(color)
        glLineWidth(width.toFloat())
        glBegin(GL_LINE_LOOP)
        glVertex2i(x2, y)
        glVertex2i(x, y)
        glVertex2i(x, y2)
        glVertex2i(x2, y2)
        glEnd()
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
    }

    fun quickDrawBorderedRect(x: Float, y: Float, x2: Float, y2: Float, width: Float, color1: Int, color2: Int) {
        quickDrawRect(x, y, x2, y2, color2)
        glColor(color1)
        glLineWidth(width)
        glBegin(GL_LINE_LOOP)
        glVertex2d(x2.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
    }

    fun drawLoadingCircle(x: Float, y: Float) {
        for (i in 0..3) {
            val rot = (System.nanoTime() / 5000000 * i % 360).toInt()
            drawCircle(x, y, (i * 10).toFloat(), rot - 180, rot)
        }
    }

    fun drawCircle(x: Float, y: Float, radius: Float, start: Int, end: Int) {
        enableBlend()
        disableTexture2D()
        tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glColor(Color.WHITE)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(2f)
        glBegin(GL_LINE_STRIP)
        var i = end.toFloat()
        while (i >= start) {
            val rad = i.toRadians()
            glVertex2f(
                x + cos(rad) * (radius * 1.001f),
                y + sin(rad) * (radius * 1.001f)
            )
            i -= 360 / 90f
        }
        glEnd()
        glDisable(GL_LINE_SMOOTH)
        enableTexture2D()
        disableBlend()
    }

    fun drawFilledCircle(xx: Int, yy: Int, radius: Float, color: Color) {
        val sections = 50
        val dAngle = 2 * Math.PI / sections
        var x: Float
        var y: Float
        glPushAttrib(GL_ENABLE_BIT)
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)
        for (i in 0 until sections) {
            x = (radius * sin(i * dAngle)).toFloat()
            y = (radius * cos(i * dAngle)).toFloat()
            glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            glVertex2f(xx + x, yy + y)
        }
        glColor4f(1f, 1f, 1f, 1f)
        glEnd()
        glPopAttrib()
    }
    @JvmStatic
    fun drawImage(image: ResourceLocation?, x: Int, y: Int, width: Int, height: Int) {
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDepthMask(false)
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(image)
        drawModalRectWithCustomSizedTexture(
            x.toFloat(),
            y.toFloat(),
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            width.toFloat(),
            height.toFloat()
        )
        glDepthMask(true)
        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    fun drawModalRectWithCustomSizedTexture(
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        width: Float,
        height: Float,
        textureWidth: Float,
        textureHeight: Float
    ) {
        val f = 1f / textureWidth
        val f1 = 1f / textureHeight
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0)
            .tex((u * f).toDouble(), ((v + height) * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
            .tex(((u + width) * f).toDouble(), ((v + height) * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), y.toDouble(), 0.0)
            .tex(((u + width) * f).toDouble(), (v * f1).toDouble()).endVertex()
        worldrenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height.
     */
    fun drawTexturedModalRect(x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int, zLevel: Float) {
        val f = 0.00390625f
        val f1 = 0.00390625f
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x.toDouble(), (y + height).toDouble(), zLevel.toDouble()).tex((textureX.toFloat() * f).toDouble(), ((textureY + height).toFloat() * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), zLevel.toDouble()).tex(((textureX + width).toFloat() * f).toDouble(), ((textureY + height).toFloat() * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), y.toDouble(), zLevel.toDouble()).tex(((textureX + width).toFloat() * f).toDouble(), (textureY.toFloat() * f1).toDouble()).endVertex()
        worldrenderer.pos(x.toDouble(), y.toDouble(), zLevel.toDouble()).tex((textureX.toFloat() * f).toDouble(), (textureY.toFloat() * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) =
        glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f)

    fun glColor(color: Color) = glColor(color.red, color.green, color.blue, color.alpha)

    private fun glColor(hex: Int) =
        glColor(hex shr 16 and 0xFF, hex shr 8 and 0xFF, hex and 0xFF, hex shr 24 and 0xFF)

    fun draw2D(entity: EntityLivingBase, posX: Double, posY: Double, posZ: Double, color: Int, backgroundColor: Int) {
        glPushMatrix()
        glTranslated(posX, posY, posZ)
        glRotated(-mc.renderManager.playerViewY.toDouble(), 0.0, 1.0, 0.0)
        glScaled(-0.1, -0.1, 0.1)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDepthMask(true)
        glColor(color)
        glCallList(DISPLAY_LISTS_2D[0])
        glColor(backgroundColor)
        glCallList(DISPLAY_LISTS_2D[1])
        glTranslated(0.0, 21 + -(entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) * 12, 0.0)
        glColor(color)
        glCallList(DISPLAY_LISTS_2D[2])
        glColor(backgroundColor)
        glCallList(DISPLAY_LISTS_2D[3])

        // Stop render
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glPopMatrix()
    }

    fun draw2D(blockPos: BlockPos, color: Int, backgroundColor: Int) {
        val renderManager = mc.renderManager
        val posX = blockPos.x + 0.5 - renderManager.renderPosX
        val posY = blockPos.y - renderManager.renderPosY
        val posZ = blockPos.z + 0.5 - renderManager.renderPosZ
        glPushMatrix()
        glTranslated(posX, posY, posZ)
        glRotated(-mc.renderManager.playerViewY.toDouble(), 0.0, 1.0, 0.0)
        glScaled(-0.1, -0.1, 0.1)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDepthMask(true)
        glColor(color)
        glCallList(DISPLAY_LISTS_2D[0])
        glColor(backgroundColor)
        glCallList(DISPLAY_LISTS_2D[1])
        glTranslated(0.0, 9.0, 0.0)
        glColor(color)
        glCallList(DISPLAY_LISTS_2D[2])
        glColor(backgroundColor)
        glCallList(DISPLAY_LISTS_2D[3])

        // Stop render
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glPopMatrix()
    }

    fun renderNameTag(string: String, x: Double, y: Double, z: Double) {
        val renderManager = mc.renderManager
        glPushMatrix()
        glTranslated(x - renderManager.renderPosX, y - renderManager.renderPosY, z - renderManager.renderPosZ)
        glNormal3f(0f, 1f, 0f)
        glRotatef(-mc.renderManager.playerViewY, 0f, 1f, 0f)
        glRotatef(mc.renderManager.playerViewX, 1f, 0f, 0f)
        glScalef(-0.05f, -0.05f, 0.05f)
        setGlCap(GL_LIGHTING, false)
        setGlCap(GL_DEPTH_TEST, false)
        setGlCap(GL_BLEND, true)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        val width = Fonts.font35.getStringWidth(string) / 2
        drawRect(-width - 1, -1, width + 1, Fonts.font35.FONT_HEIGHT, Int.MIN_VALUE)
        Fonts.font35.drawString(string, -width.toFloat(), 1.5f, Color.WHITE.rgb, true)
        resetCaps()
        glColor4f(1f, 1f, 1f, 1f)
        glPopMatrix()
    }

    fun drawLine(x: Double, y: Double, x1: Double, y1: Double, width: Float) {
        glDisable(GL_TEXTURE_2D)
        glLineWidth(width)
        glBegin(GL_LINES)
        glVertex2d(x, y)
        glVertex2d(x1, y1)
        glEnd()
        glEnable(GL_TEXTURE_2D)
    }

    fun makeScissorBox(x: Float, y: Float, x2: Float, y2: Float) {
        val scaledResolution = ScaledResolution(mc)
        val factor = scaledResolution.scaleFactor
        glScissor(
            (x * factor).toInt(),
            ((scaledResolution.scaledHeight - y2) * factor).toInt(),
            ((x2 - x) * factor).toInt(),
            ((y2 - y) * factor).toInt()
        )
    }

    /**
     * GL CAP MANAGER
     *
     *
     * TODO: Remove gl cap manager and replace by something better
     */

    fun resetCaps() = glCapMap.forEach { (cap, state) -> setGlState(cap, state) }

    fun enableGlCap(cap: Int) = setGlCap(cap, true)

    fun enableGlCap(vararg caps: Int) {
        for (cap in caps) setGlCap(cap, true)
    }

    fun disableGlCap(cap: Int) = setGlCap(cap, true)

    fun disableGlCap(vararg caps: Int) {
        for (cap in caps) setGlCap(cap, false)
    }

    fun setGlCap(cap: Int, state: Boolean) {
        glCapMap[cap] = glGetBoolean(cap)
        setGlState(cap, state)
    }

    fun setGlState(cap: Int, state: Boolean) = if (state) glEnable(cap) else glDisable(cap)

    fun drawScaledCustomSizeModalRect(
        x: Int,
        y: Int,
        u: Float,
        v: Float,
        uWidth: Int,
        vHeight: Int,
        width: Int,
        height: Int,
        tileWidth: Float,
        tileHeight: Float
    ) {
        val f = 1f / tileWidth
        val f1 = 1f / tileHeight
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0)
            .tex((u * f).toDouble(), ((v + vHeight.toFloat()) * f1).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
            .tex(((u + uWidth.toFloat()) * f).toDouble(), ((v + vHeight.toFloat()) * f1).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), y.toDouble(), 0.0)
            .tex(((u + uWidth.toFloat()) * f).toDouble(), (v * f1).toDouble()).endVertex()
        worldRenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }


}