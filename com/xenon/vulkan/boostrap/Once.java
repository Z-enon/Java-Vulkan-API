package com.xenon.vulkan.boostrap;

import java.lang.annotation.*;

/**
 * Hints that a method should only be called once. Usually used with singleton pattern or pure bootstrap.
 * Most of the time, calling a method with @Once twice will make Vulkan crash.
 * @author Zenon
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Once {
}
