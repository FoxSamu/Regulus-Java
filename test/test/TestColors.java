package test;

import java.awt.*;

public final class TestColors {
    private TestColors() {
        throw new UnsupportedOperationException( "No TestColors instances for you!" );
    }

    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;

    public static final Color BACKGROUND = new Color( 0x111111, false );
    public static final Color STATIC = new Color( 0x303045, false );
    public static final Color ROPE = new Color( 0x696D82, false );

    public static final Color RED = new Color( 0xd12110, false );
    public static final Color ORANGE = new Color( 0xf18120, false );
    public static final Color YELLOW = new Color( 0xf1b120, false );
    public static final Color LIME = new Color( 0xA1E03C, false );
    public static final Color GREEN = new Color( 0x10b121, false );
    public static final Color DARK_GREEN = new Color( 0x107131, false );
    public static final Color DARK_BLUE = new Color( 0x1020f1, false );
    public static final Color BLUE = new Color( 0x1078f1, false );
    public static final Color CYAN = new Color( 0x10BBEF, false );
    public static final Color PURPLE = new Color( 0x6710f1, false );
    public static final Color MAGENTA = new Color( 0xE413F7, false );

    private static final Color[] COLORS = {
        RED, ORANGE, YELLOW, LIME, GREEN, DARK_GREEN, DARK_BLUE, BLUE, CYAN, PURPLE, MAGENTA
    };

    public static Color withAlpha( Color color, double alpha ) {
        return new Color( color.getRed(), color.getGreen(), color.getBlue(), (int) ( color.getAlpha() * alpha ) );
    }

    public static Color randomColor() {
        int d = (int) ( Math.random() * COLORS.length );
        return COLORS[ d ];
    }
}
