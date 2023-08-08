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

package me.videogamesm12.cfx.delegation;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * <h1>ITextProvider</h1>
 * <p>Interface for text components across multiple versions of Minecraft. This is primarily used for the client and
 * server side commands.</p>
 * <p>All text providers must have the {@link Requirements} annotation in order to be properly recognized by the
 * Delegator.</p>
 */
public interface ITextProvider
{
    /**
     * Returns a literal text component.
     * @param text  String
     * @return      Text
     */
    Text literal(String text);

    /**
     * Returns a literal text component with the formatting of your choice.
     * @param text          String
     * @param formatting    Formatting
     * @return              Text
     */
    Text literal(String text, Formatting formatting);

    /**
     * Returns a translatable text component.
     * @param key   String
     * @param args  Object...
     * @return      Text
     */
    Text translatable(String key, Object... args);

    /**
     * Returns a translatable text component with the formatting of your choice.
     * @param key           String
     * @param formatting    Formatting
     * @param args          Object...
     * @return              Text
     */
    Text translatable(String key, Formatting formatting, Object... args);
}
