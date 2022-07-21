package com.xenon.vulkan.info;

import com.xenon.vulkan.boostrap.*;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;

/**
 * Defines the queues the application requires, with the same spirit as {@link GPUFeaturesCreateInfo}.
 * The user first defines what makes a given queue family suitable for its need using a lambda by calling
 * {@link #feature(int, BiPredicate)}.<br><br>
 * After feeding this structure correctly (done in
 * {@link VkLogicalDevices#create(VulkanBundle, XeGPU, long)})
 * , the resulting VkQueues can be retrieved with {@link #result(int)}.
 * @author Zenon
 */
public class QueueFeaturesCreateInfo implements Iterable<BiPredicate<VkQueueFamilyProperties, Boolean>>{

    /**
     * Creates a <code>QueueFeatures</code> object and fill the first 2 slots.<br><br>
     * <h3>IMPORTANT</h3>
     *
     * Since the first two queues MUST be respectively the graphic and the support presentation queues
     * for {@link VkSwapchains#createSwapchain(VulkanBundle, long, XeGPU, VkDevice)}
     * to work properly,
     * this method automatically fills up the first two slots of the created object, before returning it.
     * Do NOT override the first two slots!
     * @return a new QueueFeatures object
     */
    public static QueueFeaturesCreateInfo recommended() {
        var r = new QueueFeaturesCreateInfo(2);
        r.feature(0, (props, surface_present) -> (props.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0);
        r.feature(1, (props, surface_present) -> surface_present);
        return r;
    }

    /**
     * Creates a QueueFeatures object.
     * @param capacity the maximal number of VkQueues
     * @return a new QueueFeatures object
     */
    public static QueueFeaturesCreateInfo custom(int capacity) {
        return new QueueFeaturesCreateInfo(capacity);
    }


    /*predicates to filter available queue families with*/
    private final Object[] mustFeature;
    /*resulting VkQueues*/
    private final VkQueue[] results;
    /*the queue family index for each of the resulting VkQueues*/
    private final int[] queueFamiliesIndices;
    /*the queues priorities*/
    private final float[] resultsPriorities;

    /**
     * Instantiate the features needed by the application.
     * @param capacity the maximal queues capacity
     */
    protected QueueFeaturesCreateInfo(int capacity) {
        mustFeature = new Object[capacity];
        results = new VkQueue[capacity];
        queueFamiliesIndices = new int[capacity];
        resultsPriorities = new float[capacity];
        Arrays.fill(resultsPriorities, 1f);
    }

    /**
     * @return the number of featured queues
     */
    public int capacity() {
        return mustFeature.length;
    }

    /**
     * Registers a feature at a given index.
     * @param index the index to append to
     * @param feature the feature to register
     */
    public void feature(int index, BiPredicate<VkQueueFamilyProperties, Boolean> feature) {
        mustFeature[index] = Objects.requireNonNull(feature);
    }

    /**
     * Retrieves the feature at a given index
     * @param index the index to lookup at
     * @return the feature
     */
    @SuppressWarnings("unchecked")
    public BiPredicate<VkQueueFamilyProperties, Boolean> feature(int index) {
        return Objects.requireNonNull((BiPredicate<VkQueueFamilyProperties, Boolean>) mustFeature[index]);
    }

    /**
     * Appends a queue at a given index
     * @param index the index to append to
     * @param queue the queue to append
     */
    public void result(int index, VkQueue queue) {
        results[index] = queue;
    }

    /**
     * Retrieves the resulting queue at a given index
     * @param index the index to lookup at
     * @return the resulting queue
     */
    public VkQueue result(int index) {
        return results[index];
    }

    /**
     * Sets the queue family index for the queue at a given index
     * @param index the index to append to
     * @param familyIndex the queue family to append
     */
    public void family_index(int index, int familyIndex) {
        queueFamiliesIndices[index] = familyIndex;
    }

    /**
     * Retrieves the queue family index for the queue at a given index
     * @param index the index to lookup at
     * @return the queue family index
     */
    public int family_index(int index) {
        return queueFamiliesIndices[index];
    }

    /**
     * @return an array of distinct queue family indices
     */
    public int[] uniqueFamilyIndices() {
        return IntStream.of(queueFamiliesIndices).distinct().toArray();
    }
    /**
     * Sets the priority for the queue at a given index
     * @param index the index to append to
     * @param priority the priority to be set
     */
    public void priority(int index, float priority) {
        resultsPriorities[index] = priority;
    }

    /**
     * Retrieves the priority of the queue at a given index
     * @param index the index to lookup at
     * @return the priority
     */
    public float priority(int index) {
        return resultsPriorities[index];
    }

    public XeQueues immutablecopy() {
        return new XeQueues(
                results,
                queueFamiliesIndices
        );
    }

    @Override
    public Iterator<BiPredicate<VkQueueFamilyProperties, Boolean>> iterator() {
        int len = mustFeature.length;
        List<BiPredicate<VkQueueFamilyProperties, Boolean>> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            list.add(feature(i));
        return list.iterator();
    }

    @Override
    public String toString() {
        return "QueueFeatures: [" + Arrays.toString(mustFeature) + "\n; " + Arrays.toString(results) + ']';
    }


}
