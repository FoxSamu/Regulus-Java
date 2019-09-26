package test;

import javax.swing.*;

public interface TestCase {
    String getName();
    void draw( CanvasContext ctx, double mouseX, double mouseY );

    JMenu setupMenu();

    default void tick( double x, double y ) {
    }

    default void pause() {
    }

    default void play() {
    }

    default void init() {
    }

    default void mouseDown( double x, double y ) {
    }

    default void mouseUp( double x, double y ) {
    }

    default void mouseMove( double x, double y ) {
    }

    default void mouseDrag( double x, double y ) {
    }

    default void keyPress( double x, double y, int keyCode, int modifiers ) {
    }

    default void keyRelease( double x, double y, int keyCode, int modifiers ) {
    }
}
