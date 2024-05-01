package me.videogamesm12.cfx.v1_20_3.patches.individual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>OutrageousTranslationFour</h1>
 * <p>Patches an exploit in the new way of doing Text serialization by code.</p>
 * <p>This particular patch affects the game's packet system to hook into when components are processed by the client.</p>
 * <p>This patch is for 1.20.3 to 1.20.4.</p>
 */
@Mixin(PacketByteBuf.class)
@PatchMeta(minVersion = 765, maxVersion = 765) // 1.20.3 to 1.20.4
public abstract class OutrageousTranslationFour
{
    @Shadow public abstract NbtElement readNbt(NbtTagSizeTracker tracker);

    @Shadow @Deprecated public abstract <T> T decode(DynamicOps<NbtElement> ops, Codec<T> codec);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[0-9]{1,}\\$s");

    @Inject(method = "readUnlimitedText",
            at = @At("HEAD"),
            cancellable = true)
    public void fixMajorExploitMore(CallbackInfoReturnable<Text> cir)
    {
        NbtElement nbtElement = readNbt(NbtTagSizeTracker.ofUnlimitedBytes());

        if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                && getNumberOfPlaceholders(nbtElement) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
        {
            cir.setReturnValue(Text.translatable("cfx.replacement.too_many_placeholders")
                    .formatted(Formatting.RED));
        }
        else
        {
            cir.setReturnValue(Util.getResult(TextCodecs.CODEC.parse(NbtOps.INSTANCE, nbtElement), IllegalArgumentException::new));
        }
    }

    @Inject(method = "readText",
            at = @At("HEAD"),
            cancellable = true)
    public void fixMajorExploit(CallbackInfoReturnable<Text> cir)
    {
        NbtElement nbtElement = readNbt(NbtTagSizeTracker.of(0x200000L));

        if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                && getNumberOfPlaceholders(nbtElement) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
        {
            cir.setReturnValue(Text.translatable("cfx.replacement.too_many_placeholders")
                    .formatted(Formatting.RED));
        }
        else
        {
            cir.setReturnValue(Util.getResult(TextCodecs.CODEC.parse(NbtOps.INSTANCE, nbtElement), IllegalArgumentException::new));
        }
    }

    private long getNumberOfPlaceholders(NbtElement element)
    {
        long amount = 0;

        // God dammit!
        if (element.getType() != NbtCompound.COMPOUND_TYPE)
        {
            return amount;
        }

        NbtCompound from = (NbtCompound) element;

        // Figure out how many placeholders are in a single translatable component
        if (from.contains("translate"))
        {
            String key = from.getString("translate");
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
            amount += matcher.results().count();
        }

        // Undocumented way for 1.19.4+
        if (from.contains("fallback"))
        {
            String key = from.getString("fallback");
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
            amount += matcher.results().count();
        }

        // Also applies to keybind components, but to a lesser extent
        if (from.contains("keybind"))
        {
            String key = from.getString("keybind");
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
            amount += matcher.results().count();
        }

        // Recursively figure out how many placeholders the component has in the "with" shit
        if (from.contains("with"))
        {
            NbtList list = from.getList("with", NbtElement.STRING_TYPE);

            for (NbtElement within : list)
            {
                long amountWithin = getNumberOfPlaceholders(within);

                if (amountWithin == 1)
                {
                    amount++;
                }
                else if (amountWithin > 1)
                {
                    amount = amount * amountWithin;
                }
            }
        }

        // Just in case...
        if (from.contains("extra"))
        {
            NbtList list = from.getList("extra", NbtElement.STRING_TYPE);

            for (NbtElement within : list)
            {
                long amountWithin = getNumberOfPlaceholders(within);

                if (amountWithin == 1)
                {
                    amount++;
                }
                else if (amountWithin > 1)
                {
                    amount = amount * amountWithin;
                }
            }
        }

        return amount;
    }
}
