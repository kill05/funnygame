package com.github.kill05.funnygame;

public final class Unicode {

    private Unicode() {
        // Utility class
    }

    // ============================================================
    // Basic box-drawing
    // ============================================================

    public static final String HORIZONTAL = "─";
    public static final String VERTICAL = "│";

    public static final String TOP_LEFT = "┌";
    public static final String TOP_RIGHT = "┐";
    public static final String BOTTOM_LEFT = "└";
    public static final String BOTTOM_RIGHT = "┘";

    public static final String T_LEFT = "├";
    public static final String T_RIGHT = "┤";
    public static final String T_TOP = "┬";
    public static final String T_BOTTOM = "┴";

    public static final String CROSS = "┼";


    // ============================================================
    // Thick box-drawing
    // ============================================================

    public static final String THICK_HORIZONTAL = "━";
    public static final String THICK_VERTICAL = "┃";

    public static final String THICK_TOP_LEFT = "┏";
    public static final String THICK_TOP_RIGHT = "┓";
    public static final String THICK_BOTTOM_LEFT = "┗";
    public static final String THICK_BOTTOM_RIGHT = "┛";

    public static final String THICK_T_LEFT = "┣";
    public static final String THICK_T_RIGHT = "┫";
    public static final String THICK_T_TOP = "┳";
    public static final String THICK_T_BOTTOM = "┻";

    public static final String THICK_CROSS = "╋";


    // ============================================================
    // Double-line box-drawing
    // ============================================================

    public static final String DOUBLE_HORIZONTAL = "═";
    public static final String DOUBLE_VERTICAL = "║";

    public static final String DOUBLE_TOP_LEFT = "╔";
    public static final String DOUBLE_TOP_RIGHT = "╗";
    public static final String DOUBLE_BOTTOM_LEFT = "╚";
    public static final String DOUBLE_BOTTOM_RIGHT = "╝";

    public static final String DOUBLE_T_LEFT = "╠";
    public static final String DOUBLE_T_RIGHT = "╣";
    public static final String DOUBLE_T_TOP = "╦";
    public static final String DOUBLE_T_BOTTOM = "╩";

    public static final String DOUBLE_CROSS = "╬";


    // ============================================================
    // Rounded corners
    // ============================================================

    public static final String ROUND_TOP_LEFT = "╭";
    public static final String ROUND_TOP_RIGHT = "╮";
    public static final String ROUND_BOTTOM_LEFT = "╰";
    public static final String ROUND_BOTTOM_RIGHT = "╯";


    // ============================================================
    // Light / partial box-drawing
    // ============================================================

    public static final String LIGHT_LEFT = "╴";
    public static final String LIGHT_RIGHT = "╶";
    public static final String LIGHT_UP = "╵";
    public static final String LIGHT_DOWN = "╷";

    public static final String HEAVY_LEFT = "╸";
    public static final String HEAVY_RIGHT = "╺";
    public static final String HEAVY_UP = "╹";
    public static final String HEAVY_DOWN = "╻";


    // ============================================================
    // Mixed / special junctions
    // ============================================================

    public static final String MIXED_LEFT = "╞";
    public static final String MIXED_RIGHT = "╡";
    public static final String MIXED_TOP = "╤";
    public static final String MIXED_BOTTOM = "╧";
    public static final String MIXED_CROSS = "╪";


    // ============================================================
    // Block / shade characters
    // ============================================================

    public static final String FULL_BLOCK = "█";
    public static final String DARK_SHADE = "▓";
    public static final String MEDIUM_SHADE = "▒";
    public static final String LIGHT_SHADE = "░";

    public static final String UPPER_HALF = "▀";
    public static final String LOWER_HALF = "▄";
    public static final String LEFT_HALF = "▌";
    public static final String RIGHT_HALF = "▐";


    // ============================================================
    // Common geometric shapes
    // ============================================================

    public static final String SQUARE = "■";
    public static final String WHITE_SQUARE = "□";

    public static final String CIRCLE = "●";
    public static final String WHITE_CIRCLE = "○";

    public static final String DIAMOND = "◆";
    public static final String WHITE_DIAMOND = "◇";

    public static final String TRIANGLE_UP = "▲";
    public static final String TRIANGLE_DOWN = "▼";
    public static final String TRIANGLE_LEFT = "◀";
    public static final String TRIANGLE_RIGHT = "▶";


    // ============================================================
    // Arrows
    // ============================================================

    public static final String ARROW_UP = "↑";
    public static final String ARROW_DOWN = "↓";
    public static final String ARROW_LEFT = "←";
    public static final String ARROW_RIGHT = "→";

    public static final String DOUBLE_ARROW_UP = "⇑";
    public static final String DOUBLE_ARROW_DOWN = "⇓";
    public static final String DOUBLE_ARROW_LEFT = "⇐";
    public static final String DOUBLE_ARROW_RIGHT = "⇒";


    // ============================================================
    // Miscellaneous useful terminal characters
    // ============================================================

    public static final String BULLET = "•";
    public static final String DOT = "·";
    public static final String STAR = "★";
    public static final String WHITE_STAR = "☆";

    public static final String CHECK = "✓";
    public static final String CROSS_MARK = "✗";

    public static final String SPACE = " ";
}
