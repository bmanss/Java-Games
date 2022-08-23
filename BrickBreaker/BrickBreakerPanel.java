package BrickBreaker;
import javax.swing.*;
import Pong.GameObject;
import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.event.*;


public class BrickBreakerPanel extends JPanel implements Runnable {
    
    final int PANEL_WIDTH = 1250;
    final int PANEL_HEIGHT = 850;

    final int PADDLE_WIDTH = 150;
    final int PADDLE_HEIGHT = 30;

    final int PLAYER_PADDLE_WIDTH = 200;
    final int PLAYER_PADDLE_HEIGHT = 30;

    final int BALL_HEIGHT = 30;
    final int BALL_WIDTH = 30;

    final int BRICK_HEIGHT = 25;
    final int BRICK_WIDTH = 100;

    private int ballSpeedX = 0;
    private int ballSpeedY = 6;
    private int paddleSpeedX = 10;
    private int brickPosition = 0;
    private int totalBricks = 34;

    private boolean isDownKey_A = false;
    private boolean isDownKey_D = false;
    private boolean paused = false;
    private boolean ballGracePeriod = true;
    private boolean winCondition = false;

    Image bufferedGraphics;
    Graphics gfx;

    GameObject playerPaddle;
    GameObject gameBall;

    ArrayList<GameObject> bricks;
    Thread gameThread;
    
    ScheduledExecutorService ballControl = Executors.newSingleThreadScheduledExecutor();
    Runnable reEnableBall = new Runnable() {
        public void run() {
            ballGracePeriod = false;
        }
    };

    public BrickBreakerPanel(){
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.black);
        this.addKeyListener(new Keychecker());
        this.setFocusable(true);
        newGame();
    }

    public void newGame(){
        playerPaddle = new GameObject((PANEL_WIDTH / 2) - 95,  PANEL_HEIGHT - 100, PLAYER_PADDLE_WIDTH, PLAYER_PADDLE_HEIGHT, Color.blue);
        gameBall = new GameObject(PANEL_WIDTH / 2 - BALL_WIDTH, PANEL_HEIGHT / 2, BALL_WIDTH, BALL_HEIGHT, Color.yellow);
        gameBall.changeSpeed(ballSpeedX, ballSpeedY);
        bricks = new ArrayList<GameObject>();
        createBricks();
        gameThread = new Thread(this);
        gameThread.start(); 
        ballControl.schedule(reEnableBall, 500, TimeUnit.MILLISECONDS);
    }

    public void paint(Graphics g){
        bufferedGraphics = createImage(getWidth(), getHeight());
        gfx = bufferedGraphics.getGraphics();

        gfx.setColor(Color.black);
        gfx.fillRect(0, 0, getWidth(), getHeight());

        //Draw objects
        playerPaddle.drawObject(gfx);
        gameBall.drawObjectOval(gfx);

        for (GameObject brick : bricks){
            brick.drawObject(gfx);
            brick.drawBoundaries(gfx);
        }
        
        if (winCondition){
            gfx.setColor(Color.white);
            gfx.setFont(new Font("Times New Roman",Font.BOLD,50));
            gfx.drawString("You Win!",(this.getWidth() / 2) - 150 , this.getY() + 150);
        }

        g.drawImage(bufferedGraphics, 0, 0, this);
    }
    
    public void checkCollision(){
        if (playerPaddle.getX() <= getX())
            playerPaddle.setPosition(getX(), playerPaddle.getY());

        if (playerPaddle.getX() + playerPaddle.getWidth() >= getWidth())
            playerPaddle.setPosition(getWidth() - playerPaddle.getWidth(), playerPaddle.getY());

        // check if ball hits the sides/top
        if (gameBall.getX() <= 0){
            gameBall.setPosition(0, gameBall.getY());
            gameBall.changeDirection();
        }
        if (gameBall.getX() + BALL_WIDTH  >= PANEL_WIDTH){
            gameBall.setPosition(PANEL_WIDTH - BALL_WIDTH, gameBall.getY());
            gameBall.changeDirection();
        }
        if (gameBall.getY() <= 0){
            gameBall.setPosition(gameBall.getX(), 0);
            gameBall.changeVerticalDirection();
        }

        // if ball falls below player paddle reset position and apply a delay before it moves again
        if (gameBall.getY() >= PANEL_HEIGHT - BALL_HEIGHT){
            resetBall();
            ballGracePeriod = true;
            ballControl.schedule(reEnableBall, 500, TimeUnit.MILLISECONDS);
        }
        
        // change ball direction if it hits the player's paddle 
        playerPaddle.checkCollision(gameBall);
        if (playerPaddle.getHitDirection() == 'N'){
            gameBall.changeVerticalDirection();
            alterBallAngle();
        }
        if (playerPaddle.getHitDirection() == 'E' || playerPaddle.getHitDirection() == 'W'){
            gameBall.changeDirection();
        }

        brickPosition = 0;
        for (GameObject brick: bricks){
            brick.checkCollision(gameBall);
            if (brick.getHitDirection() == 'E' || brick.getHitDirection() == 'W'){
                gameBall.changeDirection();
                break;
            }
            if (brick.getHitDirection() == 'S' || brick.getHitDirection() == 'N'){
                gameBall.changeVerticalDirection();
                break;
            }
            ++brickPosition;
        }
        if (brickPosition != totalBricks + 1){
            removeBrick(brickPosition);
        }
    }

    public void removeBrick(int position){
        bricks.remove(position);
        --totalBricks;
        if (bricks.isEmpty())
            winCondition = true;
    }
    public void resetBall(){
        gameBall.setPosition(PANEL_WIDTH / 2 - BALL_WIDTH, PANEL_HEIGHT / 2);
        gameBall.changeSpeed(1, ballSpeedY);
    }

    public void moveObjects(){
        playerPaddle.moveObject();

        if (ballGracePeriod == false)
        gameBall.moveObject();
    }

    public void alterBallAngle(){
        int ballCenter = gameBall.getX() +  (gameBall.getWidth() / 2);
        int playerPaddleCenter = playerPaddle.getX() + (playerPaddle.getWidth() / 2);
        int centerBounds = 16;

        // ball hits center paddle
        if (ballCenter > playerPaddleCenter - centerBounds && ballCenter < playerPaddleCenter + centerBounds){
            ballSpeedX = 1;
        }

        // ball hits just outside of center
        else if (ballCenter > playerPaddleCenter - (centerBounds * 2) && ballCenter < playerPaddleCenter + (centerBounds * 2)){
            ballSpeedX = 4;
        }

        // ball hits towards tha paddle edges
        else{
            ballSpeedX = 6;
        }

        if (ballCenter - playerPaddleCenter < 0)
                ballSpeedX *= -1;

        gameBall.changeSpeed(ballSpeedX, gameBall.getVerticalSpeed());
    }

    public void run() {
        long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
	    double delta = 0;
		while(true) {
            long now = System.nanoTime();
            delta += (now -lastTime)/ns;
            lastTime = now;
            if(delta >=1) {
                if (!paused && !winCondition){
                    moveObjects();
                    checkCollision();
                    repaint();
                }
                delta--;
            }
        }
    }

    public void createBricks(){
        int brickSpacing = 22;
        int brickSpawnPositionX = brickSpacing;
        int brickSpawnPositionY = 25;

        for (int i = 0; i <= 34; i++){
            if (brickSpawnPositionX + PADDLE_WIDTH >= PANEL_WIDTH){
                brickSpawnPositionX = brickSpacing;
                brickSpawnPositionY += 50;
            }
            bricks.add(new GameObject(brickSpawnPositionX, brickSpawnPositionY, PADDLE_WIDTH, PADDLE_HEIGHT, Color.gray));
            brickSpawnPositionX  += PADDLE_WIDTH + brickSpacing;
        }
    }
    public class Keychecker extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            if (e.getKeyChar() == 'a' ){
                isDownKey_A = true;
                playerPaddle.changeSpeed(-paddleSpeedX, 0);
            }
            if(e.getKeyChar() == 'd'){
                isDownKey_D = true;
                playerPaddle.changeSpeed(paddleSpeedX, 0);
            }
        }

        public void keyReleased(KeyEvent e){
            // pause game movements
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE ){
                if (paused == false)
                    paused = true;
                else if (paused == true)
                    paused = false;
            }

            if (e.getKeyChar() == 'a')
                isDownKey_A = false;
            if (e.getKeyChar() == 'd')
                isDownKey_D = false;
            if (!isDownKey_A && !isDownKey_D)
                playerPaddle.changeSpeed(0, 0);
        }
    }
}
