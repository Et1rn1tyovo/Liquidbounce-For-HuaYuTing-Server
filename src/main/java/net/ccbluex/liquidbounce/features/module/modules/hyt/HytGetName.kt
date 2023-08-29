package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.S02PacketChat
import java.util.regex.Pattern

object HytGetName : Module("HytGetName",ModuleCategory.HYT) {

    private val mode = ListValue("GetNameMode", arrayOf("4V4/1V1", "32/64", "16V16"), "4V4/1V1")

    override fun onDisable() {
        clearAll()
        super.onDisable()
    }

    override val tag: String
    get() = mode.get()
    @EventTarget
    fun onPacket(event: PacketEvent) { //Trim 去除名字后面的空字符串
        val packet = event.packet

        if (packet is S02PacketChat) {
            when (mode.get().toLowerCase()) {
                "4v4/1v1" -> {
                    val matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(packet.chatComponent.unformattedText)
                    val matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?)死了!)))").matcher(packet.chatComponent.unformattedText)
                    if (matcher.find()) {
                        val name = matcher.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce+§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(5000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                    if (matcher2.find()) {
                        val name = matcher2.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(5000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                }//现在进行一个GetName Fix ImCzf#233 Impaimon Dimeples
                "32/64"-> {
                    val matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(packet.chatComponent.unformattedText)
                    val matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?)死了!)))")
                        .matcher(packet.chatComponent.unformattedText)
                    if (matcher.find()) {
                        val name = matcher.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                    if (matcher2.find()) {
                        val name = matcher2.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                }
                    "16v16" ->{
                    val matcher = Pattern.compile("击败了 (.*?)!").matcher(packet.chatComponent.unformattedText)
                    val matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(packet.chatComponent.unformattedText)
                    if (matcher.find()) {
                        val name = matcher.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                    if (matcher2.find()) {
                        val name = matcher2.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fAdd HYT Bot:$name")
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    ClientUtils.displayChatMessage("§7[§8§6Liquidbounce§7]§fDeleted HYT Bot:$name")
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                }
            }
        }
    }
    @EventTarget
    fun onWorld(event: WorldEvent?) {
        clearAll()
    }

    private fun clearAll() {
        LiquidBounce.fileManager.friendsConfig.clearFriends()
    }

}