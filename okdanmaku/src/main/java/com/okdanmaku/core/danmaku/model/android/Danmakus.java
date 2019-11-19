package com.okdanmaku.core.danmaku.model.android;

import com.okdanmaku.core.danmaku.model.Danmaku;
import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.IDanmakus;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by yangya on 2019-11-19.
 */
public class Danmakus implements IDanmakus {

    private Set<DanmakuBase> items;
    private DanmakuBase startItem, endItem;
    private Danmakus subItems;

    public Danmakus() {
        setItems(new TreeSet<>(new DanmakusComparator()));
    }

    public Danmakus(Set<DanmakuBase> items) {
        setItems(items);
    }

    private void setItems(Set<DanmakuBase> items) {
        this.items = items;
    }

    @Override
    public void addItem(DanmakuBase item) {
        items.add(item);
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
        if (startItem != null) {
            startItem.setTime(startTime);
            endItem.setTime(endTime);
            subItems.setItems(((SortedSet<DanmakuBase>) items).subSet(startItem, endItem));
            return subItems;
        }

        return null;
    }

    private DanmakuBase createItem(String text) {
        return new Danmaku(text);
    }

    private class DanmakusComparator implements Comparator<DanmakuBase> {

        @Override
        public int compare(DanmakuBase o1, DanmakuBase o2) {
            long delta = o1.time - o2.time;
            if (delta > 0) {
                // 说明 o1.time 大于 o2，意味着 o1 比 o2 晚显示，所以 o1 < o2
                return -1;
            } else if (delta < 0) {
                return 1;
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
}
