/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.init.Blocks

object XRay : Module("XRay", ModuleCategory.RENDER) {
    val coal by BoolValue("Coal",false)
    val iron by BoolValue("Iron",false)
    val gold by BoolValue("Gold",true)
    val diamond by BoolValue("Diamond",true)
    val emerald by BoolValue("Emerald",false)
    val redStone by BoolValue("RedStone",false)
    val lapis by BoolValue("Lapis",false)
    val quartz by BoolValue("Quartz",false)

    private var prevGammaLevel = 0f
    val xrayBlocks = mutableListOf(
        /*Blocks.coal_ore,
        Blocks.iron_ore,
        Blocks.gold_ore,
        Blocks.redstone_ore,
        Blocks.lapis_ore,
        Blocks.diamond_ore,
        Blocks.emerald_ore,
        Blocks.quartz_ore,*/
        Blocks.clay,
        Blocks.glowstone,
        Blocks.crafting_table,
        Blocks.torch,
        Blocks.ladder,
        Blocks.tnt,
        /*Blocks.coal_block,
        Blocks.iron_block,
        Blocks.gold_block,
        Blocks.redstone_ore,
        Blocks.diamond_block,
        Blocks.emerald_block,
        Blocks.lapis_block,*/
        Blocks.fire,
        Blocks.mossy_cobblestone,
        Blocks.mob_spawner,
        Blocks.end_portal_frame,
        Blocks.enchanting_table,
        Blocks.bookshelf,
        Blocks.command_block,
        Blocks.lava,
        Blocks.flowing_lava,
        Blocks.water,
        Blocks.flowing_water,
        Blocks.furnace,
        Blocks.lit_furnace
    )



    override fun onEnable() {
        prevGammaLevel = mc.gameSettings.gammaSetting
        if (coal){
            xrayBlocks.add(Blocks.coal_ore)
            xrayBlocks.add(Blocks.coal_block)
        }else{
            xrayBlocks.remove(Blocks.coal_ore)
            xrayBlocks.remove(Blocks.coal_block)
        }
        if (iron){
            xrayBlocks.add(Blocks.iron_ore)
            xrayBlocks.add(Blocks.iron_block)
        }else{
            xrayBlocks.remove(Blocks.iron_ore)
            xrayBlocks.remove(Blocks.iron_block)
        }
        if (gold){
            xrayBlocks.add(Blocks.gold_ore)
            xrayBlocks.add(Blocks.gold_block)
        }else{
            xrayBlocks.remove(Blocks.gold_ore)
            xrayBlocks.remove(Blocks.gold_block)
        }
        if (diamond){
            xrayBlocks.add(Blocks.diamond_ore)
            xrayBlocks.add(Blocks.diamond_block)
        }else{
            xrayBlocks.remove(Blocks.diamond_ore)
            xrayBlocks.remove(Blocks.diamond_block)
        }
        if (emerald){
            xrayBlocks.add(Blocks.emerald_ore)
            xrayBlocks.add(Blocks.emerald_block)
        }else{
            xrayBlocks.remove(Blocks.emerald_ore)
            xrayBlocks.remove(Blocks.emerald_block)
        }
        if (redStone){
            xrayBlocks.add(Blocks.redstone_ore)
            xrayBlocks.add(Blocks.redstone_block)
        }else{
            xrayBlocks.remove(Blocks.redstone_ore)
            xrayBlocks.remove(Blocks.redstone_block)
        }
        if (lapis){
            xrayBlocks.add(Blocks.lapis_ore)
            xrayBlocks.add(Blocks.lapis_block)
        }else{
            xrayBlocks.remove(Blocks.lapis_ore)
            xrayBlocks.remove(Blocks.lapis_block)
        }
        if (quartz){
            xrayBlocks.add(Blocks.quartz_ore)
            xrayBlocks.add(Blocks.quartz_block)
        }else{
            xrayBlocks.remove(Blocks.quartz_ore)
            xrayBlocks.remove(Blocks.quartz_block)
        }
    }

    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = prevGammaLevel
    }
}
