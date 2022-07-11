package com.xenon.vulkan.boostrap;

import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;

import java.util.Collection;

/**
 * @author Zenon
 */
public final class VulkanBundle {

    public static VulkanBundle alloc() {
        return new VulkanBundle();
    }

    public VkWindow window;
    public VkApplicationInfo appInfo;
    VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo;
    public VkAllocationCallbacks allocationCallbacks;
    public Collection<String> requestedLayers;
    public Collection<String> deviceExtensions;
    public GPUFeatures gpuFeatures;
    public QueueFeatures queueFeatures;
    public String GPUName;
    public String GPUVendor;
    public SwapChain swapChain;

    private VulkanBundle() {}

    /**
     * If any of its fields is null, throw a RuntimeException
     */
    public void sanity() {
        if (
                window == null ||
                appInfo == null ||
                debugCreateInfo == null ||
                requestedLayers == null ||
                deviceExtensions == null ||
                gpuFeatures == null ||
                queueFeatures == null ||
                GPUName == null ||
                GPUVendor == null ||
                swapChain == null
        )
            throw VkError.log("VulkanedBundle has got some missing fields: " + this);
    }
    
    @Override
    public String toString() {
        return "VulkanBundle{" +
                "window=" + window +
                ", appInfo=" + appInfo +
                ", debugCreateInfo=" + debugCreateInfo +
                ", allocationCallbacks=" + allocationCallbacks +
                ", requestedLayers=" + requestedLayers +
                ", deviceExtensions=" + deviceExtensions +
                ", gpuFeatures=" + gpuFeatures +
                ", queueFeatures=" + queueFeatures +
                ", GPUName='" + GPUName + '\'' +
                ", GPUVendor='" + GPUVendor + '\'' +
                ", swapChain=" + swapChain +
                '}';
    }

}
