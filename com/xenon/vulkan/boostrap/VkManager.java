package com.xenon.vulkan.boostrap;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.*;

import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.VK_FALSE;

/**
 * Anti boilerplate code class.
 * Contains everything necessary to set up a Vulkan graphic environment. May consider adding some hooks for compute
 * queues (ray tracing) or stuff like that.
 *
 * @author Zenon
 */
public final class VkManager {

    /**
     * boilerplate killer method.
     * Creates a new HashSet and adds "VK_LAYER_KHRONOS_validation" to it before calling
     * {@link #init(VkApplicationInfo, VkAllocationCallbacks, Collection, Collection, String, String, GPUFeatures, QueueFeatures, VkWindow)}.
     *
     * @param appInfo        the appInfo for VkInstance
     * @param callbacks      the allocation callbacks for VkInstance and DebugUtilsMessengerEXT
     * @param GPU_vendor     the desired GPU vendor's name
     * @param GPU_name       the desired GPU name (or a part of; See {@link VkPhysicalDevices})
     * @param gpuFeatures    the GPU features the application requires
     * @param queueFeatures  the features describing the needs of the application in terms of queues
     * @param window         the VkWindow object
     */
    @Once
    public static void init(VkApplicationInfo appInfo,
                            VkAllocationCallbacks callbacks,
                            String GPU_vendor,
                            String GPU_name,
                            GPUFeatures gpuFeatures,
                            QueueFeatures queueFeatures,
                            VkWindow window) {
        Set<String> s = new HashSet<>();
        if (VkDebug.ENABLED)
            s.add("VK_LAYER_KHRONOS_validation");
        init(appInfo, callbacks, s, Set.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME),
                GPU_vendor, GPU_name, gpuFeatures, queueFeatures, window);
    }

    /**
     * boilerplate killer method.
     *
     * @param appInfo          the appInfo for VkInstance
     * @param callbacks        the allocation callbacks for VkInstance and DebugUtilsMessengerEXT
     * @param requestedLayers  the requested VK Layers. expected to already contains VK_LAYER_KHRONOS_validation
     *                         (if desired). Collection must be mutable!
     * @param deviceExtensions the requested device features
     * @param GPU_vendor       the desired GPU vendor's name
     * @param GPU_name         the desired GPU name (or a part of; See {@link VkPhysicalDevices})
     * @param gpuFeatures      the GPU features the application requires
     * @param queueFeatures    the features describing the needs of the application in terms of queues
     * @param window           the VkWindow object.
     */
    @Once
    public static void init(VkApplicationInfo appInfo,
                            VkAllocationCallbacks callbacks,
                            Collection<String> requestedLayers,
                            Collection<String> deviceExtensions,
                            String GPU_vendor,
                            String GPU_name,
                            GPUFeatures gpuFeatures,
                            QueueFeatures queueFeatures,
                            VkWindow window) {
        System.out.println("Started Vulkan initialization");
        //VkPhysicalDevices.configureLayers(requestedLayers, GPU_vendor);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);

            debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);

            debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT |
                    VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT |
                    VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
                    VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);

            debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
                    VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
                    VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
            debugCreateInfo.pfnUserCallback((severity, type, pCallback, pUser) -> {
                if ((VkDebug.displayed_severities & severity) != 0) {
                    @SuppressWarnings("resource")   // EXTREMELY IMPORTANT, DO NOT FREE msg!!!! heap corruption risk
                    VkDebugUtilsMessengerCallbackDataEXT msg = VkDebugUtilsMessengerCallbackDataEXT.create(pCallback);
                    String s = switch (severity) {
                        case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> "[VERBOSE]";
                        case VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> "[INFO]";
                        case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> "[WARNING]";
                        case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> "[ERROR]";
                        default -> throw VkError.impossible();
                    } +
                            " msg type: " + switch (type) {
                        case VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT -> "GENERAL";
                        case VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT -> "VALIDATION";
                        case VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT -> "PERFORMANCE";
                        default -> throw VkError.impossible();
                    } +
                            " details: ";
                    System.err.println(s + msg.pMessageString());
                }
                return VK_FALSE;
            });


            //VkInstances.create(appInfo, callbacks, debugCreateInfo, requestedLayers);
            //VkDebug.setupDebug(callbacks, debugCreateInfo);


            //VkPhysicalDevices.pick(GPU_name);
            //VkPhysicalDevices.validate(gpuFeatures, deviceExtensions);

            //window.createSurface(callbacks);

            //VkLogicalDevices.create(
              //      callbacks, queueFeatures, requestedLayers, deviceExtensions, gpuFeatures, window.surface);



        }





        System.out.println("Finished initializing Vulkan");
    }

    /**
     * Dispose of every Vulkan thing necessary in one fell swoop.
     */
    @Once
    public static void dispose() {
        System.out.println("Disposing of Vulkan objects");
        //VkLogicalDevices.dispose();
        //VkDebug.dispose();
        //VkInstances.dispose();
        System.out.println("Finished disposing of Vulkan objects");
    }


}
