package com.xenon.vulkan.boostrap;

import com.xenon.vulkan.info.SwapchainAndCo;
import com.xenon.vulkan.info.SwapchainCreateInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;

/**
 * @author Zenon
 */
public class VkSwapchains {

    @Once
    public static SwapchainAndCo setup(VulkanBundle bundle) {

        XeGPU gpu = bundle.gpu;
        SwapchainCreateInfo swapChain = bundle.swapchainCreateInfo;
        VkPhysicalDevice physicalDevice = gpu.gpu();
        long surface_handle = bundle.surface;
        VkDevice device = bundle.device;

        try (MemoryStack stack = stackPush()) {
            // querying swap chain support details
            VkSurfaceCapabilitiesKHR caps;
            VkSurfaceFormatKHR.Buffer formats = null;
            IntBuffer present_modes           = null;

            caps = VkSurfaceCapabilitiesKHR.malloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface_handle, caps);

            IntBuffer countB = stack.ints(0);
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface_handle, countB, null);
            int count = countB.get(0);
            if (count != 0) {
                formats = VkSurfaceFormatKHR.malloc(count, stack);
                vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface_handle, countB, formats);
            }

            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface_handle, countB, null);
            count = countB.get(0);
            if (count != 0) {
                present_modes = stack.mallocInt(count);
                vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface_handle, countB, present_modes);
            }


            // validation
            if (formats == null || present_modes == null)
                throw VkError.format("GPU %s does not support swap chain", gpu.name());

            // formats filtering
            final int chosen_format;
            final int chosen_colorSpace;
            {
                VkSurfaceFormatKHR surface_format = formats
                        .stream()
                        .filter(f -> f.format() == swapChain.preferredSurfaceFormat())
                        .filter(f -> f.colorSpace() == swapChain.preferredSurfaceColorSpace())
                        .findAny()
                        .orElse(formats.get(0));
                chosen_format = surface_format.format();
                chosen_colorSpace = surface_format.colorSpace();
            }

            // present mode filtering
            int chosen_present_mode;
            {
                chosen_present_mode = VK_PRESENT_MODE_FIFO_KHR;
                for (int i = 0; i < present_modes.capacity(); i++) {
                    if (present_modes.get(i) == swapChain.preferredPresentMode()) {
                        chosen_present_mode = swapChain.preferredPresentMode();
                        break;
                    }
                }
            }

            // minimum image count choice
            int chosen_imageCount;
            {
                chosen_imageCount = caps.minImageCount() + swapChain.additionalImageCount();
                if (caps.maxImageCount() != 0)
                    chosen_imageCount = Math.min(caps.maxImageCount(), chosen_imageCount);
            }


            // construct final object
            SwapchainAndCo swapchainAndCo = SwapchainAndCo.build(
                    chosen_format,
                    chosen_colorSpace,
                    swapChain.imageUsage(),
                    chosen_present_mode,
                    chosen_imageCount,
                    bundle
            );

            swapchainAndCo.create(
                    physicalDevice,
                    device,
                    bundle.queueFeatures.immutablecopy(),
                    bundle.allocationCallbacks,
                    surface_handle,
                    bundle.window.width,
                    bundle.window.height
            );

            return swapchainAndCo;
        }
    }

}
