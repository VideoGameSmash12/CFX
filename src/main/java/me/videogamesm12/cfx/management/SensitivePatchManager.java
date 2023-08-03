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

package me.videogamesm12.cfx.management;

import com.google.gson.Gson;
import lombok.Getter;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * <h1>SensitivePatchManager</h1>
 * <p>Handles patches that affect more sensitive parts of the game by using a different way of getting metadata.</p>
 * <br>
 * <p>While the regular PatchManager uses the patch class itself to get its metadata by loading its class and getting
 * the patch metadata from there using annotations, the SensitivePatchManager instead retrieves patch metadata from a
 * JSON file stored in the main mod JAR. This <i>hopefully</i> prevents "class loaded too early" errors from happening.
 */
public class SensitivePatchManager implements IMixinConfigPlugin
{
    private static final Gson gson = new Gson();
    private static final SensitivePatchFile sensitivePatches;
    //--
    private final FabricLoader loader = FabricLoader.getInstance();

    static
    {
        sensitivePatches = gson.fromJson(new InputStreamReader(Objects.requireNonNull(
                CFX.class.getClassLoader().getResourceAsStream("cfx.sensitive-patches.json"))),
                SensitivePatchFile.class);
    }

    @Override
    public void onLoad(String mixinPackage)
    {
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        try
        {
            // Enforce requirement that sensitive patches be defined as such in the patches file
            if (!sensitivePatches.getPatches().containsKey(mixinClassName))
            {
                throw new IllegalArgumentException("Patch was not detected in the sensitive patches file");
            }

            // Check to see if the patch is disabled by the user in the configuration
            if (CFX.getConfig().getOverrides().getForciblyDisabledPatches().contains(mixinClassName))
            {
                CFX.getLogger().warn("Ignoring sensitive patch " + mixinClassName
                        + " as the user has specifically chosen to disable it in the configuration");
                return false;
            }

            // Get the patch metadata
            final SensitivePatchMeta metadata = sensitivePatches.getPatches().get(mixinClassName);

            // Find mod conflicts
            final Stream<String> foundConflicts = Arrays.stream(metadata.getConflictingMods()).filter(loader::isModLoaded);
            if (foundConflicts.findFirst().isPresent())
            {
                CFX.getLogger().warn("Ignoring patch " + mixinClassName + " as it conflicts with mods: " + String.join(", ", foundConflicts.toString()));
                return false;
            }

            // Make sure the patch is supported
            if (!VersionChecker.isCompatibleWithCurrentVersion(metadata))
            {
                return false;
            }
        }
        // Was the patch invalid?
        catch (IllegalArgumentException ex)
        {
            CFX.getLogger().warn("Invalid sensitive patch detected - " + mixinClassName, ex);
            return false;
        }
        // Did something else happen that broke everything?
        catch (Throwable ex)
        {
            CFX.getLogger().error("Failed to apply patch! Please report this to the mod developer immediately!", ex);
            return false;
        }

        CFX.getLogger().info("Applied sensitive patch " + mixinClassName + ".");
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }

    @Getter
    private static class SensitivePatchFile
    {
        private Map<String, SensitivePatchMeta> patches = new HashMap<>();
    }
}
