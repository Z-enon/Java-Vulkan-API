package com.xenon.vulkan.boostrap;

import com.xenon.vulkan.info.GPUFeaturesCreateInfo;
import com.xenon.vulkan.info.SwapchainAndCo;
import com.xenon.vulkan.info.QueueFeaturesCreateInfo;
import com.xenon.vulkan.info.SwapchainCreateInfo;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.Collection;
import java.util.function.IntToLongFunction;

import static com.xenon.vulkan.boostrap.XeUtils.checkVK;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public final class VulkanBundle {

    public static VulkanBundle alloc() {
        return new VulkanBundle();
    }
    public static VulkanBundle alloc(
            VkWindow window,
            VkApplicationInfo appInfo,
            @Nullable VkAllocationCallbacks callbacks,
            Collection<String> requestedLayers,
            Collection<String> deviceExtensions,
            GPUFeaturesCreateInfo gpuFeaturesCreateInfo,
            QueueFeaturesCreateInfo queueFeaturesCreateInfo,
            SwapchainCreateInfo swapchainCreateInfo,
            @Nullable IntToLongFunction renderPassCreation,
            String GPUName,
            String GPUVendor,
            VkSettings settings
    ) {
        return new VulkanBundle(
                window,
                appInfo,
                callbacks,
                requestedLayers,
                deviceExtensions,
                gpuFeaturesCreateInfo,
                queueFeaturesCreateInfo,
                swapchainCreateInfo,
                renderPassCreation,
                GPUName,
                GPUVendor,
                settings
        );
    }

    // API external fields
    public VkWindow window;
    public VkApplicationInfo appInfo;
    @Nullable
    public VkAllocationCallbacks allocationCallbacks;
    public Collection<String> requestedLayers;
    public Collection<String> deviceExtensions;
    public GPUFeaturesCreateInfo gpuFeatures;
    public QueueFeaturesCreateInfo queueFeatures;
    public SwapchainCreateInfo swapchainCreateInfo;
    @Nullable
    public IntToLongFunction renderPassCreation;
    public String GPUName;
    public String GPUVendor;
    public VkSettings settings;

    // API internal fields
    VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo;
    VkInstance instance;
    long debug;
    XeGPU gpu;
    long surface;
    VkDevice device;
    SwapchainAndCo swapchainAndCo;

    private VulkanBundle() {}
    private VulkanBundle(
            VkWindow window,
            VkApplicationInfo appInfo,
            @Nullable VkAllocationCallbacks callbacks,
            Collection<String> requestedLayers,
            Collection<String> deviceExtensions,
            GPUFeaturesCreateInfo gpuFeaturesCreateInfo,
            QueueFeaturesCreateInfo queueFeaturesCreateInfo,
            SwapchainCreateInfo swapchainCreateInfo,
            @Nullable IntToLongFunction renderPassCreation,
            String GPUName,
            String GPUVendor,
            VkSettings settings
    ) {
        this.window = window;
        this.appInfo = appInfo;
        allocationCallbacks = callbacks;
        this.requestedLayers = requestedLayers;
        this.deviceExtensions = deviceExtensions;
        gpuFeatures = gpuFeaturesCreateInfo;
        queueFeatures = queueFeaturesCreateInfo;
        this.swapchainCreateInfo = swapchainCreateInfo;
        this.renderPassCreation = renderPassCreation;
        this.GPUName = GPUName;
        this.GPUVendor = GPUVendor;
        this.settings = settings;
    }

    /**
     * If any of its fields is null, throw a RuntimeException
     */
    public void sanity1() {
        if (
                window == null ||
                appInfo == null ||
                requestedLayers == null ||
                deviceExtensions == null ||
                gpuFeatures == null ||
                queueFeatures == null ||
                swapchainCreateInfo == null ||
                GPUName == null ||
                GPUVendor == null ||
                settings == null
        )
            throw VkError.log("VulkanedBundle has got some missing external fields: " + toString1());

        if (renderPassCreation == null && !settings.useDynamicRendering)    // default render pass creation
            renderPassCreation = imgFormat -> {
            try (MemoryStack stack = stackPush()) {
                VkAttachmentDescription.Buffer colorAttachment = VkAttachmentDescription.malloc(1, stack)
                        .flags(0)
                        .format(imgFormat)
                        .samples(VK_SAMPLE_COUNT_1_BIT)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

                VkAttachmentReference.Buffer colorAttachmentRef = VkAttachmentReference.malloc(1, stack)
                        .attachment(0)
                        .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

                VkSubpassDescription.Buffer subpass = VkSubpassDescription.calloc(1, stack)
                        .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                        .colorAttachmentCount(1)
                        .pColorAttachments(colorAttachmentRef);

                VkSubpassDependency.Buffer dependency = VkSubpassDependency.calloc(1, stack)
                        .srcSubpass(VK_SUBPASS_EXTERNAL)
                        .dstSubpass(0)
                        .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                        .srcAccessMask(0)
                        .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                        .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

                VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc(stack)
                        .sType$Default()
                        .pAttachments(colorAttachment)
                        .pSubpasses(subpass)
                        .pDependencies(dependency);

                LongBuffer pRenderPass = stack.mallocLong(1);

                checkVK(vkCreateRenderPass(device, renderPassInfo, null, pRenderPass),
                        "Failed to create render pass");

                return pRenderPass.get(0);
            }
        };
    }

    public void sanity2() {
        if (
                debugCreateInfo == null ||
                instance == null ||
                debug == VK_NULL_HANDLE ||
                gpu == null ||
                surface == VK_NULL_HANDLE ||
                device == null ||
                swapchainAndCo == null

        )
            throw VkError.log("[API Internal error]VulkanedBundle has got some missing internal fields" + this);
    }

    public String toString1() {
        return "VulkanBundle{" +
                "window=" + window +
                ", appInfo=" + appInfo +
                ", allocationCallbacks=" + allocationCallbacks +
                ", requestedLayers=" + requestedLayers +
                ", deviceExtensions=" + deviceExtensions +
                ", gpuFeatures=" + gpuFeatures +
                ", queueFeatures=" + queueFeatures +
                ", swapchainCreateInfo=" + swapchainCreateInfo +
                ", GPUName='" + GPUName + '\'' +
                ", GPUVendor='" + GPUVendor + '\'' +
                ", settings='" + settings + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "VulkanBundle{" +
                "window=" + window +
                ", appInfo=" + appInfo +
                ", allocationCallbacks=" + allocationCallbacks +
                ", requestedLayers=" + requestedLayers +
                ", deviceExtensions=" + deviceExtensions +
                ", gpuFeatures=" + gpuFeatures +
                ", queueFeatures=" + queueFeatures +
                ", swapchainCreateInfo=" + swapchainCreateInfo +
                ", GPUName='" + GPUName + '\'' +
                ", GPUVendor='" + GPUVendor + '\'' +
                ", settings='" + settings + '\'' +
                ", debugCreateInfo=" + debugCreateInfo +
                ", instance=" + instance +
                ", debug=" + debug +
                ", gpu=" + gpu +
                ", surface=" + surface +
                ", device=" + device +
                ", pipelineInfo=" + swapchainAndCo +
                '}';
    }
}
