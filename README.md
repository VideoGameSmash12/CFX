# CFX
CFX is a Fabric mod for Minecraft 1.14 to 1.20.2 that patches hazardous exploits. It was originally included as a subproject of the WNT project, but it is now its own separate project.

## Why?
For almost a decade now, Minecraft exploiting has become increasingly common and increasingly dangerous. While Mojang has been making a commendable effort to combat this by patching exploits in their game with updates, they don't support older, more vulnerable versions of the game. For most people, this isn't a problem, because all they need to do to stay protected is to use the latest and greatest version of Minecraft. However, some people simply can't do that for one reason or another (such as running older hardware or using mods that don't support later versions of the game).

Furthermore, there are cases of exploits that Mojang simply hasn't fixed yet for one reason or another even in the latest version of the game, so pretty much everyone is vulnerable to some pretty nasty exploits.

So, this is where CFX steps in. It tries to patch some of the more hazardous exploits for all mainline versions of the game, even the versions that were superseded by minor updates (e.g. 1.14, 1.14.1, 1.15.1, etc). That way, almost everybody can be protected. It works standalone so you don't have to use it as part of a bigger package of mods.

## Patch Coverage
This table details what patches have yet to be back-ported from both WNT's CFX and Cockblocker.

| Exploit                   | 1.14.x | 1.15.x | 1.16.x                                           | 1.17.x  | 1.18.x  | 1.19.x                                                             | 1.20.x                                     | 
|---------------------------|--------|--------|--------------------------------------------------|---------|---------|--------------------------------------------------------------------|--------------------------------------------|
| **BadBlockEntityName**    | v1_14  | v1_14  | v1_16                                            | N/A     | N/A     | N/A                                                                | N/A                                        |
| **BadEntityName**         | v1_14  | v1_14  | N/A                                              | N/A     | N/A     | N/A                                                                | N/A                                        |
| **BadHoverIdentifier**    | N/A    | N/A    | v1_16                                            | v1_16   | v1_16   | N/A                                                                | N/A                                        |
| **BadHoverUUID**          | N/A    | N/A    | v1_16                                            | v1_16   | v1_16   | N/A                                                                | N/A                                        |
| **BadSherdIdentifiers**   | N/A    | N/A    | N/A                                              | N/A     | N/A     | N/A                                                                | v1_20 (1.20 - 1.20.1), N/A (1.20.2+)       |
| **BoundlessTranslation**  | v1_14  | v1_14  | v1_16 (1.16 - 1.16.1), v1_16_2 (1.16.2 - 1.16.5) | v1_16_2 | v1_16_2 | v1_19 (1.19 - 1.19.2)                                              | N/A                                        |
| **ClickableCommands**     | v1_14  | v1_14  | v1_16                                            | v1_16   | v1_16   | v1_19 (1.19), v1_19_1 (1.19.1 - 1.19.2), v1_19_3 (1.19.3 - 1.19.4) | v1_19_3                                    |
| **CommandSigns**          | v1_14  | v1_14  | v1_14                                            | v1_17   | v1_17   | v1_19                                                              | v1_20                                      |
| **ComponentDepth**        | v1_14  | v1_14  | v1_16                                            | v1_16   | v1_16   | v1_19                                                              | v1_19                                      |
| **ExcessiveEntityNames**  | v1_14  | v1_15  | v1_16                                            | v1_16   | v1_16   | v1_19                                                              | v1_19                                      |
| **ExcessiveHearts**       | v1_14  | v1_14  | v1_16                                            | v1_16   | v1_16   | v1_16                                                              | Not yet                                    |
| **ExcessiveParticles**    | v1_14  | v1_14  | v1_14                                            | v1_14   | v1_14   | v1_14                                                              | v1_14                                      |
| **ExtraNestedArrays**     | v1_14  | v1_14  | v1_16                                            | v1_16   | v1_16   | v1_16                                                              | v1_16                                      |
| **NBTSize**               | v1_14  | v1_14  | v1_14                                            | v1_14   | v1_14   | v1_14 (1.19 - 1.19.2), v1_19_3 (1.19.3 - 1.19.4)                   | v1_19_3 (1.20 - 1.20.1), v1_20_2 (1.20.2+) |
| **OutrageousTranslation** | v1_14  | v1_14  | v1_16                                            | v1_17   | v1_17   | v1_19                                                              | v1_19                                      |
| **UndersizedHeads**       | v1_14  | v1_15  | v1_15                                            | N/A     | N/A     | N/A                                                                | N/A                                        |
| **UpsideDownPortals**     | N/A    | N/A    | v1_16_2 (1.16.2 - 1.16.4)                        | N/A     | N/A     | N/A                                                                | N/A                                        |
