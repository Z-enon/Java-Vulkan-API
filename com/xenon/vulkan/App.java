package com.xenon.vulkan;

/**
 * @author Zenon
 */
public interface App extends Disposable, Debuggable{

    void init();
    void loop();

    default void run() {
        init();
        try {
            loop();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            dispose();
        }
    }


}
