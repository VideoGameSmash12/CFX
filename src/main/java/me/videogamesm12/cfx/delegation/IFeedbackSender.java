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

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * <h1>IFeedbackSender</h1>
 * <p>Interface used to send responses to users who had used a server-side command.</p>
 * <p>All feedback senders must have the {@link Requirements} annotation in order to be properly recognized by the
 * Delegator.</p>
 */
public interface IFeedbackSender
{
    /**
     * Sends a Text to a ServerCommandSource. This method is wrapped because 1.20 changed how feedback is sent from
     *  commands.
     * @param source            ServerCommandSource
     * @param message           Text
     * @param broadcastToOps    boolean
     */
    void sendFeedback(ServerCommandSource source, Text message, boolean broadcastToOps);
}