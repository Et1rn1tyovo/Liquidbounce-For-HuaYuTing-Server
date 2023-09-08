package net.ccbluex.liquidbounce.features.module.modules.hyt;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.value.BoolValue;

public class ViaVersionFix extends Module {
    public static BoolValue toggle = new BoolValue("Toggle",false);
    public ViaVersionFix() {
        super("ViaVersionFix", ModuleCategory.HYT, 0,false,false, "Fix ViaVersion");
    }
}
