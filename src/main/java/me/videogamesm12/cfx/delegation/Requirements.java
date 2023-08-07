package me.videogamesm12.cfx.delegation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Requirements
{
    int min();

    int max();
}
