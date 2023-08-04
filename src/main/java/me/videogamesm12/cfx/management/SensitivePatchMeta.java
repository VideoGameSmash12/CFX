package me.videogamesm12.cfx.management;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SensitivePatchMeta
{
    private int minVersion;

    private int maxVersion;

    private String[] conflictingMods = {};
}