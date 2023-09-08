/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.animations
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.defaultAnimation
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11.glTranslated

/**
 * Animations module
 *
 * This module affects the blocking animation. It allows the user to customize the animation.
 * If you are looking forward to contribute to this module, please name your animation with a reasonable name. Do not name them after clients or yourself.
 * Please credit from where you got the animation from and make sure they are willing to contribute.
 * If they are not willing to contribute, please do not add the animation to this module.
 *
 * If you are looking for the animation classes, please look at the [Animation] class. It allows you to create your own animation.
 * After making your animation class, please add it to the [animations] array. It should automatically be added to the list and show up in the GUI.
 *
 * By default, the module uses the [OneSevenAnimation] animation. If you want to change the default animation, please change the [defaultAnimation] variable.
 * Default animations are even used when the module is disabled.
 *
 * If another variables from the renderItemInFirstPerson method are needed, please let me know or pass them by yourself.
 *
 * @author CCBlueX
 */
object Animations : Module("Animations", ModuleCategory.RENDER) {

    // Default animation
    val defaultAnimation = OldAnimation()

    private val animations = arrayOf(
        OldAnimation(),
        SwangAnimation(),
        SmoothAnimation(),
        PunchAnimation(),
        PushAnimation(),
        LeakedAnimation(),
        ExhiExhiAnimation(),
        OldExhiAnimation()
    )

    private val animationMode by ListValue("Mode", animations.map { it.name }.toTypedArray(), "Pushdown")
    val oddSwing by BoolValue("OddSwing", false)

    fun getAnimation() = animations.firstOrNull { it.name == animationMode }
    override val tag: String?
        get() = animations.firstOrNull { it.name == animationMode }!!.name
}

/**
 * Sword Animation
 *
 * This class allows you to create your own animation.
 * It transforms the item in the hand and the known functions from Mojang are directly accessible as well.
 *
 * @author CCBlueX
 */
abstract class Animation(val name: String) : MinecraftInstance() {
    abstract fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer)
    /**
     * Transforms the block in the hand
     *
     * @author Mojang
     */
    protected fun doBlockTransformations() {
        translate(-0.5f, 0.2f, 0f)
        rotate(30f, 0f, 1f, 0f)
        rotate(-80f, 1f, 0f, 0f)
        rotate(60f, 0f, 1f, 0f)
    }

    /**
     * Transforms the item in the hand
     *
     * @author Mojang
     */
    protected fun transformFirstPersonItem(equipProgress: Float, swingProgress: Float) {
        translate(0.56f, -0.52f, -0.71999997f)
        translate(0f, equipProgress * -0.6f, 0f)
        rotate(45f, 0f, 1f, 0f)
        val f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f)
        val f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f)
        rotate(f * -20f, 0f, 1f, 0f)
        rotate(f1 * -20f, 0f, 0f, 1f)
        rotate(f1 * -80f, 1f, 0f, 0f)
        scale(0.4f, 0.4f, 0.4f)
    }

}

/**
 * OneSeven animation (default). Similar to the 1.7 blocking animation.
 *
 * @author CCBlueX
 */
class LeakedAnimation : Animation("Leaked") {
    override fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        val convertedProgress = MathHelper.sin(MathHelper.sqrt_float(f1) * Math.PI.toFloat());
        transformFirstPersonItem(f, 0.0f)
        translate(0.0f, 0.1f, 0.0f)
        doBlockTransformations()
        rotate(convertedProgress * 35.0f / 2.0f, 0.0f, 1.0f, 1.5f)
        rotate(-convertedProgress * 135.0f / 4.0f, 1.0f, 1.0f, 0.0f)
    }

}
class PushAnimation : Animation("Push") {
    override fun transform(swingProgress: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        val equippedProgress1: Float = (1.0f
                - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * p))
        translate(0.56f, -0.52f, -0.71999997f)
        translate(0.0f, equippedProgress1 * -0.6f, 0.0f)

        rotate(45.0f, 0.0f, 1.0f, 0.0f)

        val f4 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * Math.PI.toFloat())

        rotate(f4 * -20.0f, 0.0f, 1.0f, 0.0f)
        rotate(f5 * -20.0f, 0.0f, 0.0f, 1.0f)

        scale(0.4f, 0.4f, 0.4f)
        doBlockTransformations()
    }

}
class SwangAnimation : Animation("Swang") {
    override fun transform(swingProgress: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        transformFirstPersonItem(f / 2.0f, swingProgress)
        val var152 = MathHelper.sin((MathHelper.sqrt_float(swingProgress) * Math.PI).toFloat())
        rotate(var152 * 30.0f / 2.0f, -var152, -0.0f, 9.0f)
        rotate(var152 * 40.0f, 1.0f, -var152 / 2.0f, -0.0f)
        func_178103_d(0.4f)
    }

}
class SmoothAnimation : Animation("Smooth") {
    override fun transform(swingProgress: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        val convertedProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * Math.PI.toFloat());
        transformFirstPersonItem(f / 1.5f, 0.0f)
        func_178103_d(0.2f)
        translate(-0.05f, 0.3f, 0.3f)
        rotate(-convertedProgress * 140.0f, 8.0f, 0.0f, 8.0f)
        rotate(convertedProgress * 90.0f, 8.0f, 0.0f, 8.0f)
    }

}
class PunchAnimation : Animation("Punch") {
    override fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        val convertedProgress = MathHelper.sin(MathHelper.sqrt_float(f1) * Math.PI.toFloat());
        transformFirstPersonItem(f, 0.0f)
        func_178103_d(0.2f)
        translate(0.1f, 0.2f, 0.3f)
        rotate(-convertedProgress * 30.0f, -5.0f, 0.0f, 9.0f)
        rotate(-convertedProgress * 10.0f, 1.0f, -0.4f, -0.5f)
    }

}
class OldAnimation : Animation("1.7") {
    override fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        val equippedProgress3: Float = (1.0f
                - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * p))
        transformFirstPersonItem(equippedProgress3, f1)
        doBlockTransformations()
    }
}
class OldExhiAnimation : Animation("Old Exhibition") {
    override fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        glTranslated(-0.04, 0.13, 0.0)
        transformFirstPersonItem(f / 2.5f, 0.0f)
        rotate(-MathHelper.sin((MathHelper.sqrt_float(f1) * Math.PI).toFloat()) * 40.0f / 2.0f, MathHelper.sin((MathHelper.sqrt_float(f1) * Math.PI).toFloat()) / 2.0f, 1.0f, 4.0f)
        rotate(-MathHelper.sin((MathHelper.sqrt_float(f1) * Math.PI).toFloat()) * 30.0f, 1.0f, MathHelper.sin((MathHelper.sqrt_float(f1) * Math.PI).toFloat()) / 3.0f, -0.0f)
        func_178103_d(0.2f)
    }
}
class ExhiExhiAnimation : Animation("Exhibition") {
    override fun transform(f1: Float,f :Float,prevEquippedProgress: Float,equippedProgress: Float,p : Float, clientPlayer: AbstractClientPlayer){
        glTranslated(-0.04, 0.13, 0.0)
        val convertedProgress2 = MathHelper.sin(MathHelper.sqrt_float(f1) * Math.PI.toFloat())
        transformFirstPersonItem(f / 1.5f, 0.0f)
        //translate(0.0f, 0.2f, -0.0f)
        rotate(-convertedProgress2 * 31.0f, 1.0f, 0.0f, 2.0f)
        rotate(
            -convertedProgress2 * 33.0f, 1.5f,
            convertedProgress2 / 1.1f, 0.0f
        )
        doBlockTransformations()
    }
}
private fun func_178103_d(qq: Float) {
    translate(-0.5f, qq, 0.0f)
    rotate(30.0f, 0.0f, 1.0f, 0.0f)
    rotate(-80.0f, 1.0f, 0.0f, 0.0f)
    rotate(60.0f, 0.0f, 1.0f, 0.0f)
}
/**
 * Pushdown animation
 */