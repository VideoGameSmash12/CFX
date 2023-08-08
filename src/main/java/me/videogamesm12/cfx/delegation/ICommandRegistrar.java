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

/**
 * <h1>ICommandRegistrar</h1>
 * <p>Interface for anything that registers commands. Client and server commands are registered separately using the
 * "cfx-client" and "cfx-server" entrypoints respectively.</p>
 * <p>Registrars that use certain APIs like Cotton Client Commands do not need to be registered as a command registrar
 * in the entrypoints section as those APIs call the shots to register the commands.</p>
 * <p>All command registrars must have the {@link Requirements} annotation in order to be properly recognized by the
 * Delegator.</p>
 */
public interface ICommandRegistrar
{
    /**
     * Registers the mod's commands or sets up the foundation for the registrar to do so.
     */
    void register();
}
