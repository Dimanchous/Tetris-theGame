package tetris;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

class Scoreboard
{
    static final int MAXLEVEL = 5;
    private int level;
    private int lines;
    private int score;
    private int topscore;
    private boolean gameOver = true;


    int getSpeed()
    {
        switch (level)
        {
            case 0:
                return 300;
            case 1:
                return 150;
            case 2:
                return 100;
            case 3:
                return 70;
            case 4:
                return 50;

            default:
                return 100;
        }
    }

    void addScore(int sc)
    {
        score += sc;
    }

    void addLines(int line)
    {

        switch (line)
        {
            case 1:
                addScore(100);
                break;
            case 2:
                addScore(200);
                break;
            case 3:
                addScore(300);
                break;
            default:
                return;
        }

        lines += line;
        if (lines > 1)
            addLevel();
    }



    void addLevel()
    {
        if (level < MAXLEVEL)
            level++;
    }

    int getLines()
    {
        return lines;
    }

    int getScore()
    {
        return score;
    }

    void reset()
    {
        setTopscore();
        level = lines = score = 0;
        gameOver = false;
    }

    void setGameOver()
    {
        gameOver = true;
        try {
            saveScore(score);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            JOptionPane.showMessageDialog(new JFrame("HScore"),printScore());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static long[] loadScore() throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File("hscore.txt"));
        long [] scores = new long [5];
        int i = 0;
        while(scanner.hasNextLong())
        {
            scores[i++] = scanner.nextLong();
        }
        return scores;
    }
    public static String printScore() throws FileNotFoundException
    {
        long [] scores = loadScore();
        return  "Рекорды:\n1) "+scores[0]+"\n"+"2) "+scores[1]+"\n"+"3) "+scores[2]+"\n"+"4) "+scores[3]+"\n"+"5) "+scores[4];
    }

    public static void saveScore(long score) throws FileNotFoundException, UnsupportedEncodingException
    {
        long [] scores = loadScore();
        scores = Arrays.copyOf(scores, scores.length + 1);
        scores[scores.length - 1] = score;
        Arrays.sort(scores);
        PrintWriter writer = new PrintWriter("hscore.txt", "UTF-8");
        for(int i=scores.length-1;i>0;i--)
        {
            writer.println(scores[i]);
        }
        writer.close();
    }

    boolean isGameOver()
    {
        return gameOver;
    }

    void setTopscore()
    {
        if (score > topscore)
            topscore = score;
    }

    int getTopscore()
    {
        return topscore;
    }
}
