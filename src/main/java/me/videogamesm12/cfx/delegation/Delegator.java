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

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Comparator;
import java.util.Optional;

/**
 * <h1>Delegator</h1>
 * <p>Handles various tasks across multiple versions of the game.</p>
 */
public class Delegator
{
    private static ITextProvider textProvider = null;

    private static IFeedbackSender feedbackSender = null;

    /**
     * Gets the text provider for use with commands. When run for the first time, this method searches for a compatible
     *  IFeedbackSender using the Fabric Loader API and, once it finds one, stores it for later use.
     * @return  ITextProvider
     */
    public static ITextProvider getTextProvider()
    {
        if (textProvider == null)
        {
            final Optional<ITextProvider> optional = FabricLoader.getInstance().getEntrypoints("cfx",
                    ITextProvider.class).stream().filter((provider) ->
            {
                if (!provider.getClass().isAnnotationPresent(Requirements.class))
                {
                    CFX.getLogger().error("Text provider " + provider.getClass().getName() + " is missing the required Requirements annotation");
                    return false;
                }

                return VersionChecker.isCompatibleWithCurrentVersion(provider.getClass().getAnnotation(Requirements.class));
            }).findFirst();

            if (!optional.isPresent())
            {
                throw new IllegalStateException("No text provider found!");
            }

            textProvider = optional.get();
        }

        return textProvider;
    }

    /**
     * Gets the feedback sender for use with commands. When run for the first time, this method searches for a
     *  compatible IFeedbackSender using the Fabric Loader API and, once it finds one, stores it for later use.
     * @return IFeedbackSender
     */
    public static IFeedbackSender getFeedbackSender()
    {
        if (feedbackSender == null)
        {
            final Optional<IFeedbackSender> optional = FabricLoader.getInstance().getEntrypoints("cfx-feedback",
                    IFeedbackSender.class).stream().filter((sender) ->
            {
                if (!sender.getClass().isAnnotationPresent(Requirements.class))
                {
                    CFX.getLogger().error("Feedback sender " + sender.getClass().getName() + " is missing the required Requirements annotation");
                    return false;
                }

                return VersionChecker.isCompatibleWithCurrentVersion(sender.getClass().getAnnotation(Requirements.class));
            }).findFirst();

            if (!optional.isPresent())
            {
                throw new IllegalStateException("No feedback sender found!");
            }

            feedbackSender = optional.get();
        }

        return feedbackSender;
    }

    /**
     * Instructs the first supported client command registrar the mod can find to register its commands.
     */
    public static void registerClientCommands()
    {
        FabricLoader.getInstance().getEntrypoints("cfx-client", ICommandRegistrar.class).stream().filter((registrar) ->
        {
            if (!registrar.getClass().isAnnotationPresent(Requirements.class))
            {
                CFX.getLogger().error("Client command registrar " + registrar.getClass().getName() + " is missing the required Requirements annotation");
                return false;
            }

            final Requirements requirements = registrar.getClass().getAnnotation(Requirements.class);
            for (String id : requirements.dependencies())
            {
                if (!FabricLoader.getInstance().isModLoaded(id))
                {
                    return false;
                }
            }

            return VersionChecker.isCompatibleWithCurrentVersion(requirements);
        }).sorted(Comparator.comparingInt(one -> one.getClass().getAnnotation(Requirements.class).priority()))
                .findAny().ifPresent(ICommandRegistrar::register);
    }

    /**
     * Instructs the first supported server command registrar the mod can find to register its commands.
     */
    public static void registerServerCommands()
    {
        final Optional<ICommandRegistrar> optional = FabricLoader.getInstance().getEntrypoints("cfx-server",
                ICommandRegistrar.class).stream().filter((registrar) ->
        {
            if (!registrar.getClass().isAnnotationPresent(Requirements.class))
            {
                CFX.getLogger().error("Server command registrar " + registrar.getClass().getName() + " is missing the required Requirements annotation");
                return false;
            }

            final Requirements requirements = registrar.getClass().getAnnotation(Requirements.class);
            for (String id : requirements.dependencies())
            {
                if (!FabricLoader.getInstance().isModLoaded(id))
                {
                    return false;
                }
            }

            return VersionChecker.isCompatibleWithCurrentVersion(requirements);
        }).min(Comparator.comparingInt(one -> one.getClass().getAnnotation(Requirements.class).priority()));

        if (optional.isPresent())
        {
            optional.get().register();
        }
        else
        {
            CFX.getLogger().info("No compatible server command API detected! If you want to be able to reload CFX's"
                    + " configuration on the fly, consider installing a compatible API like the Fabric API.");
        }
    }
}
