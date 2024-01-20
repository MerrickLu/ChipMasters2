import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    //dimensions of window
    public static final int GAME_WIDTH = 867;
    public static final int GAME_HEIGHT = 500;
    public static final Rectangle PANEL_BOUNDS = new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
    public boolean onMenu = true, onSettings = false, onGame = false, onPaused = false;
    public String beforeSettings = "";

    public final static String IMAGE_FOLDER_LOCATION = "images" + File.separator;

    public static Game gameInstance;
    public Menu menu = new Menu();
    public Settings settings = new Settings();
    public GameGUI game;
    public Paused paused = new Paused();
    //    private SoundPlayer background = new SoundPlayer("sounds/background.wav");
    public Thread gamePanelThread;
    public Thread gameThread;
    public Thread gameGUIThread;
    Image image;
    Graphics graphics;




    public GamePanel(){

        this.setFocusable(true); //make everything in this class appear on the screen
        this.addKeyListener(this); //start listening for keyboard input
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        //add the MousePressed method from the MouseAdapter - by doing this we can listen for mouse input. We do this differently from the KeyListener because MouseAdapter has SEVEN mandatory methods - we only need one of them, and we don't want to make 6 empty methods
        this.setLayout(null);
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));


//        background.setVolume(settings.getMusicVolume());
//        background.play();
        // make this class run at the same time as other classes
        gamePanelThread = new Thread(this);
        gamePanelThread.start();
    }

    //paint is a method in java.awt library that we are overriding. It is a special method - it is called automatically in the background in order to update what appears in the window. You NEVER call paint() yourself
    public void paint(Graphics g){
        //we are using "double buffering here" - if we draw images directly onto the screen, it takes time and the human eye can actually notice flashes of lag as each pixel on the screen is drawn one at a time. Instead, we are going to draw images OFF the screen, then simply move the image on screen as needed.
        image = createImage(GAME_WIDTH, GAME_HEIGHT); //draw off screen
        graphics = image.getGraphics();
        try {
            draw(graphics);//update the positions of everything on the screen
        } catch (Exception e){
            e.printStackTrace();
        }
        g.drawImage(image, 0, 0, this); //move the image on the screen

    }

    //call the draw methods in each class to update positions as things move
    public void draw(Graphics g) throws IOException, FontFormatException {
        if(onMenu) menu.draw(g);
        else if (onSettings) settings.draw(g);
        else if (onGame && game != null) {
            game.draw(g);
        }
        else if (onPaused) paused.draw(g);

    }

    //handles all collision detection and responds accordingly


    //run() method is what makes the game continue running without end. It calls other methods to move objects,  check for collision, and update the screen
    public void run(){
        //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen.
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        long now;

        while(true){ //this is the infinite game loop
            now = System.nanoTime();
            delta = delta + (now-lastTime)/ns;
            lastTime = now;

            //only move objects around and update screen if enough time has passed
            if(delta >= 1) {
                repaint();
                delta--;
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (onMenu) {
            setCursor(Cursor.getDefaultCursor()); // default pointer
            menu.mouseMoved(e);
        } else if (onSettings) {
            settings.mouseMoved(e);
            setCursor(settings.getCursor());
        } else if (onGame & game != null) {
            game.mouseMoved(e);
            setCursor(game.getCursor());
        } else if (onPaused) {
            paused.mouseMoved(e);
            setCursor(paused.getCursor());
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (onGame & game != null) {
            game.mouseDragged(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        String output;
        if (onMenu) {
            output = menu.mousePressed(e);
            if (output.equals("Settings")) {
                onMenu = false;
                onSettings = true;
                beforeSettings = "Menu";
            } else if (output.equals("Start Game")) {
                onMenu = false;
                onGame = true;
                gameThread = new Thread(gameInstance = new Game(5,10,settings.getStartingCash()));
                gameGUIThread = new Thread(game = new GameGUI());
                gameThread.start();
                gameGUIThread.start();
            } else if (output.equals("Training Mode")) {
                onMenu = false;
            }
        } else if (onSettings) {
            output = settings.mousePressed(e);
            if(output.equals("Back")) {
                onSettings = false;
                if (beforeSettings.equals("Menu")) {
                    onMenu = true;
                } else if(beforeSettings.equals("Paused")) {
                    onPaused= true;
                }
            }
        } else if (onGame) {
            output = game.mousePressed(e);
            if(output.equals("Escape")) {
                onGame = false;
                onPaused = true;
            }

        } else if (onPaused) {
            output = paused.mousePressed(e);
            if(output.equals("X")) {
                onPaused = false;
                onGame = true;
            } else if(output.equals("Menu")) {
                onPaused = false;
                onMenu = true;

            } else if(output.equals("Settings")) {
                onPaused = false;
                onSettings = true;
                beforeSettings = "Paused";
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (onSettings) {
            settings.mouseReleased(e);

//            background.setVolume(settings.getMusicVolume());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    //if a key is pressed, we'll send it over to the PlayerBall class for processing
    public void keyPressed(KeyEvent e){
        String returned;
        if (onMenu) {
            returned = menu.keyPressed(e);
            if (returned.equals("Settings")) {
                onMenu = false;
                onSettings = true;
                beforeSettings = "Menu";
            } else if(returned.equals("Start Game")) {
                onMenu = false;
                onGame = true;
                gameThread = new Thread(gameInstance = new Game(5,10,settings.getStartingCash()));
                gameGUIThread = new Thread(game = new GameGUI());
                gameThread.start();
                gameGUIThread.start();
            } else if(returned.equals("Training Mode")) {

            }
        } else if (onGame) {
            game.keyPressed(e);
        }
    }

    //if a key is released, we'll send it over to the PlayerBall class for processing
    public void keyReleased(KeyEvent e){
    }

    //left empty because we don't need it; must be here because it is required to be overridded by the KeyListener interface
    public void keyTyped(KeyEvent e){

    }
}