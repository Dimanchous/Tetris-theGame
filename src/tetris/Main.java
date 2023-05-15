package tetris;

import java.awt.*;
import java.awt.event.*;
import static java.lang.Math.*;
import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.util.*;
import javax.swing.*;
import static tetris.Config.*;
import static tetris.Scoreboard.printScore;

public class Main extends JPanel implements Runnable
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> // cоздание основного потока
        {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("BETA Tetris");
            f.setResizable(false);
            f.add(new Main(), BorderLayout.CENTER);
            f.pack(); // открыть окно в выстроеном размере, в развернутом виде
            f.setLocationRelativeTo(null); // сбрасывает стандартное положение
            f.setVisible(true); //развертнутый вид
        });
    }

    enum Dir
    {
        right(1, 0), down(0, 1), left(-1, 0);

        Dir(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        final int x, y;
    };

    public static final int EMPTY = -1; //пустая ячейка
    public static final int BORDER = -2; //граница игрового поля

    Shape fallingShape;
    Shape nextShape;

    // позиция фигуры при падении
    int fallingShapeRow;
    int fallingShapeCol;

    final int[][] grid = new int[nRows][nCols];

    Thread fallingThread;
    final Scoreboard scoreboard = new Scoreboard();
    static final Random rand = new Random();

    public Main()
    {
        setPreferredSize(dim);
        setBackground(bgColor);
        setFocusable(true);

        initGrid();
        selectShape();

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if (scoreboard.isGameOver())
                {
                    startNewGame();
                    repaint();
                }
            }
        });

        addKeyListener(new KeyAdapter()
        {
            boolean fastDown;

            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_F2)
                {
                    try {
                        JOptionPane.showMessageDialog(new JFrame("HScore"),printScore());
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_F1)
                    JOptionPane.showMessageDialog(new JFrame("HScore"),"Управление фигурой: [Стрелочки] твоей любимой клавиатуры, [Пробел] - Приземлить самую лучшую фигуру\n" + "Посмотреть таблицу рекордов: Кнопочка [F2] твоей любимой клавиатуры \n");

                if (scoreboard.isGameOver())
                    return;

                switch (e.getKeyCode())
                {

                    case KeyEvent.VK_UP:
                        if (canRotate(fallingShape))
                            rotate(fallingShape);
                        break;

                    case KeyEvent.VK_LEFT:
                        if (canMove(fallingShape, Dir.left))
                            move(Dir.left);
                        break;

                    case KeyEvent.VK_RIGHT:
                        if (canMove(fallingShape, Dir.right))
                            move(Dir.right);
                        break;

                    case KeyEvent.VK_SPACE:
                        if (!fastDown)
                        {
                            fastDown = true;
                            while (canMove(fallingShape, Dir.down))
                            {
                                move(Dir.down);
                                repaint();
                            }
                            shapeHasLanded();
                        }
                        break;

                    case KeyEvent.VK_F1:
                        JOptionPane.showMessageDialog(new JFrame("HScore"),"Управление: [Стрелочки] твоей любимой клавиатуры\n" + "Посмотреть таблицу рекордов: Кнопочка [F2] твоей любимой клавиатуры \n");


                }
                repaint();
            }
            public void keyReleased(KeyEvent e)
            {
                fastDown = false;
            }
        });
    }

    void selectShape()
    {
        fallingShapeRow = 1;
        fallingShapeCol = 5;
        fallingShape = nextShape;
        Shape[] shapes = Shape.values();
        nextShape = shapes[rand.nextInt(shapes.length)];
        if (fallingShape != null)
            fallingShape.reset();
    }

    void startNewGame()
    {
        stop();
        initGrid();
        selectShape();
        scoreboard.reset();
        (fallingThread = new Thread(this)).start();
    }

    void stop()
    {
        if (fallingThread != null)
        {
            Thread tmp = fallingThread;
            fallingThread = null;
            tmp.interrupt();
        }
    }

    void initGrid() //иницилизируем сетку
    {
        for (int r = 0; r < nRows; r++)
        {
            Arrays.fill(grid[r], EMPTY);
            for (int c = 0; c < nCols; c++)
            {
                if (c == 0 || c == nCols - 1 || r == nRows - 1)
                    grid[r][c] = BORDER;
            }
        }
    }

    public void run()
    {

        while (Thread.currentThread() == fallingThread)
        {

            try
            {
                Thread.sleep(scoreboard.getSpeed());
            }
            catch (InterruptedException e)
            {
                return;
            }

            if (!scoreboard.isGameOver())
            {
                if (canMove(fallingShape, Dir.down))
                {
                    move(Dir.down);
                }
                else
                {
                    shapeHasLanded();
                }
                repaint();
            }


        }
    }

    boolean canRotate(Shape s)
    {
        if (s == Shape.SQ)
            return false;

        int[][] pos = new int[4][2];
        for (int i = 0; i < pos.length; i++)
        {
            pos[i] = s.pos[i].clone();
        }

        for (int[] row : pos)
        {
            int tmp = row[0];
            row[0] = row[1];
            row[1] = -tmp;
        }

        for (int[] p : pos)
        {
            int newCol = fallingShapeCol + p[0];
            int newRow = fallingShapeRow + p[1];
            if (grid[newRow][newCol] != EMPTY)
            {
                return false;
            }
        }
        return true;
    }

    void rotate(Shape s)
    {
        if (s == Shape.SQ)
            return;

        for (int[] row : s.pos)
        {
            int tmp = row[0];
            row[0] = row[1];
            row[1] = -tmp;
        }
    }

    void move(Dir dir)
    {
        fallingShapeRow += dir.y;
        fallingShapeCol += dir.x;
    }

    boolean canMove(Shape s, Dir dir)
    {
        for (int[] p : s.pos)
        {
            int newCol = fallingShapeCol + dir.x + p[0];
            int newRow = fallingShapeRow + dir.y + p[1];
            if (grid[newRow][newCol] != EMPTY)
                return false;
        }
        return true;
    }

    void shapeHasLanded()
    {
        addShape(fallingShape);
        if (fallingShapeRow < 2)
        {
            scoreboard.setGameOver();
            scoreboard.setTopscore();
            stop();
        }
        else
        {
            scoreboard.addLines(removeLines());
        }
        selectShape();
    }

    int removeLines()
    {
        int count = 0;
        for (int r = 0; r < nRows - 1; r++)
        {
            for (int c = 1; c < nCols - 1; c++)
            {
                if (grid[r][c] == EMPTY)
                    break;
                if (c == nCols - 2)
                {
                    count++;
                    removeLine(r);
                }
            }
        }
        return count;
    }

    void removeLine(int line)
    {
        for (int c = 0; c < nCols; c++)
            grid[line][c] = EMPTY;

        for (int c = 0; c < nCols; c++)
        {
            for (int r = line; r > 0; r--)
                grid[r][c] = grid[r - 1][c];
        }
    }

    void addShape(Shape s)
    {
        for (int[] p : s.pos)
            grid[fallingShapeRow + p[1]][fallingShapeCol + p[0]] = s.ordinal();
    }





    void drawStartScreen(Graphics2D g)
    {
        g.setFont(mainFont);
        g.setColor(titlebgColor);
        g.fill(titleRect);
        g.fill(clickRect);
        g.setColor(textColor);
        g.drawString("Tetris", titleX, titleY);
        g.setFont(smallFont);
        g.drawString("PLAY", clickX, clickY);
    }

    void drawSquare(Graphics2D g, int colorIndex, int r, int c)
    {
        g.setColor(colors[colorIndex]);
        g.fillRect(leftMargin + c * blockSize, topMargin + r * blockSize, blockSize, blockSize);
        g.setStroke(smallStroke);
        g.setColor(squareBorder);
        g.drawRect(leftMargin + c * blockSize, topMargin + r * blockSize, blockSize, blockSize);
    }

    void drawUI(Graphics2D g)
    {
        // Цвет сетки фона
        g.setColor(gridColor);
        g.fill(gridRect);

        // при падении фигуры перерисовывает ее снова, но внизу
        for (int r = 0; r < nRows; r++)
        {
            for (int c = 0; c < nCols; c++)
            {
                int idx = grid[r][c];
                if (idx > EMPTY)
                    drawSquare(g, idx, r, c);
            }
        }
        // Границы сетки
        g.setStroke(largeStroke);
        g.setColor(gridBorderColor);
        g.draw(gridRect);
        g.draw(previewRect);
        // таблица рекордов
        int x = scoreX;
        int y = scoreY;
        g.setColor(textColor);
        g.setFont(smallFont);
        g.drawString(format("hiscore  %6d", scoreboard.getTopscore()), x + 25, y - 35);
        g.drawString(format("lines    %6d", scoreboard.getLines()), x + 25, y + 200);
        g.drawString(format("score    %6d", scoreboard.getScore()), x + 25, y + 230);
        // окно следующей фигуры
        int minX = 5, minY = 5, maxX = 0, maxY = 0;
        for (int[] p : nextShape.pos) {
            minX = min(minX, p[0]);
            minY = min(minY, p[1]);
            maxX = max(maxX, p[0]);
            maxY = max(maxY, p[1]);
        }
        double cx = previewCenterX - ((minX + maxX + 1) / 2.0 * blockSize);
        double cy = previewCenterY - ((minY + maxY + 1) / 2.0 * blockSize);

        g.translate(cx, cy);
        for (int[] p : nextShape.shape)
            drawSquare(g, nextShape.ordinal(), p[1], p[0]);
        g.translate(-cx, -cy);
    }

    void drawFallingShape(Graphics2D g)
    {
        int idx = fallingShape.ordinal();
        for (int[] p : fallingShape.pos)
            drawSquare(g, idx, fallingShapeRow + p[1], fallingShapeCol + p[0]);
    }

    public void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //сглаживание
        drawUI(g);

        if (scoreboard.isGameOver())
        {
            drawStartScreen(g);
        }
        else
        {
            drawFallingShape(g);
        }
    }


}
