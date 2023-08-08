package me.videogamesm12.cfx.v1_19.delegation;

import me.videogamesm12.cfx.delegation.ITextProvider;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Requirements(min = 759, max = 999)
public class TextProvider implements ITextProvider
{
    @Override
    public Text literal(String text)
    {
        return Text.literal(text);
    }

    @Override
    public Text literal(String text, Formatting formatting)
    {
        return MutableText.of(literal(text).getContent()).formatted(formatting);
    }

    @Override
    public Text translatable(String key, Object... args)
    {
        return Text.translatable(key, args);
    }

    @Override
    public Text translatable(String key, Formatting formatting, Object... args)
    {
        return MutableText.of(translatable(key, args).getContent()).formatted(formatting);
    }
}
