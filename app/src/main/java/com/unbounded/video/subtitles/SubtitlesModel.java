package com.unbounded.video.subtitles;

import java.io.Serializable;

/**
 * @Auther dwt
 * @date 2017/11/22.
 * @descraption 字幕数据结构
 */

public class SubtitlesModel implements Serializable
{
    /**
     * 当前节点
     */
    public int node;

    /**
     * 开始显示的时间
     */
    public int star;

    /**
     * 结束显示的时间
     */
    public int end;


    /**
     * 显示的内容《英文》
     */
    public String contextE;

    /**
     * 显示的内容《中文》
     */
    public String contextC;

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getContextE() {
        return contextE;
    }

    public void setContextE(String contextE) {
        this.contextE = contextE;
    }

    public String getContextC() {
        return contextC;
    }

    public void setContextC(String contextC) {
        this.contextC = contextC;
    }
}
