package com.xenon.vulkan;

import java.lang.annotation.*;

/**
 * Usually provides usage hints of a Vulkan thingy
 * @author Zenon
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Vulkan {
    String value();
}
