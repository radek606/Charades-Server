package com.ick.kalambury.messages;

import com.ick.kalambury.entities.GameDataProtos;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DrawableMessage {

    private GameDataProtos.Drawable.Tool tool;
    private int strokeWidth;
    private int color;
    private int width;
    private int height;
    private List<Point> points;

    private DrawableMessage(Builder builder) {
        tool = builder.tool;
        strokeWidth = builder.strokeWidth;
        color = builder.color;
        width = builder.width;
        height = builder.height;
        points = builder.points;
    }

    public static Builder newBuilder(GameDataProtos.Drawable.Tool val) {
        return new Builder(val);
    }

    public static DrawableMessage fromProto(GameDataProtos.Drawable line) {
        return newBuilder(line.getTool())
                .setStrokeWidth(line.getStrokeWidth())
                .setColor(line.getColor())
                .setWidth(line.getWidth())
                .setHeight(line.getHeight())
                .setPoints(line.getPointsList().stream()
                        .map(point -> new Point(point.getX(), point.getY()))
                        .collect(Collectors.toList()))
                .build();
    }

    public GameDataProtos.Drawable.Tool getTool() {
        return tool;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Point> getPoints() {
        return points;
    }

    public GameDataProtos.Drawable toProto() {
        return GameDataProtos.Drawable.newBuilder()
                .setTool(tool)
                .setStrokeWidth(strokeWidth)
                .setColor(color)
                .setWidth(width)
                .setHeight(height)
                .addAllPoints(points.stream()
                        .map(point -> GameDataProtos.Drawable.Point.newBuilder().setX(point.x).setY(point.y).build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static final class Builder {
        private GameDataProtos.Drawable.Tool tool;
        private int strokeWidth;
        private int color;
        private int width;
        private int height;
        private List<Point> points;

        private Builder(GameDataProtos.Drawable.Tool val) {
            tool = val;
        }

        public Builder setStrokeWidth(int val) {
            strokeWidth = val;
            return this;
        }

        public Builder setColor(int val) {
            color = val;
            return this;
        }

        public Builder setWidth(int val) {
            width = val;
            return this;
        }

        public Builder setHeight(int val) {
            height = val;
            return this;
        }

        public Builder setPoints(List<Point> val) {
            points = val;
            return this;
        }

        public DrawableMessage build() {
            return new DrawableMessage(this);
        }
    }
}
