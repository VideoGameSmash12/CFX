package me.videogamesm12.cfx.delegation;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ITextProvider
{
    Text literal(String text);

    Text literal(String text, Formatting formatting);

    Text translatable(String key, Object... args);

    Text translatable(String key, Formatting formatting, Object... args);
}
