package Pong;
import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PongPanel extends JPanel implements Runnable{

    final int PANEL_WIDTH = 1250;
    final int PANEL_HEIGHT = 850;

    final int PADDLE_WIDTH = 25;
    final int PADDLE_HEIGHT = 150;

    final int BALL_HEIGHT = 30;
    final int BALL_WIDTH = 30;

    final int PLAYER_SCORE_X = (PANEL_WIDTH / 2) - 100;
    final int PLAYER_SCORE_Y = 50;

    final int OPPONENT_SCORE_X = (PANEL_WIDTH / 2) + 75;
    final int OPPONENT_SCORE_y = 50;

    private int ballStartSpeedX = -6;
    private int ballStartSpeedY = -1;
    private int padSpeed = 6;

    private int leftPaddleCenter;
    private int rightPaddleCenter;
    private int ballCenter;
    private int defaultBounceSpeed = 3;
    private int bounceSpeed = 3;
    private int opponentSpeed = 2;
    private int upperSpeedBounds = 4;

    private Integer playerScore = 0;
    private Integer opponentScore = 0;

    private boolean isDownKey_W = false;
    private boolean isDownKey_S = false;
    private boolean paused = false;
    private boolean ballGracePeriod = false;

    Image bufferedGraphics;
    Graphics gfx;

    ScheduledExecutorService ballControl = Executors.newSingleThreadScheduledExecutor();
    Random rand = new Random();
    GameObject gameBall;
    GameObject leftPaddle;
    GameObject rightPaddle;
    Thread gameThread;
    Runnable reEnableBall = new Runnable() {
        public void run() {
            ballGracePeriod = false;
        }
    };
    
    public PongPanel(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.black);
        this.addKeyListener(new Keychecker());
        this.setFocusable(true);
        newGame();
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void paint(Graphics g){
        bufferedGraphics = createImage(getWidth(), getHeight());
        gfx = bufferedGraphics.getGraphics();

        gfx.setColor(Color.black);
        gfx.fillRect(0, 0, getWidth(), getHeight());

        //Draw separating line
        gfx.setColor(Color.gray);
        ((Graphics2D) gfx).setStroke(new BasicStroke(3));
        gfx.drawLine(PANEL_WIDTH / 2, 0, PANEL_WIDTH / 2, PANEL_HEIGHT);
        
        //Draw objects
        leftPaddle.drawObject(gfx);
        rightPaddle.drawObject(gfx);
        gameBall.drawObject(gfx);
        updateScore(gfx);
        
        g.drawImage(bufferedGraphics, 0, 0, this);
    }

    // spawn in objects into initial positions
    public void newGame(){
        opponentSpeed = rand.nextInt(2) + upperSpeedBounds;
        leftPaddle = new GameObject(10, 200, PADDLE_WIDTH,PADDLE_HEIGHT,Color.blue);
        rightPaddle = new GameObject(PANEL_WIDTH - PADDLE_WIDTH - 10, 200, PADDLE_WIDTH, PADDLE_HEIGHT,Color.red);
        gameBall = new GameObject((PANEL_WIDTH/2) - 15, (PANEL_HEIGHT/2) - 50, BALL_WIDTH, BALL_HEIGHT,Color.white);
        gameBall.changeSpeed(ballStartSpeedX, ballStartSpeedY);
        gameBall.changeSpeed(randomDirection(gameBall.getHorizontalSpeed()), randomDirection(gameBall.getVerticalSpeed()));
    }

    public void updateScore(Graphics graphics){
        graphics.setColor(Color.white);
		graphics.setFont(new Font("Consolas",Font.PLAIN,50));
        graphics.drawString(playerScore.toString(), PLAYER_SCORE_X, PLAYER_SCORE_Y);
        graphics.drawString(opponentScore.toString(), OPPONENT_SCORE_X, OPPONENT_SCORE_y);

    }

    public void moveObjects(){
        // if ball moves past edges reset ball position
        if (gameBall.getX() <= 0 || (gameBall.getX() + BALL_WIDTH)  >= PANEL_WIDTH){
            ballGracePeriod = true;
            if (gameBall.getX() <= 0)
                ++opponentScore;
            if ((gameBall.getX() + BALL_WIDTH)  >= PANEL_WIDTH)
                ++playerScore;
            gameBall.setPosition((PANEL_WIDTH/2) - 15, (PANEL_HEIGHT/2) - 50);
            gameBall.changeSpeed(gameBall.getHorizontalSpeed(), defaultBounceSpeed);
            gameBall.changeSpeed(randomDirection(gameBall.getHorizontalSpeed()), randomDirection(gameBall.getVerticalSpeed()));
            ballGracePeriod = true;
            ballControl.schedule(reEnableBall, 500, TimeUnit.MILLISECONDS);
        }
        
        // move right paddle in respondance to gameball center location
        if (ballCenter < rightPaddleCenter - 10)
            rightPaddle.changeSpeed(rightPaddle.getHorizontalSpeed(), -opponentSpeed);
        else if (ballCenter > rightPaddleCenter + 10) 
            rightPaddle.changeSpeed(rightPaddle.getHorizontalSpeed(), opponentSpeed);
        else { rightPaddle.changeSpeed(rightPaddle.getHorizontalSpeed(), 0); }
        
        
        rightPaddle.moveObject();
        leftPaddle.moveObject();
    
        // allow ball to move after the grace period
        if (ballGracePeriod == false)
            gameBall.moveObject();
        
        // set center values for objects
        leftPaddleCenter = leftPaddle.getY() + (leftPaddle.getHeight() / 2);
        rightPaddleCenter = rightPaddle.getY() + (rightPaddle.getHeight() / 2);
        ballCenter = gameBall.getY() + (gameBall.getHeight() / 2);
        
    }

    public void checkCollision(){
        // panel top collision for ball
        if (gameBall.getY() <= 0){
            gameBall.setPosition(gameBall.getX(),0);
            gameBall.changeVerticalDirection();
        }

        // panel bottom collision for ball
        if ((gameBall.getY() + BALL_HEIGHT) >= PANEL_HEIGHT){
            gameBall.setPosition(gameBall.getX(),PANEL_HEIGHT - BALL_HEIGHT);
            gameBall.changeVerticalDirection();
        }

        // check for paddle collision
        leftPaddle.checkCollision(gameBall);
        rightPaddle.checkCollision(gameBall);

        // keep paddles in panel
        if (leftPaddle.getY() <= 0) 
            leftPaddle.setPosition(leftPaddle.getX(), 0);
        if (rightPaddle.getY() <= 0) 
            rightPaddle.setPosition(rightPaddle.getX(), 0);

        if (leftPaddle.getY() + leftPaddle.getHeight() >= PANEL_HEIGHT) 
            leftPaddle.setPosition(leftPaddle.getX(), PANEL_HEIGHT - leftPaddle.getHeight());
        if (rightPaddle.getY() + rightPaddle.getHeight() >= PANEL_HEIGHT) 
            rightPaddle.setPosition(rightPaddle.getX(), PANEL_HEIGHT - rightPaddle.getHeight());

        // Bounce ball off player paddle with randomized vertical ball speed( different angle) and opponent speed
        if (leftPaddle.getHitDirection() == 'E'){
            opponentSpeed = rand.nextInt(2) + upperSpeedBounds;
            bounceSpeed = rand.nextInt(3) + 5;
            gameBall.changeDirection();
            if (ballCenter > leftPaddleCenter - 10 && ballCenter < leftPaddleCenter + 10)
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),0);
            else if (ballCenter > leftPaddleCenter) 
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),bounceSpeed);
            else if (ballCenter < leftPaddleCenter) 
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),-bounceSpeed);

            // change ball vertical direction based on player movement if player is moving
            if (leftPaddle.getVerticalSpeed() > 0)
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),bounceSpeed);
            if (leftPaddle.getVerticalSpeed() < 0)
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),-bounceSpeed);
        }

        // if opponent hits ball send it back with random vertical speed (different angle)
        if (rightPaddle.getHitDirection() == 'W'){
            bounceSpeed = rand.nextInt(3) + 5;
            gameBall.changeDirection();
            if (ballCenter > rightPaddleCenter - 10 && ballCenter < rightPaddleCenter + 10)
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),0);
            else if (ballCenter > rightPaddleCenter) 
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),bounceSpeed);
            else if (ballCenter < rightPaddleCenter) 
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(),-bounceSpeed);
        }
        // bounce ball off paddle top/bottom sides and change vertical movement to go the opposite direction
        if (leftPaddle.getHitDirection() == 'N' || leftPaddle.getHitDirection() == 'S') 
            gameBall.changeVerticalDirection();
        if (rightPaddle.getHitDirection() == 'N' || rightPaddle.getHitDirection() == 'S') 
            gameBall.changeVerticalDirection();
    }

    // run the game
    public void run() {
        long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
	    double delta = 0;
		while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if(delta >=1) {
                if (!paused){
                    moveObjects();
                    checkCollision();
                    repaint();
                }
                delta--;
            }
        }
    }

    // used to alter the direction of the gameBall by taking in a speed value and turning it positive or negative 
    public int randomDirection(int currentValue){
        int randomDirection;
        randomDirection = rand.nextInt(2);

        if (randomDirection == 0)
            return Math.abs(currentValue);
        else {
            return currentValue * -1 ;
        }
    }

    // local key controls for moving the player paddle 
    public class Keychecker extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == 'w' ){
                isDownKey_W = true;
                leftPaddle.changeSpeed(0, -padSpeed);
            }
            if(e.getKeyChar() == 's'){
                isDownKey_S = true;
                leftPaddle.changeSpeed(0, padSpeed);
            }
    
            // reset key for testing
            if (e.getKeyChar() == 'z' ){
                gameBall.setPosition((PANEL_WIDTH/2) - 15, (PANEL_HEIGHT/2) - 200);
                gameBall.changeSpeed(gameBall.getHorizontalSpeed(), defaultBounceSpeed);
                gameBall.changeSpeed(randomDirection(gameBall.getHorizontalSpeed()), randomDirection(gameBall.getVerticalSpeed()));
            }
            
        }
        public void keyReleased(KeyEvent e) {

            // pause game movements
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE ){
                if (paused == false)
                    paused = true;
                else if (paused == true)
                    paused = false;
            }

            if (e.getKeyChar() == 'w')
                isDownKey_W = false;
            if (e.getKeyChar() == 's')
                isDownKey_S = false;
            if (!isDownKey_W && !isDownKey_S)
                leftPaddle.changeSpeed(0, 0);
        }
    }
}
