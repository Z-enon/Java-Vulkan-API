package com.xenon.vulkan;

import com.xenon.vulkan.boostrap.Once;

/**
 * @author Zenon
 */
public interface Disposable {

    @Once
    void dispose();
}
