package com.xenon.vulkan.boostrap;

import com.xenon.vulkan.info.SwapchainAndCo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;

import java.util.HashSet;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public record VulkanisedXenon(
        VkWindow window,
        VkInstance vkInstance,
        XeGPU gpu,
        VkDevice device,
        VkAllocationCallbacks allocationCallbacks,
        XeQueues queues,
        SwapchainAndCo swapchainAndCo,
        long debug_handle,
        long surface_handle
) {

    /**
     * Initializes Vulkan
     * @param bundle the container
     * @return a VulkanManager for this application
     */
    @Once
    public static VulkanisedXenon light(VulkanBundle bundle) {
        bundle.sanity1();
        MemoryStack stack = stackPush();

        bundle.requestedLayers = new HashSet<>(bundle.requestedLayers);
        VkPhysicalDevices.configureLayers(bundle);
        bundle.debugCreateInfo = VkDebug.genDebugCreateInfo(stack);

        bundle.instance     = VkInstances.create(bundle);
        bundle.debug        = VkDebug.setupDebug(bundle);
        bundle.gpu          = VkPhysicalDevices.pick(bundle).validate(bundle);
        bundle.surface      = VkWindow.createSurface(bundle);
        bundle.device       = VkLogicalDevices.create(bundle);
        bundle.swapchainAndCo = VkSwapchains.setup(bundle);


        stack.close();  // free bundle.debugCreateInfo
        bundle.sanity2();

        var xe = new VulkanisedXenon(
                bundle.window,
                bundle.instance,
                bundle.gpu,
                bundle.device,
                bundle.allocationCallbacks,
                bundle.queueFeatures.immutablecopy(),
                bundle.swapchainAndCo,
                bundle.debug,
                bundle.surface
        );
        bundle.window.setResizeCallback(xe::refreshPipeline);

        return xe;
    }

    public void refreshPipeline(int width, int height) {
        if (width == 0 || height == 0)  return; // minimizing case

        vkDeviceWaitIdle(device);
        swapchainAndCo.cleanOutOfDate(this);
        swapchainAndCo.create(
                gpu.gpu(),
                device,
                queues,
                allocationCallbacks,
                surface_handle,
                width,
                height
        );
    }


    /**
     * Vulkan cleanup
     */
    @Once
    public void douse() {
        swapchainAndCo.dispose(this);
        vkDestroyDevice(device, allocationCallbacks);
        vkDestroySurfaceKHR(vkInstance, surface_handle, allocationCallbacks);
        VkDebug.dispose(this);
        vkDestroyInstance(vkInstance, allocationCallbacks);
        window.dispose();
    }

}
