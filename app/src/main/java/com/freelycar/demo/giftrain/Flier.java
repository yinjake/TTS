package com.freelycar.demo.giftrain;

/**
 * 飞行物(设计待完善，需要考虑到某些飞行物可以转换（比如红包boom会变成爆炸物，也可能变成礼包）。
 */

@Deprecated
public interface Flier {

    int nextX(int dx);

    int nextY(int dy);

    /*判断某个点是否在区域内*/
    boolean isInArea(int x, int y);

    int getImageRes();

    boolean isClickable();

    int getType();

    int addTypeIndex(int addIndex);
}