package com.sillyrilly.util.d3;

public class Const {
    // Набір символів від темного до світлого
    public static final String SHADES =
        " .'`^\",:;Il!i><~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkha*#MW&8%B@$";

    // Тривіальний куб
    public final static V3[] CUBE_VERTS = {
        new V3(-1, -1, -1), new V3(1, -1, -1),
        new V3(1, 1, -1), new V3(-1, 1, -1),
        new V3(-1, -1, 1), new V3(1, -1, 1),
        new V3(1, 1, 1), new V3(-1, 1, 1)
    };

    // Вершини куба
    public static final int[][] CUBE_FACES = {
        {0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4},
        {2, 3, 7, 6}, {1, 2, 6, 5}, {0, 3, 7, 4}
    };

}
