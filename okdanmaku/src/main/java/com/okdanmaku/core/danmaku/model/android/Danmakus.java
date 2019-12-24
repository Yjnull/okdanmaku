package com.okdanmaku.core.danmaku.model.android;

import com.okdanmaku.core.danmaku.model.BaseDanmaku;
import com.okdanmaku.core.danmaku.model.Danmaku;
import com.okdanmaku.core.danmaku.model.IDanmakus;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by yangya on 2019-11-19.
 */
public class Danmakus implements IDanmakus {
    public static final int SORT_BY_TIME = 0;
    public static final int SORT_BY_YPOS = 1;

    public Set<BaseDanmaku> items;
    private BaseDanmaku startItem, endItem;
    private Danmakus subItems;

    public Danmakus() {
        this(SORT_BY_TIME);
    }

    public Danmakus(int sortType) {
        Comparator<BaseDanmaku> comparator = null;
        if (sortType == SORT_BY_TIME) {
            comparator = new TimeComparator();
        } else if (sortType == SORT_BY_YPOS) {
            comparator = new YPosComparator();
        }
        items = new TreeSet<>(comparator);
    }

    public Danmakus(Set<BaseDanmaku> items) {
        setItems(items);
    }

    private void setItems(Set<BaseDanmaku> items) {
        if (items != null) {
            for (BaseDanmaku item : items) {
                if (item.isOutside()) {
                    item.setVisibility(false);
                } else {
                    break;
                }
            }
        }
        this.items = items;
    }

    @Override
    public void addItem(BaseDanmaku item) {
        if (items != null) {
            items.add(item);
        }
    }

    @Override
    public void removeItem(BaseDanmaku item) {
        if (item.isOutside()) {
            item.setVisibility(false);
        }
        if (items != null) {
            items.remove(item);
        }
    }

    @Override
    public IDanmakus sub(long startTime, long endTime) {
        if (subItems == null) {
            subItems = new Danmakus();
        }
        if (startItem == null) {
            startItem = createItem("start");
        }
        if (endItem == null) {
            endItem = createItem("end");
        }

        startItem.setTime(startTime);
        endItem.setTime(endTime);
        subItems.setItems(((SortedSet<BaseDanmaku>) items).subSet(startItem, endItem));
        return subItems;
    }

    private BaseDanmaku createItem(String text) {
        return new Danmaku(text);
    }

    private class TimeComparator implements Comparator<BaseDanmaku> {

        @Override
        public int compare(BaseDanmaku o1, BaseDanmaku o2) {
            long delta = o1.time - o2.time;
            if (delta > 0) {
                // 说明 o1.time 大于 o2.time，意味着 o1 比 o2 晚显示
                return 1;
            } else if (delta < 0) {
                return -1;
            }

            if (o1.text == null && o2.text == null) {
                return 0;
            }

            if (o1.text == null) {
                return -1;
            }

            if (o2.text == null) {
                return 1;
            }

            return o1.text.compareTo(o2.text);
        }
    }

    private class YPosComparator implements Comparator<BaseDanmaku> {

        @Override
        public int compare(BaseDanmaku o1, BaseDanmaku o2) {
            int result = Float.compare(o1.getTop(), o2.getTop());
            if (result != 0) {
                return result;
            }
            long delta = o1.time - o2.time;
            if (delta > 0) {
                result = 1;
            } else if (delta < 0) {
                result = -1;
            }

            return result;
        }
    }

}
