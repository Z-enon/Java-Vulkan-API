package com.xenon.vulkan.boostrap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;

public final class VkLogicalDevices {



    @Once
    public static VkDevice create(VulkanBundle bundle, XeGPU gpu, long surface_handle) {
        VkPhysicalDevice physicalDevice = gpu.gpu();
        QueueFeatures filteringQueueFamilyFeatures = bundle.queueFeatures;
        GPUFeatures gpuFeatures = bundle.gpuFeatures;
        Collection<String> layers = bundle.requestedLayers;
        Collection<String> deviceExtensions = bundle.deviceExtensions;
        VkAllocationCallbacks callbacks = bundle.allocationCallbacks;

        try (MemoryStack stack = stackPush()) {
            // queue families filtering
            IntBuffer countB = stack.mallocInt(1);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, countB, null);
            VkQueueFamilyProperties.Buffer props = VkQueueFamilyProperties.calloc(countB.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, countB, props);

            // extensive queue family searching

            int[] uniqueIndices;
            {
                IntBuffer surface_present = stack.ints(VK_FALSE);
                int i = 0;

                boolean[] callbacksSuccess = new boolean[filteringQueueFamilyFeatures.capacity()];

                Iterator<VkQueueFamilyProperties> it = props.stream().iterator();
                while (it.hasNext() && someFalse(callbacksSuccess)) {
                    // don't ever put it in a try-with-resources!! it's owned by the MemoryStack.
                    VkQueueFamilyProperties prop = it.next();
                    vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface_handle, surface_present);

                    int j = 0;
                    for (var func : filteringQueueFamilyFeatures) {
                        if (!callbacksSuccess[j] &&
                                (callbacksSuccess[j] = func.test(prop, surface_present.get(0) == VK_TRUE))) {
                            filteringQueueFamilyFeatures.family_index(j, i);
                        }
                        j++;
                    }

                    i++;
                }
                if (someFalse(callbacksSuccess))
                    throw VkError.format("Selected GPU %s does not offer required queues", gpu.name());
                uniqueIndices = filteringQueueFamilyFeatures.uniqueFamilyIndices();
            }


            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(uniqueIndices.length, stack);
            for (int i = 0; i < uniqueIndices.length; i++) {
                VkDeviceQueueCreateInfo queueCreateInfo = queueCreateInfos.get(i);
                queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                queueCreateInfo.queueFamilyIndex(uniqueIndices[i]);
                queueCreateInfo.pQueuePriorities(stack.floats(filteringQueueFamilyFeatures.priority(i)));
            }



            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            createInfo.pQueueCreateInfos(queueCreateInfos);
            createInfo.pEnabledFeatures(gpuFeatures.underlying());
            createInfo.ppEnabledExtensionNames(XeUtils.pointerbuffer(stack, deviceExtensions));
            // layers again
            createInfo.ppEnabledLayerNames(XeUtils.pointerbuffer(stack, layers));

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);
            if (vkCreateDevice(physicalDevice, createInfo, callbacks, pDevice) != VK_SUCCESS)
                throw VkError.log("Failed to create logical device");

            VkDevice device = new VkDevice(pDevice.get(0), physicalDevice, createInfo);

            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);
            for (int i = 0; i < filteringQueueFamilyFeatures.capacity(); i++) {
                vkGetDeviceQueue(device, filteringQueueFamilyFeatures.family_index(i), 0, pQueue);
                filteringQueueFamilyFeatures.result(i, new VkQueue(pQueue.get(0), device));
            }
            return device;
        }
    }

    private static boolean someFalse(boolean[] bs) {
        for (var b : bs)
            if (!b)
                return true;
        return false;
    }

}