package BrickBreaker;


import java. awt.*;
public class GameObject {

    private int verticalSpeed = 0;
    private int horizontalSpeed = 0;

    private int x;
    private int y;
    private int width;
    private int height;

    private Color color;
    private char hitDirection = ' ';

    public GameObject(){
        this.x=0;
		this.y=0;
		this.width=0;
		this.height=0;
    }

    public GameObject(int x, int y, int width, int height, Color color){
        this.color = color;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
    }

    public void drawBoundaries(Graphics graphics){
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.red);
        graphics2D.drawLine(x, y, x + width, y);
        graphics2D.drawLine(x, y + height, x + width, y + height);
        graphics2D.drawLine(x, y, x, y + height);
        graphics2D.drawLine(x + width, y, x + width, y + height);
    }

    public void drawObject(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void drawObjectOval(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
    public void checkCollision(GameObject collider){

        int currentRB = this.x + this.width;
        int currentLB = this.y + this.height;

        int colliderRB = collider.getX() + collider.getWidth();
        int colliderLB = collider.getY() + collider.getHeight();

        if ((colliderLB >= this.y && colliderLB <= currentLB) || (collider.getY() >= this.y && collider.getY() <= currentLB || this.y > collider.y && currentLB < colliderLB )){
            if ((colliderRB >= this.x && colliderRB <= currentRB) || (collider.getX() >= this.x && collider.getX() <= currentRB || (collider.getX() < this.x && colliderRB > currentRB)) ){

                // object moves in same direction
                collider.moveObject(-collider.getHorizontalSpeed(), -collider.getVerticalSpeed());

                // move in correct direction given where the collision happens
                if (hitDirection == 'N' || hitDirection == 'S')
                    collider.moveObject(collider.getHorizontalSpeed(), this.getVerticalSpeed());
                if (hitDirection == 'E' || hitDirection == 'W')
                    collider.moveObject(this.getHorizontalSpeed(), collider.getVerticalSpeed());

                // if no previous hit direction is found look for a new one  
                if (hitDirection == ' '){
                    collider.moveObject(this.horizontalSpeed, this.verticalSpeed);
                    if (collider.getX() > (this.x + this.width) && (collider.getX() + collider.getWidth()) > (this.x + this.width)){
                        hitDirection = 'E';
                        collider.setPosition(this.getX() + this.getWidth(), collider.getY());
                    }
                    if ((collider.getX() + collider.getWidth())  < this.x && collider.getX() < this.x){
                        hitDirection = 'W';
                        collider.setPosition(this.x - collider.getWidth(), collider.getY());
                    }
                    if ((collider.getY() + collider.getHeight()) < this.y && (collider.getY() < this.y)){
                        hitDirection = 'N';
                        collider.setPosition(collider.getX() , this.y - collider.height);
                    }
                    if (collider.getY()  > (this.y + this.height) && (collider.getY() + collider.getHeight())  > (this.y + this.height)){
                        hitDirection = 'S';
                        collider.setPosition(collider.getX() , this.y + this.height );
                    }
                }
                return; 
            }
        }
        // default to no hit direction
        hitDirection = ' ';
        return;
    }

    // move object along current speed values
    public void moveObject(){
        this.x += horizontalSpeed;
        this.y += verticalSpeed;
    }

    // move object along new values
    public void moveObject(int x, int y){
        this.x += x;
        this.y += y;
    }

    public void changeSpeed(int x, int y){
        this.horizontalSpeed = x;
        this.verticalSpeed = y;
    }

    // manually set object position
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void changeDirection(){
        this.horizontalSpeed = -this.horizontalSpeed;
    }

    public void changeVerticalDirection(){
        this.verticalSpeed =-this.verticalSpeed;
    }

    public char getHitDirection(){
        return this.hitDirection;
    }

    public int getHorizontalSpeed(){
        return this.horizontalSpeed;
    }

    public int getVerticalSpeed(){
        return this.verticalSpeed;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

}
