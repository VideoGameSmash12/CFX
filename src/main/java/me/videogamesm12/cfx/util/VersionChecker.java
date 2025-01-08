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

package me.videogamesm12.cfx.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.videogamesm12.cfx.delegation.Requirements;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.MinecraftVersion;

import java.io.InputStreamReader;
import java.util.Objects;

/**
 * <h1>VersionChecker</h1>
 * <p>Utility class for checking and comparing with the currently running version of Minecraft.</p>
 */
public class VersionChecker
{
    private static final Gson gson = new Gson();

    private static final VersionMetadata gameMetadata;

    static
    {
        gameMetadata = gson.fromJson(new InputStreamReader(Objects.requireNonNull(
                MinecraftVersion.class.getClassLoader().getResourceAsStream("version.json"))),
                VersionMetadata.class);
    }

    /**
     * Checks a provider's metadata against the current version of the game by comparing protocol version numbers.
     * @param meta  Requirements
     * @return      True if the provider's metadata indicate that the game's current protocol version is in between its
     *              minimum and maximum protocol versions
     * @throws IllegalArgumentException If the provider's maximum version number is somehow less than the minimum
     */
    public static boolean isCompatibleWithCurrentVersion(Requirements meta)
    {
        if (meta.max() < meta.min())
        {
            throw new IllegalArgumentException("Invalid patch metadata - the maximum version number should never be "
                    + "lower than the minimum version");
        }

        return meta.min() <= gameMetadata.getProtocolVersion()
                && meta.max() >= gameMetadata.getProtocolVersion();
    }

    /**
     * Checks a patch's metadata against the current version of the game by comparing protocol version numbers.
     * @param meta  PatchMeta
     * @return      True if the patch's metadata indicate that the game's current protocol version is in between its
     *              minimum and maximum protocol versions
     * @throws IllegalArgumentException If the patch's maximum version number is somehow less than the minimum
     */
    public static boolean isCompatibleWithCurrentVersion(PatchMeta meta)
    {
        if (meta.maxVersion() < meta.minVersion())
        {
            throw new IllegalArgumentException("Invalid patch metadata - the maximum version number should never be "
                    + "lower than the minimum version");
        }

        return meta.minVersion() <= gameMetadata.getProtocolVersion()
                && meta.maxVersion() >= gameMetadata.getProtocolVersion();
    }
    
    public static int getProtocolVersion()
    {
        return gameMetadata.getProtocolVersion();
    }

    private static class VersionMetadata
    {
        @SerializedName("protocol_version")
        @Getter
        private int protocolVersion;
    }
}
