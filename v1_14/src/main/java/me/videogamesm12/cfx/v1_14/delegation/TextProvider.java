package me.videogamesm12.cfx.v1_14.delegation;

import me.videogamesm12.cfx.delegation.ITextProvider;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Requirements(min = 477, max = 578)
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
        return literal(text).formatted(formatting);
    }

    @Override
    public Text translatable(String key, Object... args)
    {
        return new TranslatableText(key, args);
    }

    @Override
    public Text translatable(String key, Formatting formatting, Object... args)
    {
        return translatable(key, args).formatted(formatting);
    }
}
