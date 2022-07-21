package com.xenon.vulkan.structs;

import com.xenon.vulkan.boostrap.VkError;
import com.xenon.vulkan.boostrap.VkShaderModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Zenon
 */
public class VkGraphicsPipelineCreateInfos {


    public static VkGraphicsPipelineCreateInfo usual(
            MemoryStack stack,
            VkShaderModule[] shaderModules,
            @NotNull VertexInputStateCI vertexInputStateCI,
            @NotNull ViewportStateCI viewportStateCI,
            @Nullable MultisampleStateCI multisampleStateCI,
            long layout,
            long renderpass,
            int subpass,
            long basePipelineHandle,
            int basePipelineIndex) {
        return fromModules(stack, 0, shaderModules,
                vertexInputStateCI,
                inputAssemblyState(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false),
                null,
                viewportStateCI,
                rasterizationState_usual(0, false, false, VK_POLYGON_MODE_FILL,
                        VK_CULL_MODE_BACK_BIT, VK_FRONT_FACE_COUNTER_CLOCKWISE),
                multisampleStateCI,
                null,
                colorBlendState(0, 0, false, 0,
                        new ColorBlendAttachmentStateCI[]{colorBlendAttachmentState_usual()}, new float[]{0, 0, 0, 0}),
                dynamicState(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR),
                layout,
                renderpass,
                subpass,
                basePipelineHandle,
                basePipelineIndex
        );
    }

    /**
     * Macro for filling {@link VkGraphicsPipelineCreateInfo}.
     * @param stack the stack
     * @param flags VkPipelineCreateFlags
     * @param shaderModules the shader modules
     * @param vertexInputStateCI VkPipelineVertexInputStateCreateInfo
     * @param inputAssemblyStateCI VkPipelineInputAssemblyStateCreateInfo
     * @param tesselationStateCI VkPipelineTessellationStateCreateInfo
     * @param viewportStateCI VkPipelineViewportStateCreateInfo
     * @param rasterizationStateCI VkPipelineRasterizationStateCreateInfo
     * @param multisampleStateCI VkPipelineMultisampleStateCreateInfo
     * @param depthStencilStateCI VkPipelineDepthStencilStateCreateInfo
     * @param colorBlendStateCI VkPipelineColorBlendStateCreateInfo
     * @param dynamicStateCI VkPipelineDynamicStateCreateInfo
     * @param layout the pipeline layout
     * @param renderpass the render pass
     * @param subpass the subpass
     * @param basePipelineHandle the base pipeline handle
     * @param basePipelineIndex the base pipeline index
     * @return a filled VkGraphicsPipelineCreateInfo
     */
    public static VkGraphicsPipelineCreateInfo fromModules(
            MemoryStack stack,
            int flags,
            VkShaderModule[] shaderModules,
            @Nullable VertexInputStateCI vertexInputStateCI,
            @Nullable InputAssemblyStateCI inputAssemblyStateCI,
            @Nullable TesselationStateCI tesselationStateCI,
            @Nullable ViewportStateCI viewportStateCI,
            @Nullable RasterizationStateCI rasterizationStateCI,
            @Nullable MultisampleStateCI multisampleStateCI,
            @Nullable DepthStencilStateCI depthStencilStateCI,
            @Nullable ColorBlendStateCI colorBlendStateCI,
            @Nullable DynamicStateCI dynamicStateCI,
            long layout,
            long renderpass,
            int subpass,
            long basePipelineHandle,
            int basePipelineIndex) {

        VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.malloc(shaderModules.length, stack);

        for (VkShaderModule module : shaderModules)
            module.fillCreateInfo(stack, stages);

        VkPipelineVertexInputStateCreateInfo vi = vertexInputStateCI == null ? null : vertexInputStateCI.build(stack);
        VkPipelineInputAssemblyStateCreateInfo ia = inputAssemblyStateCI == null ? null : inputAssemblyStateCI.build(stack);
        VkPipelineTessellationStateCreateInfo t = tesselationStateCI == null ? null : tesselationStateCI.build(stack);
        VkPipelineViewportStateCreateInfo v = viewportStateCI == null ? null : viewportStateCI.build(stack);
        VkPipelineRasterizationStateCreateInfo r = rasterizationStateCI == null ? null : rasterizationStateCI.build(stack);
        VkPipelineMultisampleStateCreateInfo m = multisampleStateCI == null ? null : multisampleStateCI.build(stack);
        VkPipelineDepthStencilStateCreateInfo ds = depthStencilStateCI == null ? null : depthStencilStateCI.build(stack);
        VkPipelineColorBlendStateCreateInfo cb = colorBlendStateCI == null ? null : colorBlendStateCI.build(stack);
        VkPipelineDynamicStateCreateInfo d = dynamicStateCI == null ? null : dynamicStateCI.build(stack);

        return custom(stack, flags, stages, vi, ia, t, v, r, m, ds, cb, d,
                layout, renderpass, subpass, basePipelineHandle,basePipelineIndex);
    }


    /**
     * Not so useful macro for creating a VkGraphicsPipelineCreateInfo
     */
    public static VkGraphicsPipelineCreateInfo custom(
            MemoryStack stack,
            int flags,
            VkPipelineShaderStageCreateInfo.Buffer pStages,
            VkPipelineVertexInputStateCreateInfo pVertexInputState,
            VkPipelineInputAssemblyStateCreateInfo pInputAssemblyState,
            VkPipelineTessellationStateCreateInfo pTessellationState,
            VkPipelineViewportStateCreateInfo pViewportState,
            VkPipelineRasterizationStateCreateInfo pRasterizationState,
            VkPipelineMultisampleStateCreateInfo pMultisampleState,
            VkPipelineDepthStencilStateCreateInfo pDepthStencilState,
            VkPipelineColorBlendStateCreateInfo pColorBlendState,
            VkPipelineDynamicStateCreateInfo pDynamicState,
            long layout,
            long renderPass,
            int subpass,
            long basePipelineHandle,
            int basePipelineIndex) {

        return VkGraphicsPipelineCreateInfo.malloc(stack)
                .sType$Default()
                .pNext(0)
                .flags(flags)
                .pStages(pStages)
                .pVertexInputState(pVertexInputState)
                .pInputAssemblyState(pInputAssemblyState)
                .pTessellationState(pTessellationState)
                .pViewportState(pViewportState)
                .pRasterizationState(pRasterizationState)
                .pMultisampleState(pMultisampleState)
                .pDepthStencilState(pDepthStencilState)
                .pColorBlendState(pColorBlendState)
                .pDynamicState(pDynamicState)
                .layout(layout)
                .renderPass(renderPass)
                .subpass(subpass)
                .basePipelineHandle(basePipelineHandle)
                .basePipelineIndex(basePipelineIndex);
    }


    /*---------------- MACROS -------------------*/

    // vertex input

    /**
     *
     * @param pNext pNext
     * @param pVertexBindingDescriptions the vertex bindings
     * @param pVertexAttributeDescriptions the vertex attributes
     * @return a new macro for {@link VkPipelineVertexInputStateCreateInfo}
     * @see VertexInputStateCI#build(MemoryStack)
     */
    public static VertexInputStateCI vertexInputState(
            long pNext,
            VkVertexInputBindingDescription.Buffer pVertexBindingDescriptions,
            VkVertexInputAttributeDescription.Buffer pVertexAttributeDescriptions) {
        return new VertexInputStateCI(pNext, pVertexBindingDescriptions, pVertexAttributeDescriptions);
    }

    public record VertexInputStateCI(
            long pNext,
            VkVertexInputBindingDescription.Buffer pVertexBindingDescriptions,
            VkVertexInputAttributeDescription.Buffer pVertexAttributeDescriptions) {

        public VkPipelineVertexInputStateCreateInfo build(MemoryStack stack) {
            return VkPipelineVertexInputStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(0)
                    .pVertexBindingDescriptions(pVertexBindingDescriptions)
                    .pVertexAttributeDescriptions(pVertexAttributeDescriptions);
        }
    }



    // input assembly

    /**
     *
     * @param topology the topology
     * @param primitiveRestart whether primitive restart should be enabled
     * @return a new macro for {@link VkPipelineInputAssemblyStateCreateInfo}
     * @see InputAssemblyStateCI#build(MemoryStack)
     */
    public static InputAssemblyStateCI inputAssemblyState(int topology, boolean primitiveRestart) {
        return new InputAssemblyStateCI(topology, primitiveRestart);
    }

    public record InputAssemblyStateCI(int topology, boolean primitiveRestart) {

        public VkPipelineInputAssemblyStateCreateInfo build(MemoryStack stack) {
            return VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .topology(topology)
                    .primitiveRestartEnable(primitiveRestart);
        }
    }


    // tesselation

    /**
     *
     * @param pNext pNext
     * @param patchControlPoints patchControlPoints
     * @return a new macro for VkPipelineTessellationStateCreateInfo
     * @see TesselationStateCI#build(MemoryStack)
     */
    public static TesselationStateCI tesselationState(long pNext, int patchControlPoints) {
        return new TesselationStateCI(pNext, patchControlPoints);
    }

    public record TesselationStateCI(long pNext, int patchControlPoints) {

        public VkPipelineTessellationStateCreateInfo build(MemoryStack stack) {
            return VkPipelineTessellationStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(0)
                    .patchControlPoints(patchControlPoints);
        }
    }



    // viewports & scissors

    /**
     * Same as {@link #viewportState(long, VkViewport.Buffer, int, VkRect2D.Buffer, int)} but for dynamic
     * viewport & scissors.
     * @param pNext pNext
     * @param viewportCount viewport count
     * @param scissorCount scissor count
     * @return a new macro for {@link VkPipelineViewportStateCreateInfo}
     * @see #viewportState(long, VkViewport.Buffer, int, VkRect2D.Buffer, int)
     */
    public static ViewportStateCI viewportStateDynamic(long pNext, int viewportCount, int scissorCount) {
        return viewportState(pNext, null, viewportCount, null, scissorCount);
    }

    /**
     *
     * @param pNext pNext
     * @param pViewports an array of viewports
     * @param viewportCount viewport count
     * @param pScissors an array of scissors
     * @param scissorCount scissor count
     * @return a new macro for {@link VkPipelineViewportStateCreateInfo}
     * @see ViewportStateCI#build(MemoryStack)
     */
    public static ViewportStateCI viewportState(
            long pNext,
            @Nullable VkViewport.Buffer pViewports,
            int viewportCount,
            @Nullable VkRect2D.Buffer pScissors,
            int scissorCount) {
        return new ViewportStateCI(pNext, pViewports, viewportCount, pScissors, scissorCount);
    }

    public record ViewportStateCI(long pNext,
                                  @Nullable VkViewport.Buffer pViewports, int viewportCount,
                                  @Nullable VkRect2D.Buffer pScissors, int scissorCount) {

        public VkPipelineViewportStateCreateInfo build(MemoryStack stack) {
            return VkPipelineViewportStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(0)
                    .pViewports(pViewports)
                    .viewportCount(viewportCount)
                    .pScissors(pScissors)
                    .scissorCount(scissorCount);
        }
    }


    // rasterization

    /**
     * Same as {@link #rasterizationState(long, boolean, boolean, int, int, int, boolean, float, float, float, float)}
     * but with depth bias disabled and line width set to 1.0f.
     * @param pNext pNext
     * @param depthClamp whether clamping depth should be enabled
     * @param rasterizerDiscard whether the rasterizer should discard everything. keep it to true
     * @param polygonMode the polygon mode
     * @param cullMode the culling mode
     * @param frontFace define what's a front face
     * @return a new macro for {@link VkPipelineRasterizationStateCreateInfo}
     * @see #rasterizationState(long, boolean, boolean, int, int, int, boolean, float, float, float, float)
     */
    public static RasterizationStateCI rasterizationState_usual(
            long pNext,
            boolean depthClamp,
            boolean rasterizerDiscard,
            int polygonMode,
            int cullMode,
            int frontFace) {
        return rasterizationState(pNext, depthClamp, rasterizerDiscard, polygonMode, cullMode, frontFace,
                false, 0, 0, 0, 1f);
    }

    /**
     *
     * @param pNext pNext
     * @param depthClamp whether clamping depth should be enabled
     * @param rasterizerDiscard whether the rasterizer should discard everything. keep it to true
     * @param polygonMode the polygon mode
     * @param cullMode the culling mode
     * @param frontFace define what's a front face
     * @param depthBias whether depth bias should be enabled
     * @param depthBiasConstantFactor depth bias constant factor
     * @param depthBiasClamp depth bias clamp
     * @param depthBiasSlopeFactor depth bias slope factor
     * @param lineWidth line width
     * @return a new macro for {@link VkPipelineRasterizationStateCreateInfo}
     * @see RasterizationStateCI#build(MemoryStack)
     */
    public static RasterizationStateCI rasterizationState(
            long pNext,
            boolean depthClamp,
            boolean rasterizerDiscard,
            int polygonMode,
            int cullMode,
            int frontFace,
            boolean depthBias,
            float depthBiasConstantFactor,
            float depthBiasClamp,
            float depthBiasSlopeFactor,
            float lineWidth) {
        return new RasterizationStateCI(pNext, depthClamp, rasterizerDiscard, polygonMode, cullMode,
                frontFace, depthBias, depthBiasConstantFactor, depthBiasClamp, depthBiasSlopeFactor, lineWidth);
    }

    public record RasterizationStateCI(
            long pNext,
            boolean depthClamp,
            boolean rasterizerDiscard,
            int polygonMode,
            int cullMode,
            int frontFace,
            boolean depthBias,
            float depthBiasConstantFactor,
            float depthBiasClamp,
            float depthBiasSlopeFactor,
            float lineWidth) {

        public VkPipelineRasterizationStateCreateInfo build(MemoryStack stack) {
            return VkPipelineRasterizationStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(0)
                    .depthClampEnable(depthClamp)
                    .rasterizerDiscardEnable(rasterizerDiscard)
                    .polygonMode(polygonMode)
                    .cullMode(cullMode)
                    .frontFace(frontFace)
                    .depthBiasEnable(depthBias)
                    .depthBiasConstantFactor(depthBiasConstantFactor)
                    .depthBiasClamp(depthBiasClamp)
                    .depthBiasSlopeFactor(depthBiasSlopeFactor)
                    .lineWidth(lineWidth);
        }
    }



    // multisample

    /**
     *
     * @param pNext pNext
     * @param rasterizationSamples rasterization samples
     * @param sampleShading whether sample shading should be enabled
     * @param minSampleShading minimum sample shading
     * @param pSampleMask pSampleMask, possibly null
     * @param alphaToCoverage whether alphaToCoverage should be enabled
     * @param alphaToOne whether alphaToOne should be enabled
     * @return a new macro for {@link VkPipelineMultisampleStateCreateInfo}
     * @see MultisampleStateCI#build(MemoryStack)
     */
    public static MultisampleStateCI multisampleState(
            long pNext,
            int rasterizationSamples,
            boolean sampleShading,
            float minSampleShading,
            @Nullable IntBuffer pSampleMask,
            boolean alphaToCoverage,
            boolean alphaToOne
    ) {
        return new MultisampleStateCI(
                pNext, rasterizationSamples, sampleShading, minSampleShading, pSampleMask, alphaToCoverage, alphaToOne);
    }

    public record MultisampleStateCI(
            long pNext,
            int rasterizationSamples,
            boolean sampleShading,
            float minSampleShading,
            @Nullable IntBuffer pSampleMask,
            boolean alphaToCoverage,
            boolean alphaToOne) {

        public VkPipelineMultisampleStateCreateInfo build(MemoryStack stack) {
            return VkPipelineMultisampleStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(0)
                    .rasterizationSamples(rasterizationSamples)
                    .sampleShadingEnable(sampleShading)
                    .minSampleShading(minSampleShading)
                    .pSampleMask(pSampleMask)
                    .alphaToCoverageEnable(alphaToCoverage)
                    .alphaToOneEnable(alphaToOne);
        }
    }



    // depth stencil

    /**
     *
     * @param flags the flags
     * @param depthTest whether depth should be tested
     * @param depthWrite whether writes to the depth buffer should be enabled
     * @param depthCompareOp depth comparison operation
     * @param depthBoundsTest whether bounds should be tested during depth test
     * @param stencilTest whether stencil test should be enabled
     * @param front the front stencil operation state
     * @param back the back stencil operation state
     * @param minDepthBounds min depth bounds
     * @param maxDepthBounds max depth bounds
     * @return a new macro for {@link VkPipelineDepthStencilStateCreateInfo}
     * @see DepthStencilStateCI#build(MemoryStack)
     */
    public static DepthStencilStateCI depthStencilState(
            int flags,
            boolean depthTest,
            boolean depthWrite,
            int depthCompareOp,
            boolean depthBoundsTest,
            boolean stencilTest,
            VkStencilOpState front,
            VkStencilOpState back,
            float minDepthBounds,
            float maxDepthBounds
    ) {
        return new DepthStencilStateCI(flags, depthTest, depthWrite, depthCompareOp, depthBoundsTest,
                stencilTest, front, back, minDepthBounds, maxDepthBounds);
    }

    public record DepthStencilStateCI(
            int flags,
            boolean depthTest,
            boolean depthWrite,
            int depthCompareOp,
            boolean depthBoundsTest,
            boolean stencilTest,
            VkStencilOpState front,
            VkStencilOpState back,
            float minDepthBounds,
            float maxDepthBounds) {

        public VkPipelineDepthStencilStateCreateInfo build(MemoryStack stack) {
            return VkPipelineDepthStencilStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(0)
                    .flags(flags)
                    .depthTestEnable(depthTest)
                    .depthWriteEnable(depthWrite)
                    .depthCompareOp(depthCompareOp)
                    .depthBoundsTestEnable(depthBoundsTest)
                    .stencilTestEnable(stencilTest)
                    .front(front)
                    .back(back)
                    .minDepthBounds(minDepthBounds)
                    .maxDepthBounds(maxDepthBounds);
        }
    }



    // color blending

    /**
     *
     * @param pNext pNext
     * @param flags flags
     * @param logicOpEnable whether blend operation should be enabled
     * @param logicOp the blending logic operation
     * @param colorBlendAttachmentStates pAttachments
     * @param blendConstants the blend constants. Must be 4 of length
     * @return a new macro for {@link VkPipelineColorBlendStateCreateInfo}
     * @see ColorBlendStateCI#build(MemoryStack)
     */
    public static ColorBlendStateCI colorBlendState(
            long pNext,
            int flags,
            boolean logicOpEnable,
            int logicOp,
            @Nullable ColorBlendAttachmentStateCI[] colorBlendAttachmentStates,
            float[] blendConstants
    ) {
        if (blendConstants.length != 4)
            throw VkError.log("Invalid blend constants count");
        return new ColorBlendStateCI(pNext, flags, logicOpEnable, logicOp, colorBlendAttachmentStates, blendConstants);
    }

    public record ColorBlendStateCI(
            long pNext,
            int flags,
            boolean logicOpEnable,
            int logicOp,
            @Nullable ColorBlendAttachmentStateCI[] colorBlendAttachmentStates,
            float[] blendConstants) {

        public VkPipelineColorBlendStateCreateInfo build(MemoryStack stack) {
            var ci = VkPipelineColorBlendStateCreateInfo.malloc(stack)
                    .sType$Default()
                    .pNext(pNext)
                    .flags(flags)
                    .logicOpEnable(logicOpEnable)
                    .logicOp(logicOp);
            if (colorBlendAttachmentStates != null) {
                VkPipelineColorBlendAttachmentState.Buffer buffer =
                        VkPipelineColorBlendAttachmentState.malloc(colorBlendAttachmentStates.length, stack);
                for (ColorBlendAttachmentStateCI att : colorBlendAttachmentStates) {
                    if (att == null)
                        throw VkError.log("Found null in color blend attachment array: " +
                                Arrays.toString(colorBlendAttachmentStates));
                    att.fillContainer(buffer);
                }
                ci.pAttachments(buffer);
            }

            for (int i = 0; i < blendConstants.length; i++)
                ci.blendConstants(i, blendConstants[i]);

            return ci;
        }
    }


    /**
     * Same as {@link #colorBlendAttachmentState(boolean, int, int, int, int, int, int, int)} with default parameters.
     * @return a new macro for {@link VkPipelineColorBlendAttachmentState}
     * @see #colorBlendAttachmentState(boolean, int, int, int, int, int, int, int)
     */
    public static ColorBlendAttachmentStateCI colorBlendAttachmentState_usual() {
        return colorBlendAttachmentState(true, VK_BLEND_FACTOR_ONE, VK_BLEND_FACTOR_ZERO, VK_BLEND_OP_ADD,
                VK_BLEND_FACTOR_ONE, VK_BLEND_FACTOR_ZERO, VK_BLEND_OP_ADD, VK_COLOR_COMPONENT_R_BIT |
                        VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
    }

    /**
     *
     * @param blend whether blend should be enabled
     * @param srcColorBlendFactor src color blend factor
     * @param dstColorBlendFactor dst color blend factor
     * @param colorBlendOp color blend operation
     * @param srcAlphaBlendFactor src alpha blend factor
     * @param dstAlphaBlendFactor dst alpha blend factor
     * @param alphaBlendOp alpha blend operation
     * @param colorWriteMask color write mask
     * @return a new macro for {@link VkPipelineColorBlendAttachmentState}
     * @see ColorBlendAttachmentStateCI#fillContainer(VkPipelineColorBlendAttachmentState.Buffer)
     */
    public static ColorBlendAttachmentStateCI colorBlendAttachmentState(
            boolean blend,
            int srcColorBlendFactor,
            int dstColorBlendFactor,
            int colorBlendOp,
            int srcAlphaBlendFactor,
            int dstAlphaBlendFactor,
            int alphaBlendOp,
            int colorWriteMask
    ) {
        return new ColorBlendAttachmentStateCI(blend, srcColorBlendFactor, dstColorBlendFactor, colorBlendOp,
                srcAlphaBlendFactor, dstAlphaBlendFactor, alphaBlendOp, colorWriteMask);
    }

    public record ColorBlendAttachmentStateCI(
            boolean blend,
            int srcColorBlendFactor,
            int dstColorBlendFactor,
            int colorBlendOp,
            int srcAlphaBlendFactor,
            int dstAlphaBlendFactor,
            int alphaBlendOp,
            int colorWriteMask) {

        public void fillContainer(VkPipelineColorBlendAttachmentState.Buffer container) {
            container.blendEnable(blend)
                    .srcColorBlendFactor(srcColorBlendFactor)
                    .dstColorBlendFactor(dstColorBlendFactor)
                    .colorBlendOp(colorBlendOp)
                    .srcAlphaBlendFactor(srcAlphaBlendFactor)
                    .dstAlphaBlendFactor(dstAlphaBlendFactor)
                    .alphaBlendOp(alphaBlendOp)
                    .colorWriteMask(colorWriteMask);
        }

    }


    // dynamic state

    /**
     *
     * @param dynamicStates the requested dynamic states
     * @return a new macro for {@link VkPipelineDynamicStateCreateInfo}
     * @see DynamicStateCI#build(MemoryStack)
     */
    public static DynamicStateCI dynamicState(int... dynamicStates) {
        return new DynamicStateCI(dynamicStates);
    }

    public record DynamicStateCI(int... dynamicStates) {

        public VkPipelineDynamicStateCreateInfo build(MemoryStack stack) {
            return VkPipelineDynamicStateCreateInfo.calloc(stack)
                    .sType$Default()
                    .pDynamicStates(stack.ints(dynamicStates));
        }
    }

}
