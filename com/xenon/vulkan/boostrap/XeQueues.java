package com.xenon.vulkan.boostrap;

import org.lwjgl.vulkan.VkQueue;

/**
 * @author Zenon
 */
public record XeQueues(VkQueue[] queues, int[] familyIndices) {
}
