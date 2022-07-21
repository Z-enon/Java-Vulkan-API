package com.xenon.vulkan.boostrap;

import com.xenon.vulkan.info.GPUFeaturesCreateInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkEnumerateDeviceExtensionProperties;

/**
 * Container for VkPhysicalDevice and its name
 * @author Zenon
 */
public record XeGPU(VkPhysicalDevice gpu, String name) {

    /**
     * Validates the selected GPU with the supplied GPUFeatures and the requested device extensions.
     * @param bundle the container
     * @return this instance
     */
    public XeGPU validate(VulkanBundle bundle) {
        VkPhysicalDevice device = gpu();
        Collection<String> deviceExtensions = bundle.deviceExtensions;
        GPUFeaturesCreateInfo gpuFeatures = bundle.gpuFeatures;


        try (MemoryStack stack = stackPush()) {
            VkPhysicalDeviceProperties props = VkPhysicalDeviceProperties.calloc(stack);
            vkGetPhysicalDeviceProperties(device, props);
            if (props.deviceType() != VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU)
                System.err.println("[WARNING] Selected GPU " + name() + " isn't a dedicated one.");
            VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);
            vkGetPhysicalDeviceFeatures(device, features);
            gpuFeatures.validateDeviceFeatures(features);

            IntBuffer count = stack.ints(0);
            vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, count, null);
            VkExtensionProperties.Buffer extensionProperties = VkExtensionProperties.malloc(count.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, count, extensionProperties);
            Collection<String> availableExtensions = extensionProperties
                    .stream()
                    .map(VkExtensionProperties::extensionNameString)
                    .collect(Collectors.toSet());

            if (!availableExtensions.containsAll(deviceExtensions))
                throw VkError.missing(availableExtensions, deviceExtensions);
        }
        return this;
    }
}
