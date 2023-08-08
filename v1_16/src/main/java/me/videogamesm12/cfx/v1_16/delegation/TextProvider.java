package me.videogamesm12.cfx.v1_16.delegation;

import me.videogamesm12.cfx.delegation.ITextProvider;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Requirements(min = 735, max = 758)
public class TextProvider implements ITextProvider
{
    @Override
    public Text literal(String text)
    {
        return new LiteralText(text);
    }

    @Override
    public Text literal(String text, Formatting formatting)
    {
        return literal(text).copy().formatted(formatting);
    }

    @Override
    public Text translatable(String key, Object... args)
    {
        return new TranslatableText(key, args);
    }

    @Override
    public Text translatable(String key, Formatting formatting, Object... args)
    {
        return translatable(key, args).copy().formatted(formatting);
    }
}
