/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.MovementUtils.isOnGround
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.toRadians
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextInt
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.ThreadQuickExitException
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.cos
import kotlin.math.sin

object Velocity : Module("Velocity", ModuleCategory.COMBAT) {

    /**
     * OPTIONS
     */
    private val mode by ListValue("Mode", arrayOf("Simple", "AAC", "AACPush", "AACZero", "AACv4",
        "Reverse", "SmoothReverse", "Jump", "Glitch", "Legit","GrimAC"), "Simple")

    private val horizontal by FloatValue("Horizontal", 0F, 0F..1F) { mode in arrayOf("Simple", "AAC", "Legit") }
    private val vertical by FloatValue("Vertical", 0F, 0F..1F) { mode in arrayOf("Simple", "Legit") }

    // Reverse
    private val reverseStrength by FloatValue("ReverseStrength", 1F, 0.1F..1F) { mode == "Reverse" }
    private val reverse2Strength by FloatValue("SmoothReverseStrength", 0.05F, 0.02F..0.1F) { mode == "SmoothReverse" }

    // AAC Push
    private val aacPushXZReducer by FloatValue("AACPushXZReducer", 2F, 1F..3F) { mode == "AACPush" }
    private val aacPushYReducer by BoolValue("AACPushYReducer", true) { mode == "AACPush" }

    // AAC v4
    private val aacv4MotionReducer by FloatValue("AACv4MotionReducer", 0.62F, 0F..1F) { mode == "AACv4" }

    // Legit
    private val legitDisableInAir by BoolValue("DisableInAir", true) { mode == "Legit" }
    private val legitChance by IntegerValue("Chance", 100, 0..100) { mode == "Legit" }

    //Grim
    private val AutoDisableMode = ListValue("AutoDisable",arrayOf("Safe", "Silent"),"Silent"){ mode == "GrimAC" }

    private val AutoSilent = IntegerValue("SilentTicks",10,0,10)
    private val OnlyMove by BoolValue("OnlyMove", true) { mode == "GrimAC" }
    private val OnlyGround by BoolValue("OnlyGround", true) { mode == "GrimAC" }
    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    // SmoothReverse
    private var reverseHurt = false

    // AACPush
    private var jump = false

    override val tag
        get() = mode
    //GrimAC
    var cancelPackets = 0
    private var resetPersec = 8
    private var updates = 0
    private var S08 = 0
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private var disableLogger = false
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()


    override fun onEnable() {
        super.onEnable()
        if (mc.thePlayer == null) return
        inBus.clear()
        cancelPackets = 0
    }

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
        super.onDisable()
        if (mc.thePlayer == null) return
        inBus.clear()
        cancelPackets = 0
        blink()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (mode.lowercase()) {
            "jump" ->
                if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                    thePlayer.motionY = 0.42

                    val yaw = thePlayer.rotationYaw.toRadians()

                    thePlayer.motionX -= sin(yaw) * 0.2
                    thePlayer.motionZ += cos(yaw) * 0.2
                }

            "glitch" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionY = 0.4

                velocityInput = false
            }
            "grimac"->{
                if((OnlyMove&&!MovementUtils.isMoving)||(OnlyGround&&!mc.thePlayer!!.onGround)){return}
                updates++
                if (resetPersec > 0) {
                    if (updates >= 0) {
                        updates = 0
                        if (cancelPackets > 0){
                            cancelPackets--
                        }
                    }
                }
                if(cancelPackets == 0){
                    blink()
                }
            }
            "reverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    speed *= reverseStrength
                } else if (velocityTimer.hasTimePassed(80))
                    velocityInput = false
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    thePlayer.speedInAir = 0.02F
                    return
                }

                if (thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!thePlayer.onGround) {
                    if (reverseHurt)
                        thePlayer.speedInAir = reverse2Strength
                } else if (velocityTimer.hasTimePassed(80)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(80)) {
                thePlayer.motionX *= horizontal
                thePlayer.motionZ *= horizontal
                //mc.thePlayer.motionY *= vertical ?
                velocityInput = false
            }

            "aacv4" ->
                if (thePlayer.hurtTime>0 && !thePlayer.onGround){
                    val reduce = aacv4MotionReducer
                    thePlayer.motionX *= reduce
                    thePlayer.motionZ *= reduce
                }

            "aacpush" -> {
                if (jump) {
                    if (thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (thePlayer.hurtTime > 0 && thePlayer.motionX != 0.0 && thePlayer.motionZ != 0.0)
                        thePlayer.onGround = true

                    // Reduce Y
                    if (thePlayer.hurtResistantTime > 0 && aacPushYReducer && !Speed.state)
                        thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducer

                    thePlayer.motionX /= reduce
                    thePlayer.motionZ /= reduce
                }
            }

            "aaczero" ->
                if (thePlayer.hurtTime > 0) {
                    if (!velocityInput || thePlayer.onGround || thePlayer.fallDistance > 2F)
                        return

                    thePlayer.motionY -= 1.0
                    thePlayer.isAirBorne = true
                    thePlayer.onGround = true
                } else
                    velocityInput = false

            "legit" -> {
                if (legitDisableInAir && !isOnGround(0.5))
                    return

                if (mc.thePlayer.maxHurtResistantTime != mc.thePlayer.hurtResistantTime || mc.thePlayer.maxHurtResistantTime == 0)
                    return

                if (nextInt(endExclusive = 100) < legitChance) {
                    val horizontal = horizontal / 100f
                    val vertical = vertical / 100f

                    thePlayer.motionX *= horizontal.toDouble()
                    thePlayer.motionZ *= horizontal.toDouble()
                    thePlayer.motionY *= vertical.toDouble()
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet is S12PacketEntityVelocity && (mc.theWorld?.getEntityByID(packet.entityID) ?: return) == thePlayer) {
            velocityTimer.reset()

            when (mode.lowercase()) {
                "simple" -> {
                    val horizontal = horizontal
                    val vertical = vertical

                    if (horizontal + vertical > 0.0) {
                        packet.motionX = (packet.motionX * horizontal).toInt()
                        packet.motionY = (packet.motionY * vertical).toInt()
                        packet.motionZ = (packet.motionZ * horizontal).toInt()
                    } else {
                        event.cancelEvent()
                    }
                }
                "grimac"-> {
                    if((OnlyMove&&!MovementUtils.isMoving)||(OnlyGround&&!mc.thePlayer!!.onGround)){
                        return
                    }

                    val packet = event.packet

                    if(S08>0){
                        S08--
                        return
                    }

                    if(packet is S08PacketPlayerPosLook){
                        if(AutoDisableMode.get().equals("silent", ignoreCase = true)){
                            S08 = AutoSilent.get()
                        }
                        if(AutoDisableMode.get().equals("safe", ignoreCase = true)){
                            state = false
                        }
                    }

                    if (packet is S12PacketEntityVelocity) {
                        if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) {
                            return
                        }
                        if(MovementUtils.isMoving) {
                            event.cancelEvent()
                        } else {
                            if (mc.thePlayer.onGround) {
                                packet.motionX = 0
                                packet.motionY = 0
                                packet.motionZ = 0
                            } else  {
                                event.cancelEvent()
                            }
                        }
                        event.cancelEvent()
                        cancelPackets = 3
                    }

                    if(cancelPackets > 0){
                        if(MovementUtils.isMoving || !OnlyMove){
                            if (mc.thePlayer == null || disableLogger) return
                            if (packet is C03PacketPlayer) // Cancel all movement stuff
                                event.cancelEvent()
                            if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
                                packet is C08PacketPlayerBlockPlacement ||
                                packet is C0APacketAnimation ||
                                packet is C0BPacketEntityAction || packet is C02PacketUseEntity
                            ) {
                                event.cancelEvent()
                                packets.add(packet)
                            }
                            if(packet::class.java.simpleName.startsWith("S", true)) {
                                if(packet is S12PacketEntityVelocity && (mc.theWorld?.getEntityByID(packet.entityID) ?: return) == mc.thePlayer){return}
                                event.cancelEvent()
                                inBus.add(packet as Packet<INetHandlerPlayClient>)
                            }
                        }
                    }
                }
                "aac", "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }
            }
        } else if (packet is S27PacketExplosion) {
            when (mode.lowercase()) {
                "simple" -> {
                    val horizontal = horizontal
                    val vertical = vertical

                    if (horizontal + vertical > 0.0) {
                        mc.thePlayer.motionX += packet.func_149149_c() * horizontal
                        mc.thePlayer.motionY += packet.func_149144_d() * vertical
                        mc.thePlayer.motionZ += packet.func_149147_e() * horizontal
                    } else {
                        event.cancelEvent()
                    }
                }

                "aac", "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }
            }


        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (mode.lowercase()) {
            "aacpush" -> {
                jump = true

                if (!thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aaczero" ->
                if (thePlayer.hurtTime > 0)
                    event.cancelEvent()
        }
    }
    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            while (!inBus.isEmpty()) {
                inBus.poll()?.processPacket(mc.netHandler)
            }
            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
    }
}
