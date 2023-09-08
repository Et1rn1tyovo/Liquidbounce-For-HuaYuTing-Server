/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition

object NoSlowDown : Module("NoSlowDown", ModuleCategory.MOVEMENT,0,true,true,"NoSlowDown","No Slow Down") {

    // Highly customizable values
    private val mode by ListValue("Mode", arrayOf("Vanilla","GrimAC","Hypixel"),"Vanilla")

    private val blockForwardMultiplier by FloatValue("BlockForwardMultiplier", 1f, 0.2F..1f)
    private val blockStrafeMultiplier by FloatValue("BlockStrafeMultiplier", 1f, 0.2F..1f)

    private val consumeForwardMultiplier by FloatValue("ConsumeForwardMultiplier", 1f, 0.2F..1f)
    private val consumeStrafeMultiplier by FloatValue("ConsumeStrafeMultiplier", 1f, 0.2F..1f)

    private val bowForwardMultiplier by FloatValue("BowForwardMultiplier", 1f, 0.2F..1f)
    private val bowStrafeMultiplier by FloatValue("BowStrafeMultiplier", 1f, 0.2F..1f)

    //val c08 by BoolValue("C08", true){ mode.equals("GrimAC")}
    // Blocks
    val soulsand by BoolValue("Soulsand", true)
    val liquidPush by BoolValue("LiquidPush", true)

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if ((mc.thePlayer.isBlocking || mc.thePlayer.isUsingItem) && MovementUtils.isMoving) {
            when (mode.lowercase()) {
                "vanilla" -> {

                }

                "hypixel" -> {
                    if (mc.gameSettings.keyBindUseItem.isKeyDown && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem()
                            .getItem() is ItemFood || mc.thePlayer.getHeldItem()
                            .getItem() is ItemBucketMilk || mc.thePlayer.getHeldItem()
                            .getItem() is ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata()))
                    ) {
                        RotationUtils.setRotation(Rotation(mc.thePlayer.rotationYaw,90f),1,true,false,180)
                    }
                }

                "grimac" -> {
                    //if (event.eventState == EventState.PRE) {
                    //    mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 8))
                    //    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    //}
                    //if (event.eventState == EventState.POST && c08) {
                    //    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                    //}
                    if ((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && !mc.thePlayer!!.isBlocking && (
                        mc.thePlayer!!.heldItem!!.item is ItemFood
                        || (mc.thePlayer!!.heldItem!!.item is ItemPotion && !ItemPotion.isSplash(mc.thePlayer!!.heldItem!!.item.getMetadata(mc.thePlayer!!.heldItem!!.metadata)))
                        || mc.thePlayer!!.heldItem!!.item is ItemBow
                        || mc.thePlayer!!.heldItem!!.item is ItemBucketMilk)
                    ) {
                        if (mc.thePlayer!!.isUsingItem) {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 8))
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        }
                    }
                    if (event.eventState == EventState.PRE && (mc.thePlayer!!.heldItem!!.item is ItemSword)) {

                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 8))
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    }
                }
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) =
            when (item) {
                is ItemFood, is ItemPotion, is ItemBucketMilk ->
                    if (isForward) consumeForwardMultiplier else consumeStrafeMultiplier

                is ItemSword ->
                    if (isForward) blockForwardMultiplier else blockStrafeMultiplier

                is ItemBow ->
                    if (isForward) bowForwardMultiplier else bowStrafeMultiplier

                else -> 0.2F
            }
    override val tag
        get() = mode
}
