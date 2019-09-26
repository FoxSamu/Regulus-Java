package test;

import javax.swing.JFrame;

public final class Fullscreen {

    private Fullscreen() {
    }

    public static void makeFullscreenable( JFrame frame ) {
        frame.getRootPane().putClientProperty( "apple.awt.fullscreenable", true );
    }

}
