package tetris;

import java.awt.*;

final class Config
{
    final static Color[] colors = {new Color(0x13CD00), new Color(0x1064B5), new Color(0x73E6B9), new Color(0xFDF800), new Color(0xFFFFFF), new Color(0x79229C), Color.red};
    final static Font mainFont = new Font("VALORANT", Font.BOLD, 48);
    final static Font smallFont = mainFont.deriveFont(Font.BOLD, 18);
    final static Dimension dim = new Dimension(960, 960);
    final static Rectangle gridRect = new Rectangle(70, 70, 488, 785);
    final static Rectangle previewRect = new Rectangle(580, 70, 300, 300);
    final static Rectangle titleRect = new Rectangle(150, 127, 330, 150);
    final static Rectangle clickRect = new Rectangle(73, 562, 430, 60);
    final static int blockSize = 30;
    final static int nRows = 27;
    final static int nCols = 18;
    final static int topMargin = 72;
    final static int leftMargin = 45;
    final static int scoreX = 600;
    final static int scoreY = 495;
    final static int titleX = 237;
    final static int titleY = 225;
    final static int clickX = 200;
    final static int clickY = 600;
    final static int previewCenterX = 690;
    final static int previewCenterY = 145;
    final static Stroke largeStroke = new BasicStroke(5);
    final static Stroke smallStroke = new BasicStroke(2);

    final static Color squareBorder = new Color(0x7B7B27);
    final static Color titlebgColor = Color.black;
    final static Color textColor = new Color(0xFDF800);
    final static Color bgColor = new Color(0x1D0010);
    final static Color gridColor = new Color(0x313131);
    final static Color gridBorderColor = new Color(0x7B7B27);
}

enum Shape
{
    Z(new int[][]{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}),
    S(new int[][]{{0, -1}, {0, 0}, {1, 0}, {1, 1}}),
    I(new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, 2}}),
    T(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, 1}}),
    SQ(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}),
    L(new int[][]{{-1, -1}, {0, -1}, {0, 0}, {0, 1}}),
    J(new int[][]{{1, -1}, {0, -1}, {0, 0}, {0, 1}});

    private Shape(int[][] shape)
    {
        this.shape = shape;
        pos = new int[4][2];
        reset();
    }

    void reset()
    {
        for (int i = 0; i < pos.length; i++)
        {
            pos[i] = shape[i].clone();
        }
    }

    final int[][] pos, shape;
}