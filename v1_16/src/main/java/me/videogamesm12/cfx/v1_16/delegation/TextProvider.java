/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.cfx.v1_16.delegation;

import me.videogamesm12.cfx.delegation.ITextProvider;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * <h1>TextProvider</h1>
 * <p>Implementation of {@link ITextProvider} for versions 1.16 to 1.18.2.</p>
 */
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
