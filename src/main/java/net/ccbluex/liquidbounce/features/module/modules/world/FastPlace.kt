/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import com.google.common.eventbus.Subscribe
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemEgg
import net.minecraft.item.ItemSnowball
import java.lang.reflect.Field

object FastPlace : Module("FastPlace", ModuleCategory.WORLD) {
    val rightClickDelayTimerField: Field? = null
    val speed by IntegerValue("Speed", 0, 0..4)
    val onlyBlocks by BoolValue("OnlyBlocks", true)
    val facingBlocks by BoolValue("OnlyWhenFacingBlocks", true)
    fun canBeEnabled(): Boolean {
        return rightClickDelayTimerField != null
    }
    @EventTarget
    fun onTick(event: net.ccbluex.liquidbounce.event.TickEvent?) {
        if ((mc.thePlayer != null && mc.theWorld != null) && mc.inGameHasFocus && FastPlace.rightClickDelayTimerField != null) {
            if (onlyBlocks) {
                val item = mc.thePlayer.heldItem
                if (item != null && item.item is ItemBlock) {
                    try {
                        val c = speed
                        if (c == 0) {
                            FastPlace.rightClickDelayTimerField.set(mc, 0)
                        } else {
                            if (c == 4) {
                                return
                            }
                            val d: Int = FastPlace.rightClickDelayTimerField.getInt(mc)
                            if (d == 4) {
                                FastPlace.rightClickDelayTimerField.set(mc, c)
                            }
                        }
                    } catch (ignored: IllegalAccessException) {
                    } catch (ignored: IndexOutOfBoundsException) {
                    }
                } else if (item != null && (item.item is ItemSnowball || item.item is ItemEgg)
                    && facingBlocks
                ) {
                    try {
                        val c = facingBlocks as Int
                        if (c == 0) {
                            FastPlace.rightClickDelayTimerField.set(mc, 0)
                        } else {
                            if (c == 4) {
                                return
                            }
                            val d: Int = FastPlace.rightClickDelayTimerField.getInt(mc)
                            if (d == 4) {
                                FastPlace.rightClickDelayTimerField.set(mc, c)
                            }
                        }
                    } catch (ignored: IllegalAccessException) {
                    } catch (ignored: IndexOutOfBoundsException) {
                    }
                }
            } else {
                try {
                    val c = speed
                    if (c == 0) {
                        FastPlace.rightClickDelayTimerField.set(mc, 0)
                    } else {
                        if (c == 4) {
                            return
                        }
                        val d: Int = FastPlace.rightClickDelayTimerField.getInt(mc)
                        if (d == 4) {
                            FastPlace.rightClickDelayTimerField.set(mc, c)
                        }
                    }
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: IndexOutOfBoundsException) {
                }
            }
        }
    }
}
