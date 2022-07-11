package com.xenon.vulkan.boostrap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.Locale;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;

public final class VkPhysicalDevices {


    /**
     * Counter-measures for broken vkEnumeratePhysicalDevices.
     * See "vkEnumeratePhysicalDevices returns 0" or "VK_LAYER_AMD_switchable_graphics breaks devices".
     */
    public static void configureLayers(VulkanBundle bundle) {
        bundle.requestedLayers.add(switch(bundle.GPUVendor.toLowerCase(Locale.ROOT)) {
            case "nvidia" -> "VK_LAYER_NV_optimus";
            case "amd" -> "VK_LAYER_AMD_switchable_graphics";   // I'm not sure if this even works with AMD gpus tbh
            default -> throw VkError.log("Invalid vendor name. AMD or NVIDIA expected");
        });
    }


    /**
     * Picks the first device which name contains <code>GPU_name</code>.
     * Essentially
     * <code>physicalDevices.foreach(d -> if (d.name.contains(GPU_name)) {selectDevice(d);break;})</code>
     */
    @Once
    public static XeGPU pick(VulkanBundle bundle, VkInstance instance) {
        String requestedGPUName = bundle.GPUName;

        try (MemoryStack stack = stackPush()) {

            IntBuffer countB = stack.mallocInt(1);
            vkEnumeratePhysicalDevices(instance, countB, null);
            int count = countB.get(0);

            if (count == 0)
                throw VkError.log("No GPU supporting Vulkan");

            PointerBuffer devices = stack.mallocPointer(count);
            vkEnumeratePhysicalDevices(instance, countB, devices);

            for (int i = 0; i < count; i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(devices.get(i), instance);
                VkPhysicalDeviceProperties props = VkPhysicalDeviceProperties.calloc(stack);
                vkGetPhysicalDeviceProperties(device, props);
                String gpuName = props.deviceNameString();
                System.out.println("Found a GPU named: " + gpuName);
                if (gpuName.contains(requestedGPUName)) {
                    System.out.println(gpuName + " was deemed suitable for the requested name '"+requestedGPUName+'\'');
                    return new XeGPU(device, gpuName);
                }
            }
        }
        throw VkError.format("Couldn't find a GPU called '%s'", requestedGPUName);
    }

}
