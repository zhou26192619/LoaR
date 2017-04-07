package com.loar.accessibility;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * 辅助功能帮助
 * Created by Justsy on 2016/4/21.
 */
public class AccessHelper {

    /**
     * 点击对象
     */
    public static class ClickOb {
        private static HashMap<Long, ClickOb> clickObs = new HashMap();
        long stayTime = 200;

        private final AccessibilityNodeInfo node;
        long clickTime = System.currentTimeMillis();

        private long nodeId;

        public ClickOb(AccessibilityNodeInfo node) {
            this.node = node;
            try {
                Class<?> c = Class.forName(AccessibilityNodeInfo.class.getCanonicalName());
                Method m = c.getMethod("getSourceNodeId");
                nodeId = (Long) m.invoke(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ClickOb(AccessibilityNodeInfo node, long stayTime) {
            this(node);
            this.stayTime = stayTime;
        }

        public void setStayTime(long stayTime) {
            this.stayTime = stayTime;
        }

        public void excute() {
            if (clickObs.containsKey(nodeId)) {
                if ((System.currentTimeMillis() - clickObs.get(nodeId).clickTime) > stayTime) {
                    clickObs.remove(nodeId);
                }
            } else {
                //防止内存泄漏
                if (clickObs.size() > 100) {
                    clickObs.clear();
                }
                clickObs.put(nodeId, this);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            if (node.isClickable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                            Thread.sleep(stayTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            clickObs.remove(nodeId);
                        }
                    }
                }.start();
            }
        }
    }

    public final static HashMap<Long, Thread> scrollThreads = new HashMap<>();

    /**
     * 来回滚动
     *
     * @param l
     */
    public void scrollThrough(final AccessibilityNodeInfo l, final long interval, final long duration) {
        if (l == null) {
            return;
        }
        if (scrollThreads.size() > 100) {
            scrollThreads.clear();
        }
        if (!scrollThreads.containsKey(getNodeID(l))) {
            Thread thread = new Thread(new Runnable() {
                int scroll = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
                long lastTime = System.currentTimeMillis();
                long startTime = System.currentTimeMillis();

                @Override
                public void run() {
                    while (true) {
                        if (System.currentTimeMillis() - startTime > duration) {
                            break;
                        }
                        if (System.currentTimeMillis() - lastTime > interval) {
                            if (scroll == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
                                scroll = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
                            } else {
                                scroll = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
                            }
                            l.performAction(scroll);
                            lastTime = System.currentTimeMillis();
                        } else {
                            l.performAction(scroll);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    scrollThreads.remove(getNodeID(l));
                }
            });
            thread.start();
            scrollThreads.put(getNodeID(l), thread);
        }

    }


    public void removeAllScrollThread() {
        scrollThreads.clear();
    }

    public long getNodeID(AccessibilityNodeInfo node) {
        try {
            Class<?> c = Class.forName("android.view.accessibility.AccessibilityNodeInfo");
            Method m = c.getMethod("getSourceNodeId");
            return (Long) m.invoke(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param nodeInfo
     * @param stayTime 多少时间内无法再次点击
     * @return
     */
    public boolean executeClick(AccessibilityNodeInfo nodeInfo, long stayTime) {
        AccessibilityNodeInfo clickNode = nodeInfo;
        while (clickNode != null) {
            if (clickNode.isClickable()) {
                new ClickOb(clickNode, stayTime).excute();
                return true;
            }
            clickNode = clickNode.getParent();
        }
        return false;
    }

    public boolean executeClick(AccessibilityNodeInfo nodeInfo) {
        return executeClick(nodeInfo, 200);
    }

    public boolean executeClick(List<AccessibilityNodeInfo> nodeInfos, long stayTime) {
        boolean result = false;
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            for (AccessibilityNodeInfo n : nodeInfos) {
                result = executeClick(n, stayTime);
            }
        }
        return result;
    }

    public boolean executeClick(List<AccessibilityNodeInfo> nodeInfos) {
        return executeClick(nodeInfos, 200);
    }

    /**
     * 延迟点击
     *
     * @param nodeInfo
     * @param delay
     */
    public void executeClickDelay(final AccessibilityNodeInfo nodeInfo, final long delay) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(delay);
                    if (nodeInfo != null) {
                        executeClick(nodeInfo);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void executeClickDelay(List<AccessibilityNodeInfo> nodeInfos, long delay) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            for (AccessibilityNodeInfo n : nodeInfos) {
                executeClickDelay(n, delay);
            }
        }
    }

    public void executeEnable(AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo clickNode = nodeInfo;
        while (clickNode != null) {
            if (clickNode.isClickable()) {
                clickNode.setEnabled(false);
                return;
            }
            clickNode = clickNode.getParent();
        }
    }

    public void executeEnable(List<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            for (AccessibilityNodeInfo n : nodeInfos) {
                executeEnable(n);
            }
        }
    }

    /**
     * 通过类名向上查找nodeInfo
     *
     * @param source
     * @param className
     * @return
     */
    public AccessibilityNodeInfo getNodeInfoByClassName(AccessibilityNodeInfo source, String className) {
        AccessibilityNodeInfo current = source;
        while (true) {
            if (current.getClassName().equals(className)) {
                return current;
            }
            current = current.getParent();
            if (current == null) {
                return null;
            }
        }
    }

    /**
     * 是否包含关键字
     *
     * @param nodeList 要比较的节点集
     * @param keys     关键字
     * @return
     */
    public boolean isEqualsKeys(final List<AccessibilityNodeInfo> nodeList, final List<String> keys) {
        for (String s : keys) {
            boolean isFind = false;
            for (AccessibilityNodeInfo l : nodeList) {
                if (s.equals(l.getText())) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含关键字
     *
     * @param nodeList 要比较的节点集
     * @param keys     关键字
     * @return
     */
    public boolean isContainsKeys(final List<AccessibilityNodeInfo> nodeList, final List<String> keys) {
        for (String s : keys) {
            boolean isFind = false;
            for (AccessibilityNodeInfo l : nodeList) {
                if (s.contains(l.getText())) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                return false;
            }
        }
        return true;
    }
}
