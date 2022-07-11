package com.xenon.vulkan.boostrap;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.PrintStream;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Sets up Vulkan debug.
 * @author Zenon
 */
public final class VkDebug {


    /**
     * All severities are enabled in order to see crashes, however you can prevent some severities
     * from showing in the console using this field.
     */
    public static int displayed_severities = VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;

    /**
     * whether we are in development. Essentially
     * <code>org.lwjgl.system.Configuration.DEBUG.get(true)</code>.
     */
    public static final boolean ENABLED = Configuration.DEBUG.get(true);


    /**
     * Creates a new debugCreateInfo structure on the stack
     * @param stack the stack
     * @return the new debugCreateInfo structure
     */
    public static VkDebugUtilsMessengerCreateInfoEXT genDebugCreateInfo(MemoryStack stack) {
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
                PrintStream ps = severity == VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT ? System.err : System.out;
                ps.println(s + msg.pMessageString());
            }
            return VK_FALSE;
        });
        return debugCreateInfo;
    }

    @Once
    public static long setupDebug(VulkanBundle bundle, VkInstance instance) {
        if (ENABLED) try (MemoryStack stack = stackPush()) {
            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

            if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") == NULL ||
                    vkCreateDebugUtilsMessengerEXT(
                            instance,
                            bundle.debugCreateInfo,
                            bundle.allocationCallbacks,
                            pDebugMessenger
                    ) != VK_SUCCESS)
                throw VkError.log("Failed to set up debug messenger");

            return pDebugMessenger.get(0);
        }
        return VK_NULL_HANDLE;
    }

    /**
     * Dispose of VkDebugMessenger
     */
    @Once
    public static void dispose(VulkanisedXenon xenon) {
        if (ENABLED && vkGetInstanceProcAddr(xenon.vkInstance(), "vkDestroyDebugUtilsMessengerEXT") != NULL)
            vkDestroyDebugUtilsMessengerEXT(xenon.vkInstance(), xenon.debug_handle(), xenon.allocationCallbacks());
    }

}
