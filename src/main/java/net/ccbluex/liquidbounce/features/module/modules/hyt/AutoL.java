package net.ccbluex.liquidbounce.features.module.modules.hyt;

import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EntityDeathEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;


public class AutoL extends Module {
    public AutoL() {
        super("AutoL", ModuleCategory.HYT, 0, true, true, "雜魚~", "Auto L");
    }
    public final BoolValue HYT = new BoolValue("Hyt @",true);
    private int index = 0;
    private Entity target;
    private final String message[] = new String[]{
        "呐呐~杂鱼哥哥不会这样就被捉弄的不会说话了吧~真是弱哎~",
        "嘻嘻~杂鱼哥哥不会以为竖个大拇哥就能欺负我了吧~不会吧~不会吧~杂鱼哥哥怎么可能欺负",
        "哥哥真是好欺负啊~嘻嘻~",
        "哎~杂鱼说话就是无趣唉~只会发一张表情包的笨蛋到处都有吧",
        "呐呐~杂鱼哥哥发这个是想教育我吗~嘻嘻~怎么可能啊~",
        "欸？你这个杂鱼~又来问我问题了吗",
        "你这种杂鱼~怎么有资格和我说话？",
        "哥哥的这么短~根本没感觉的好吧!",
        "我就是喜欢捉弄这样笨笨的哥哥哦~",
        "想带我走?哥哥不会是想做嗇閪的事情吧~",
        "哥哥是没有loli控吗？要不然怎么天天围着我转呀，好恶心~",
        "哥哥这么想了解我，不会是想抱我回家然后做邢种事吧~",
        "我只是看哥哥太可怜了，才陪陪你的哦~绝对不是喜欢你~",
        "啊嘞嘞，哥哥都这么大了~不会还没有女朋友吧~"
    };
    @EventTarget
    public void onAttack(AttackEvent event){
        if (event.getTargetEntity() instanceof EntityLivingBase){
            target = event.getTargetEntity();
        }
    }
    @EventTarget
    public void onTick(TickEvent event){
        if (target != null && target.isDead && target instanceof EntityPlayer){
            mc.thePlayer.sendChatMessage((HYT.get()?"@":"")+"[AutoL] !_ " + target.getName() + " " + message[index]);
            index++;
            target = null;
        }
        if (index > message.length - 1){
            index = 0;
        }
    }
    @Override
    public void onEnable(){
        index = 0;
    }
    @Override
    public void onDisable(){
        index = 0;
    }

    @Override
    public String getTag() {
        return "Sexy";
    }
}
