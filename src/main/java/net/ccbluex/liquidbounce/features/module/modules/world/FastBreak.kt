/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition

object FastBreak : Module("FastBreak", ModuleCategory.WORLD) {

    private val breakDamage by FloatValue("BreakDamage", 0.8F, 0.1F..1F)
    private val ticks by IntegerValue("Tick", 10, 0..50)
    val timer = MSTimer()
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0
        if (timer.hasTimePassed(ticks)){
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                mc.thePlayer.sendQueue.addToSendQueue(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
            }

            if (mc.playerController.curBlockDamageMP > breakDamage)
                mc.playerController.curBlockDamageMP = 1F

            if (Fucker.currentDamage > breakDamage)
                Fucker.currentDamage = 1F

            if (Nuker.currentDamage > breakDamage)
                Nuker.currentDamage = 1F
        }
        timer.reset()
    }
}
