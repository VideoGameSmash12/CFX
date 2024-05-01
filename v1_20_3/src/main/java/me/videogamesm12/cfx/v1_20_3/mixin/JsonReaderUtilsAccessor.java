package me.videogamesm12.cfx.v1_20_3.mixin;

import com.google.gson.stream.JsonReader;
import net.minecraft.util.JsonReaderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JsonReaderUtils.class)
public interface JsonReaderUtilsAccessor
{
    @Invoker("getPos")
    public static int invokeGetPos(JsonReader reader) {
        throw new AssertionError();
    }
}
