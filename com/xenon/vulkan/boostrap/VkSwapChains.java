package com.xenon.vulkan.boostrap;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public final class VkSwapChains {

    @Once
    @SuppressWarnings("resource")
    public static SwapChain create(VulkanBundle bundle, long surface_handle, XeGPU gpu, VkDevice device) {

        QueueFeatures queueFeatures = bundle.queueFeatures;
        SwapChain swapChain = bundle.swapChain;
        VkPhysicalDevice physicalDevice = gpu.gpu();
        VkAllocationCallbacks callbacks = bundle.allocationCallbacks;
        final int width = bundle.window.width;
        final int height = bundle.window.height;

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
                        .filter(f -> f.format() == swapChain.format())
                        .filter(f -> f.colorSpace() == swapChain.colorSpace())
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
                    if (present_modes.get(i) == swapChain.presentMode()) {
                        chosen_present_mode = swapChain.presentMode();
                        break;
                    }
                }
            }

            // Extent choice
            VkExtent2D chosen_extent;
            {
                if (caps.currentExtent().width() != -1) // uint32_t max
                    chosen_extent = caps.currentExtent();
                else {
                    chosen_extent = VkExtent2D.malloc(stack);

                    VkExtent2D minExtent = caps.minImageExtent();
                    VkExtent2D maxExtent = caps.maxImageExtent();
                    chosen_extent.width(Math.max( minExtent.width(), Math.min(maxExtent.width(), width) ));
                    chosen_extent.height(Math.max( minExtent.height(), Math.min(maxExtent.height(), height) ));
                }
            }

            // minimum image count choice
            int chosen_imageCount;
            {
                chosen_imageCount = caps.minImageCount() + swapChain.additional_img_count;
                if (caps.maxImageCount() != 0)
                    chosen_imageCount = Math.min(caps.maxImageCount(), chosen_imageCount);
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            swapchainCreateInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            swapchainCreateInfo.surface(surface_handle);
            swapchainCreateInfo.minImageCount(chosen_imageCount);
            swapchainCreateInfo.imageFormat(chosen_format);
            swapchainCreateInfo.imageColorSpace(chosen_colorSpace);
            swapchainCreateInfo.imageExtent(chosen_extent);
            swapchainCreateInfo.imageArrayLayers(1);
            swapchainCreateInfo.imageUsage(swapChain.image_usage);

            if (queueFeatures.family_index(0) != queueFeatures.family_index(1)) {
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                //createInfo.queueFamilyIndexCount(2);
                swapchainCreateInfo.pQueueFamilyIndices(stack.ints(
                        queueFeatures.family_index(0),
                        queueFeatures.family_index(1)
                ));
            } else swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);

            swapchainCreateInfo.preTransform(caps.currentTransform());
            swapchainCreateInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            swapchainCreateInfo.presentMode(chosen_present_mode);
            swapchainCreateInfo.clipped(true);
            swapchainCreateInfo.oldSwapchain(VK_NULL_HANDLE);

            LongBuffer ptr = stack.longs(VK_NULL_HANDLE);

            if (vkCreateSwapchainKHR(device, swapchainCreateInfo, callbacks, ptr) != VK_SUCCESS)
                throw VkError.log("Failed to create swap chain");

            long swapChain_handle = ptr.get(0);

            // swapChain images
            long[] images;
            {
                countB = stack.ints(0);
                vkGetSwapchainImagesKHR(device, swapChain_handle, countB, null);
                count = countB.get(0);
                LongBuffer pSwapChainImages = stack.mallocLong(count);
                vkGetSwapchainImagesKHR(device, swapChain_handle, countB, pSwapChainImages);
                images = new long[count];
                for (int i = 0; i < count; i++)
                    images[i] = pSwapChainImages.get(i);
            }


            // swapChain images view
            long[] imagesView;
            {
                imagesView = new long[count];
                ptr = stack.longs(VK_NULL_HANDLE);
                VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.calloc(stack);
                imageViewCreateInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
                imageViewCreateInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
                imageViewCreateInfo.format(chosen_format);
                var comp = imageViewCreateInfo.components();
                comp.r(VK_COMPONENT_SWIZZLE_IDENTITY);
                comp.g(VK_COMPONENT_SWIZZLE_IDENTITY);
                comp.b(VK_COMPONENT_SWIZZLE_IDENTITY);
                comp.a(VK_COMPONENT_SWIZZLE_IDENTITY);
                var res = imageViewCreateInfo.subresourceRange();
                res.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                res.baseMipLevel(0);
                res.levelCount(1);
                res.baseArrayLayer(0);
                res.layerCount(1);
                System.out.println(swapchainCreateInfo.imageFormat() == imageViewCreateInfo.format());
                for (int i = 0; i < count; i++) {
                    imageViewCreateInfo.image(images[i]);
                    if (vkCreateImageView(device, imageViewCreateInfo, callbacks, ptr) != VK_SUCCESS)
                        throw VkError.log("Failed to create image view n°"+i);
                    imagesView[i] = ptr.get(0);
                }
            }


            // update swapChain
            swapChain.format(chosen_format);
            swapChain.colorSpace(chosen_colorSpace);
            swapChain.presentMode(chosen_present_mode);
            if (swapChain.extent2D() != null) {
                swapChain.extent2D().free();
                throw VkError.log("Didn't expect a VkExtent2D in the swap chain at setup!");
            }
            swapChain.extent2D(VkExtent2D.malloc().set(chosen_extent)); // chosen_extent was allocated on the stack
            swapChain.imageCount(chosen_imageCount);
            swapChain.handle = swapChain_handle;
            swapChain.images = images;
            swapChain.imagesView = imagesView;

            return swapChain;
        }
    }

}
