
/* Bepen Neupane
 * NetID: bneupane
 * Project 4
 * TR 11:05 - 12:20
 * I did not collaborate with anyone on this assignment.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

public class LobPong extends JComponent implements KeyListener, ActionListener, MouseListener {

	protected int keyCode; // what key as pressed
	protected int paddleX = 100; // starting x position of the paddle
	protected int paddleY = 0; // randomly assigned y position of the paddle which changes later
	protected int paddleMove = 0; // when this changes, the paddle moves
	protected int ballX = 30; // x position of the ball
	protected int ballY = 100; // y position of the ball
	protected int veloX = 0; // x velocity of the ball
	protected int veloY = 0; // y velocity of the ball
	protected int gravity = 0; //random value for gravity which will change later
	protected int seconds = 30; // 30 seconds each level
	protected int life = 3; // user starts off with three lives
	protected int level = 1; // user starts off on level 1
	protected int objectX = 0; // the x position of the heart and the broken heart which increment and decrement the lives
	protected int paddleWidth = 200; //starting width of the paddle
	protected boolean win = false; // the game starts of with win being false because the user hasn't won yet
	protected boolean leftMove = false; // false if the left arrow key isn't being held down, true otherwise
	protected boolean rightMove = false; // false if the right arrow key isn't being held down, true otherwise
	protected boolean heartVisible = false; //when this is changed to true, the heart appears and moves across the screen
	protected boolean deathVisible = false; //when this is changed to true, the broken heart appears and moves across the screen
	protected Timer pTimer; // timer for the paddle
	protected Timer bTimer; // timer for the ball
	protected Timer tTimer; // timer for the time left in the level
	protected Timer lTimer; // timer for the heart
	protected Timer dTimer; // timer for the broken heart
	protected Image background; // initializes background as an image
	protected Image heart; // initializes heart as an image
	protected Image death; // initializes death as an image

	public LobPong() {
		super();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		setLayout(new BorderLayout());
		pTimer = new Timer(3, new PaddleTimer()); // this timer allows the screen to be repainted every 3 ms so the paddle movements are registered
		pTimer.start();
		bTimer = new Timer(30, new BallTimer()); // this timer allows the screen to be repainted every 30ms to register the ball movement
		tTimer = new Timer(1000, new TimeTimer()); // this timer allows the screen to repaint every 1 second and also has a decrementing method for seconds
		lTimer = new Timer(50, new LifeTimer()); // this timer is to animate the heart
		dTimer = new Timer(50, new DeathTimer()); // this timer is to animate the broken heart

	}

	@Override
	public void paintComponent(Graphics g) {
		paddleY = getHeight() - 30;
		ImageIcon b = new ImageIcon("background.jpg");
		background = b.getImage();
		g.drawImage(background, 0, 0, null); // this paints the background which is the space background
		ImageIcon heartIcon = new ImageIcon("life.png");
		heart = heartIcon.getImage(); //adds the heart image to heart
		ImageIcon deathIcon = new ImageIcon("death.png");
		death = deathIcon.getImage(); //adds the broken heart image to death

		if (heartVisible == true) { //when heartVisible is true, the heart will be drawn
			g.drawImage(heart, objectX, getHeight() - 300, null);
		}
		if (deathVisible == true && life > 1) { //when deathVisible is true, the broken heart will be drawn. It will only be drawn when the player has more than one live left
			g.drawImage(death, objectX, 250, null);
		}

		if (life > 0 && win == false) { // if the user hasn't won or lost yet, the paddle and the ball will be drawn with the life count, the time, and the level
			g.setColor(Color.WHITE);
			g.fillOval(ballX, ballY, 15, 15);
			g.setColor(Color.ORANGE);
			g.fillRect(paddleX, paddleY, paddleWidth, 30);
			g.setColor(Color.GREEN);
			g.setFont(new Font("Impact", Font.BOLD, 25)); //the level, lives, and time are drawn with this font
			g.drawString("Level: " + level, getWidth() / 2 - 50, 100);
			g.drawString("Time: " + seconds, getWidth() / 2 - 50, 150);
			g.drawString("Lives: " + life, getWidth() / 2 - 50, 200);
			
		} else if (life == 0 && win == false) { // if the user has lost (since they have 0 lives), the end screen is shown and the user has an option to try again
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Herculanum", Font.PLAIN, 100));
			g.drawString("Game Over", 0, 100);
			g.drawString("Level Reached: " + level, 0, getHeight() - 100); //the level that the user reached will be displayed
			g.fillRect(30, getHeight() / 2 - 50, 200, 100);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Times New Roman", Font.PLAIN, 35));
			g.drawString("Try Again?", 40, getHeight() / 2 + 5);
			g.setFont(new Font("Times New Roman", Font.PLAIN, 15));
			g.drawString("(Click here)", 40, getHeight() / 2 + 30);
		}
		if (win == true) { //if the user wins, the win screen will be shown which also displays the lives the user had left
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Herculanum", Font.PLAIN, 100));
			g.drawString("You Win!", 0, 100);
			g.drawString("Lives Left: " + life, 0, getHeight() - 100);
		}

	}

	protected class PaddleTimer implements ActionListener {
		public void actionPerformed(ActionEvent e) { //every time the timer sets off the action listener, the paddle position will change based on whether or not the user was holding the arrow keys down
			paddleX += paddleMove;
			repaint(); //canvas is repainted to reflect the changes
		}
	}

	protected class BallTimer implements ActionListener { //when the timer sets off, the ball position is updated and the canvas is repainted
		public void actionPerformed(ActionEvent e) {
			ballPosition();
			repaint();
		}
	}

	protected class TimeTimer implements ActionListener { // when the timer sets off, seconds, which starts at 30, is decremented by 1
		public void actionPerformed(ActionEvent e) {
			seconds--;
			if (seconds == 10) { // heart appears and goes through the screen
				heartVisible = true;
				objectX = 0; // the starting x of the heart
				lTimer.start(); // timer starts
			}
			if (seconds == 25) { // broken heart appears and goes through the screen
				deathVisible = true;
				objectX = 0; // the starting x of the broken heart
				dTimer.start(); // timer starts
			}
			if (seconds == 0) { // if the user gets to 0 seconds, the next level starts which has a slightly smaller paddle
				level++;
				paddleWidth -= 20;
				if (paddleWidth == 0) { // if the paddle width gets to 0, the user wins
					win = true;
				} else { // if the user doesn't win, the restart method is called which brings the ball back and positions the paddle and ball and also puts seconds back to 30
					restart();
				}
			}

		}
	}

	protected class LifeTimer implements ActionListener { // this timer is run when there are 10 seconds left
		public void actionPerformed(ActionEvent e) {
			objectX += getWidth() / 200; // the x position is incremented by two hundredths of the width
			if (objectX > getWidth()) { // if the heart goes past the width of the screen, it is no longer visible
				heartVisible = false;
			}
			repaint(); // repaints canvas to reflect the change in position
		}
	}

	protected class DeathTimer implements ActionListener {// this timer is run when there are 25 seconds left
		public void actionPerformed(ActionEvent e) {
			objectX += getWidth() / 200;// the x position is incremented by two hundredths of the width
			if (objectX > getWidth()) { // if the broken heart goes past the width of the screen, it is no longer visible
				deathVisible = false;
			}
			repaint();// repaints canvas to reflect the change in position
		}
	}

	public void ballPosition() { //whenever this method is called from the ball timer, it changes the position of the ball
		ballX += veloX; // x position increases by x velocity which is reset when the spacebar is set
		veloY += gravity; // y velocity is changed by gravity
		ballY += veloY; // y position changes by y velocity
		if (ballY >= paddleY + 3) { // if the ball is over the paddle, this runs
			if ((ballX > paddleX - 10) && (ballX < paddleX + paddleWidth + 10)) { // if the ball position is within the paddle, with some room for slack, it registers as a hit and the y velocity changes
				if (rightMove == true) { // if the right arrow key is being held down while the ball hits the paddle, the velocity will increase by 6 which
					veloX += 6;			 // causes the ball to either move faster to the right or slower to the left
				}
				if (leftMove == true) { // if the left arrow key is being held down while the ball hits the paddle, the velocity will decrease by 6 which
					veloX -= 6;			// causes the ball to either move faster to the left or slower to the right
				}
				veloY *= -1; // because the y velocity changes, the ball will now be going upwards as if it were a bounce, until it loses momentum and starts falling

			}
		}
		if (ballY >= getHeight()) { // if the ball falls off the screen, the user loses a life
			if (win == false) { // i did this to prevent the user from losing a life on the win screen
				life--;
			}
			restart(); // the restart method resets the position of the ball and paddle, and also resets the time
		}
		
		

		if (ballX <= 0) { // if the ball hits the left wall, this runs
			veloX *= -1; //x velocity changes which changes the way the ball is going
			veloY += 4; // y velocity increases so the ball doesn't lose momentum too fast

		}
		if ((ballX + 15) >= getWidth()) { // if the ball hits the right wall, this runs
			veloX *= -1; //x velocity changes which changes the way the ball is going
			veloY += 4; // y velocity increases so the ball doesn't lose momentum too fast

		}
		if (ballX > objectX && ballX < objectX + 50 && ballY > getHeight() - 350 && ballY < getHeight() - 300
				&& heartVisible == true) { // if the ball goes through or touches the heart, the user will gain a life
			life += 1;
			heartVisible = false;
			repaint();
		}

		if (ballX > objectX && ballX < objectX + 50 && ballY > 250 && ballY < 300 && deathVisible == true) { // if the ball goes through or touches the broken heart, the user will lose a life
			if (!(life == 1)) { // will only run if the user has more than one life to make the game easier
				life -= 1;
				deathVisible = false;
				repaint();
			}

		}

	}

	public void restart() { //whenever this is called, the game essentially resets itself to get ready for the next level
		bTimer.stop(); // ball timer stops
		tTimer.stop(); // the timer for the time stops
		seconds = 30; // seconds left goes back to 30
		ballX = 30; // the position of the ball is set back to the original position
		ballY = 100;
		paddleX = 100; // the paddle goes back to the original position
		heartVisible = false; // the heart is invisible
		deathVisible = false; // the broken heart is invisible
		repaint(); // repaint

	}

	public void playAgain() { // if the user loses and clicks on the play again rectangle, lives go back to three, you go back to level one, and the paddle width goes back to the original size then the restart method is called
		life = 3;
		level = 1;
		paddleWidth = 200;
		restart();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) { // if the left arrow key is pressed, the left method is called and leftMove is set to true
			left();
			leftMove = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // if the right arrow key is pressed, the right method is called and rightMove is set to true
			right();
			rightMove = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) { // if the spacebar is pressed, the game starts with predetermined velocities and gravity, seconds go back to 30
			tTimer.start();
			bTimer.start();
			seconds = 30;
			veloX = 9;
			veloY = 2;
			gravity = 1;
		}

	}

	public void left() { // the left method changes paddleMove to -8 which means that the x value for the paddle will decrement by 8
		paddleMove = -8;
	}

	public void right() { // the right method changes paddleMove to 8 which means that the x value for the paddle will increment by 8
		paddleMove = 8;
	}

	@Override
	public void keyReleased(KeyEvent e) { // when either the left or right keys are released, paddleMove changes to 0, preventing the paddle from moving
		paddleMove = 0;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // if the user lets go of the right key, rightMove is set to false
			rightMove = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) { // if the user lets go of the left key, leftMove is set to false
			leftMove = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) { // when the mouse is pressed, this runs
		int xPress = e.getX();
		int yPress = e.getY();
		if (life == 0) { // if the lives are at 0, that means that the user is at the end screen
			if ((xPress >= 30 && xPress <= 230) && (yPress >= getHeight() / 2 - 50 && yPress <= getHeight() / 2 + 50)) { // if the user clicks on the yellow rectangle that says "Try again?", the game restarts
				playAgain(); // the game restarts
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(); //creates frame
		LobPong canvas = new LobPong(); // creates canvas
		frame.add(canvas); // canvas added to frame
		frame.setSize(1000, 1000); // size of the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if the user closes the frame, the program ends
		frame.setVisible(true); // frame is visible
	}
}