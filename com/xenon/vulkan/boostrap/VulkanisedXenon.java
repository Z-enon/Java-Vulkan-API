package com.xenon.vulkan.boostrap;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkQueue;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.vkDestroyDevice;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;

/**
 * @author Zenon
 */
public record VulkanisedXenon(
        VkWindow window,
        VkInstance vkInstance,
        XeGPU gpu,
        VkDevice device,
        VkAllocationCallbacks allocationCallbacks,
        VkQueue[] queues,
        ISwapChain swapChain,
        long debug_handle,
        long surface_handle
) {

    @Once
    public static VulkanisedXenon light(VulkanBundle bundle) {
        MemoryStack stack = stackPush();

        VkPhysicalDevices.configureLayers(bundle);
        bundle.debugCreateInfo = VkDebug.genDebugCreateInfo(stack);
        bundle.sanity();

        VkInstance inst     = VkInstances.create(bundle);
        long debug_handle   = VkDebug.setupDebug(bundle, inst);
        XeGPU gpu           = VkPhysicalDevices.pick(bundle, inst).validate(bundle);
        long surface_handle = VkWindow.createSurface(bundle, inst);
        VkDevice device     = VkLogicalDevices.create(bundle, gpu, surface_handle);
        SwapChain swapChain = VkSwapChains.create(bundle, surface_handle, gpu, device);
        swapChain.sanity();

        stack.close();  // free bundle.debugCreateInfo
        return new VulkanisedXenon(
                bundle.window,
                inst,
                gpu,
                device,
                bundle.allocationCallbacks,
                bundle.queueFeatures.results,
                swapChain.immutablecopy(),
                debug_handle,
                surface_handle
        );
    }





    @Once
    public void douse() {
        swapChain.free();
        swapChain.dispose(this);
        vkDestroyDevice(device, allocationCallbacks);
        vkDestroySurfaceKHR(vkInstance, surface_handle, allocationCallbacks);
        VkDebug.dispose(this);
        vkDestroyInstance(vkInstance, allocationCallbacks);
        window.dispose();
    }

}
