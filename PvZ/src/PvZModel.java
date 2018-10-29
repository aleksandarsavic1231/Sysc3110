import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;

/** PvZModel
 * 
 * The Model class initializes the game by commencing a game loop which
 * prompts the user and initializes the gameboard 
 *
 */


public class PvZModel {

	/**
	 * Spawned entities. 
	 */
	private LinkedList<Entity> entities;
  	
	/**
	 * Game balance.
	 */
	private int sunPoints;
	
	/**
	 * Current game iteration.
	 */
	public int gameCounter;

	private Board gameBoard;

	private Scanner reader;
	
	/**
	 * The period which sun points are automatically rewarded to balance (welfare)
	 */
	public static final int PAYMENT_PERIOD = 4;
	
	/**
	 * The sun points automatically deposited every payment period
	 */
	public static final int WELFARE = 25;
	
	public static final int INITIAL_BALANCE = 1000;
	
	//PvZModel constructor
	public PvZModel() {
		 entities = new LinkedList<Entity>();
		 sunPoints = INITIAL_BALANCE; 
		 gameBoard = new GameBoard();
		 gameCounter = 0;
	}	
	
	/**
	 * 
	 * @param p
	 * @return boolean type
	 * Checks true or false if another entity is occupied in that specific position
	 */
	private boolean isOccupied(Point p) {
		for(Entity e : entities) {
			if (e.getX() == p.x && e.getY() == p.y) return true; 
		}
		return false;
	}
	
	/**
	 * 
	 * @param entityName
	 * @return Point type
	 * prompts user to spawn new entity and gets location
	 */
	private Point getLocation(String entityName) {
		System.out.println("Enter a location to spawn new " + entityName + ": ");
		reader = new Scanner(System.in);
		String input = reader.next().toUpperCase();
		// Ensure valid spawn location 
		Point p = gameBoard.isValidLocation(input);
		if (p == null) {
			return getLocation(entityName);
		}
		// Ensure location is not currently occupied by entity
		if (isOccupied(p)) {
			System.out.println("Location " + input + " is currently occupied.");
			return getLocation(entityName);
		}
		// Return valid spawn location
		return p;
	}
	
	/**
	 * 
	 * @return boolean type
	 * Checks true or false if Zombie entity reaches end of board that results in a loosing game over 
	 */
	private boolean isGameOver() {
		// TODO: Make functional method to iterate over entities
		for(Entity e : entities) {
			if (e instanceof Zombie && e.getX() == -1) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return boolean type 
	 * Checks true or false for if round is over
	 */
	private boolean isRoundOver() {
		// TODO: Make functional method to iterate over entities
		for(Entity e : entities) {
			if (e instanceof Zombie) return false;
		}
		return true;
	}

	/**
	 * Returns void
	 * Shows amount of sun points left and what plants are available to buy 
	 * Prompts user to make the next move on making a purchase or not and proceeds accordingly 
	 */
	private void nextMove() {
		// TODO: Make functional 
		// Update isDeployable
		//Sunflower.isDeployable(gameCounter);
		//PeaShooter.isDeployable(gameCounter);	
		System.out.println("Sun points: " + sunPoints);
		// Check for purchasable items
		boolean isSunflowerPurchasable = sunPoints >= Sunflower.COST && Sunflower.isDeployable(gameCounter);
		boolean isPeaShooterPurchasable = sunPoints >= PeaShooter.COST && PeaShooter.isDeployable(gameCounter);
		
		for(ListIterator<Entity> iter = entities.listIterator(); iter.hasNext(); ) {
			Entity e = iter.next();
			if (e.getClass().getName().equals("Zombie")) {
				Zombie f = (Zombie) e;
				System.out.println("Zombie's Health: " + f.getHealth());
			}
			if (e instanceof PeaShooter) {
				System.out.println("PeaShooter Health: " + ((PeaShooter) e).getHealth());
			}
		}
		
		if (!(isSunflowerPurchasable || isPeaShooterPurchasable)) {
			System.out.println("No store items deployable.");
		} else {
			// Print available items to purchase 
			System.out.println("Items available for purchase:");
			String sunflowerName = Sunflower.class.getName();
			if (isSunflowerPurchasable) {
				System.out.println("<" + sunflowerName + " : " + Sunflower.COST + " Sun points>");
			}
			String peaShooterName = PeaShooter.class.getName();
			if (isPeaShooterPurchasable) {
				System.out.println("<" + peaShooterName + " : " + PeaShooter.COST + " Sun points>");
			} 
			System.out.println("Press <Enter> to proceed without purchases");
			// Read from standard out
			reader = new Scanner(System.in);
			String input = reader.nextLine().toUpperCase();
			if (isSunflowerPurchasable && sunflowerName.toUpperCase().equals(input)) {
				sunPoints -= Sunflower.COST;
				entities.add(new Sunflower(getLocation(sunflowerName)));	
				Sunflower.setNextDeployable(gameCounter);
			} else if (isPeaShooterPurchasable && peaShooterName.toUpperCase().equals(input)) {
				sunPoints -= PeaShooter.COST;
				entities.add(new PeaShooter(getLocation(peaShooterName)));
				PeaShooter.setNextDeployable(gameCounter);
			} 
			else if(input.isEmpty()) {
				return;
			}
			else {
				System.out.println("Invalid input!");
				nextMove();
			}
		}
	}
	
	/**
	 * 
	 * @param n
	 * Returns void
	 * Spawns Zombie Entity to the game board
	 */
	private void spawnZombies(int n) {
		for (int i = 0; i < n; i ++) {
			entities.add(new Zombie(new Point(GameBoard.COLUMNS - 1, 0)));
		}
	}
	
	/**
	 * 
	 * @param m
	 * @return boolean type
	 * Checks true and false if collision has been made between two things in same position
	 * Updates game when bullet and entity zombie collide resulting in damage to the zombie which lowers the health
	 * Updates game when entity zombie is infront of any plant and checks if plant is alive or not due to damage caused by zombie
	 */
	private boolean isCollision(Moveable m) {
		// TODO: Make functional 
		boolean isCollision = false;
		for(ListIterator<Entity> iter = entities.listIterator(); iter.hasNext(); ) {
			// Ensure the current position of Entity is not the next move of Moveable
			// Ignore if Entity and Moveable are instances of Zombie
			// (Zombies may share next position)
			Entity e = iter.next();
			boolean isZombie = e instanceof Zombie;
			if (e.getX() == m.nextPosition().getX() && e.getY() == m.nextPosition().getY()) {
				
				isCollision = true;
				// Zombie hit by bullet
				if (isZombie && m instanceof Bullet) {
					((Zombie) e).setHealth(((Bullet) m).getDamage());
					// Zombie should update position regardless of being hit by bullet 
					if (isCollision((Moveable) e)) {
						((Zombie) e).updatePosition();
					}
					break;
				}
				//Zombie collided with plant 
				if ((e instanceof PeaShooter || e instanceof Sunflower) && m instanceof Zombie) {
					((Alive) e).setHealth(Zombie.DAMAGE);				
					break;
				}
			}
		}
		return isCollision;
	}

	/**
	 * Returns void
	 * loops turn based game until game is over due to either all zombies are dead (win) or zombie making it to the end of the board (lose)
	 * prints board after every turn to update user 
	 * Notifies User on result of game once game is over
	 */
	private void gameLoop() {
		gameBoard.print();
		spawnZombies(1);
		while (!isGameOver()) {
			gameBoard.clear();
			nextMove();
			boolean deathOccurred = false;
			for(ListIterator<Entity> iter = entities.listIterator(); iter.hasNext(); ) {
				Entity e = iter.next();
				// Unlock Moveable entity
				if (e instanceof Moveable) ((Moveable) e).unlock();
				// Add entity to current game board
				gameBoard.addEntity(e);
				// Check if Shooter can fire 
				if (e instanceof Shooter && ((Shooter) e).canShoot())  {
					// Instantiate new bullet if Entity is instance of PeaShooter
					if (e instanceof PeaShooter) {
						iter.add(new Bullet(new Point(e.getX() + 1, e.getY()), PeaShooter.DAMAGE));
					}
					// Increase sun points if Entity is instance of Sunflower
					if (e instanceof Sunflower) {
						sunPoints += Sun.REWARD;
					}
				}
				// Update location of entity if instance of Moveable	
				if (e instanceof Moveable) {
					if (!isCollision((Moveable) e)) {	
						// Lock Moveable entity from moving again during current game iteration
						((Moveable) e).updatePosition();
					} else if (e instanceof Bullet) {
						// Remove bullet on impact
						iter.remove();
					}
				}
				// Remove bullet if domain is greater than game board columns
				if (e instanceof Bullet && e.getX() >= GameBoard.COLUMNS) {
					iter.remove();
				}
				// Check for dead entities
				if (e instanceof Alive && ((Alive) e).getHealth() <= 0) {
					System.out.println(e.getClass().getName() + " has died.");
					gameBoard.removeEntity(e);
					iter.remove();
					deathOccurred = true;
				}
			}
			gameBoard.print();
			// Check for end of round only if instance of Alive died during current game iteration.
			if (deathOccurred && isRoundOver()) break;
			gameCounter++;
			// Add automatic welfare if payment period has elapsed 
			if (gameCounter % PAYMENT_PERIOD == 0) sunPoints += WELFARE;
		}
		if (isRoundOver()) System.out.println("You beat the round."); 
		if (isGameOver()) System.out.println("Game over.");
		reader.close();
	}
	
	/**
	 * 
	 * @param args
	 * Returns void 
	 */
	public static void main(String args[]) {
		PvZModel PvZ = new PvZModel();
		PvZ.gameLoop();
	}
		
}
