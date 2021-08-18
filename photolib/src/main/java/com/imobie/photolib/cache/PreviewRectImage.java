package com.imobie.photolib.cache;

public class PreviewRectImage {
    private int[] rect;
    private int position;

    public PreviewRectImage(int position,int[] rect) {
        this.position = position;
        this.rect = rect;

    }

    public PreviewRectImage() {
    }

    public int[] getRect() {
        return rect;
    }

    public void setRect(int[] rect) {
        this.rect = rect;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
