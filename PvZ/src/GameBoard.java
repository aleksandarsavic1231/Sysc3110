import java.awt.Point;

/**
 * GameBoard consisting of multiple tiles. 
 * 
 * @author kylehorne
 * @version 28 Oct 18
 */
public class GameBoard implements Board {
	
	/**
	 * The rows for all GameBoard objects.
	 */
	public static final int ROWS = 5;
	
	/**
	 * The columns for all GameBoard objects.
	 */
	public static final int COLUMNS = 10;
	
	/**
	 * The beginning ASCII character for all GameBoard objects.
	 */
	public static final int ASCII_LOWER_BOUND = 65;
	
	/**
	 * Tiles making up the board.
	 */
	private char[][] tiles;
	
	/**
	 * Constructor.
	 */
	public GameBoard() {
		tiles = new char [ROWS][COLUMNS];
		clear();
	}
	
	/**
	 * Iterate over GameBoard and apply update.
	 * 
	 * @param t The tile.
	 */
	private void iterateBoard(Tile t) {
		for (int i = 0 ; i < ROWS ; i++){
			for (int j = 0 ; j < COLUMNS ; j++){
				t.update(i, j);
			}
		}	 
	}
	
	@Override
	public void clear () {iterateBoard((i, j) -> tiles[i][j] = ' '); }

	@Override
	public void print () {
		for (int j = 0; j < COLUMNS; j++) 
			System.out.print(Character.toString((char) (j + ASCII_LOWER_BOUND)) + " ");
		System.out.println();
		iterateBoard((i, j) -> {
			System.out.print(Character.toString(tiles[i][j]));
		    System.out.print(" ");
		    if (j == COLUMNS - 1) System.out.println(i);
		});
	}
	
	@Override
	public void addEntity(Entity e) {
		// TODO: Throw exception if out of bounds
		// TODO: Loosen coupling 
		int i = e.getX();
		int j = e.getY(); 
		char c = tiles[j][i];
		if (j < ROWS && i < COLUMNS) {
			char identifier = ' '; 
			if (e instanceof PeaShooter) identifier = PeaShooter.IDENTIFIER;
			else if (e instanceof Sunflower) identifier = Sunflower.IDENTIFIER;
			else if (e instanceof Bullet) identifier = Bullet.IDENTIFIER;
			else identifier = Zombie.IDENTIFIER;
			if (c == ' ') tiles[j][i] = identifier;
		}
	}
	
	@Override
	public void removeEntity(Entity e) {
		tiles[e.getY()][e.getX()] = ' ';
	}

	@Override
	public Point isValidLocation(String input) {
		int x = input.charAt(0) - ASCII_LOWER_BOUND;
		int y = Character.getNumericValue(input.charAt(1));
		// Ensure valid input 
		if (input.length() != 2 || !(0 <= x && x < GameBoard.COLUMNS && 0 <= y && y < GameBoard.ROWS)) {
			System.out.println("Invalid spawn location.");
			return null;
		} 
		return new Point(x, y);
	}
	
}
