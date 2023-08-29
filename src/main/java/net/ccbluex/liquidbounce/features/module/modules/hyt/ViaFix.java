package net.ccbluex.liquidbounce.features.module.modules.hyt;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.value.BoolValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViaFix extends Module {
    public static BoolValue toggle = new BoolValue("Toggle",false);
    public ViaFix() {
        super("ViaFix", ModuleCategory.HYT, 0,false,false, "Fix ViaVersion");
    }
}
