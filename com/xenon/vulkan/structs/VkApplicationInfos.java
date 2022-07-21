package com.xenon.vulkan.structs;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;

import java.nio.ByteBuffer;

/**
 * @author Zenon
 */
public final class VkApplicationInfos {

    public static VkApplicationInfo usual(MemoryStack stack, String applicationName, int applicationVersion,
                                            String engineName, int engineVersion, int apiVersion) {
        return custom(VkApplicationInfo.malloc(stack), stack.UTF8(applicationName),
                applicationVersion, stack.UTF8(engineName), engineVersion, apiVersion);
    }


    public static VkApplicationInfo custom(VkApplicationInfo info, ByteBuffer pApplicationName,
                                            int applicationVersion, ByteBuffer pEngineName, int engineVersion,
                                            int apiVersion) {
        info.sType$Default();
        info.pNext(0);
        info.pApplicationName(pApplicationName);
        info.applicationVersion(applicationVersion);
        info.pEngineName(pEngineName);
        info.engineVersion(engineVersion);
        info.apiVersion(apiVersion);
        return info;
    }

}
