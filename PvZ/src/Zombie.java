import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * Zombie is a Moveable enemy that can do damage on impact with plants.
 * 
 * @author kylehorne
 * @version 28 Oct 18
 */
public class Zombie extends Alive implements Moveable {
	
	/**
	 * Damaged caused by Zombie on impact with a plant.
	 */
	public static final int DAMAGE = 2;
	
	/**
	 * Velocity of a Zombie.
	 */
	public static final int VELOCITY = 1;
	
	/**
	 * Character representation of a Zombie.
	 */
	public static final ImageIcon IMAGE = new ImageIcon("Zombie_PvZ-2.png");
	/**
	 * Is this bullet locked from moving.
	 */
	private boolean locked;

	/**
	 * Constructor.
	 * 
	 * @param position The spawn location of this Zombie.
	 */
	public Zombie(Point position) {
		super(position, 5);
		locked = false;
	}

	@Override
	public void updatePosition() {
		if (!locked) getPoint().setLocation(nextPosition());
		locked = true;
	}

	@Override
	public Point nextPosition() {
		return new Point(getX() - VELOCITY, getY());
	}	

	@Override
	public void unlock() {
		this.locked = false;
	}	

}


