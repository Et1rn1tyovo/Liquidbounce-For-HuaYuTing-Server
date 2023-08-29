/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.ccbluex.liquidbounce.utils.extensions.toRadiansD
import javax.vecmath.Vector2f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance() {

    var speed
        get() = sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ).toFloat()
        set(value) = strafe(value)

    val isMoving
        get() = mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f)

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()

    val hasMotion
        get() = mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0 && mc.thePlayer.motionY != 0.0
    fun getMoveYaw(yaw: Float): Float {
        var yaw = yaw
        val from =
            Vector2f(mc.thePlayer.lastTickPosX as Float, mc.thePlayer.lastTickPosZ as Float)
        val to = Vector2f(mc.thePlayer.posX as Float, mc.thePlayer.posZ as Float)
        val diff = Vector2f(to.x - from.x, to.y - from.y)
        val x: Double = diff.x.toDouble()
        val z: Double = diff.y.toDouble()
        if (x != 0.0 && z != 0.0) {
            yaw = Math.toDegrees((Math.atan2(-x, z) + Math.PI * 2f) % Math.PI * 2f).toFloat()
        }
        return yaw
    }
    fun strafe(speed: Float = this.speed, stopWhenNoInput: Boolean = false) {
        if (!isMoving) {
            if (stopWhenNoInput)
                mc.thePlayer.stopXZ()

            return
        }

        val yaw = direction
        mc.thePlayer.motionX = -sin(yaw) * speed
        mc.thePlayer.motionZ = cos(yaw) * speed
    }

    fun forward(length: Double) {
        val thePlayer = mc.thePlayer ?: return
        val yaw = thePlayer.rotationYaw.toRadiansD()
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }

    val direction: Double
        get() {
            val thePlayer = mc.thePlayer
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return rotationYaw.toRadiansD()
        }

    fun isOnGround(height: Double) =
        mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer,
                mc.thePlayer.entityBoundingBox.offset(0.0, -height, 0.0)
            ).isNotEmpty()
}