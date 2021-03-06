import java.util.LinkedList;

/**
 * Next Command causes PvZ to move to its next state.
 * 
 * @author kylehorne
 * @version 25 Nov 18
 */
public class NextCommand implements Undoable {
	
	/**
	 * The last state of Model Entities.
	 */
	private LinkedList<Entity> lastEntities;
	
	/**
	 * The last state of Model balance.
	 */
	private int lastBalance;

	/**
	 * Constructor.
	 * 
	 * @param model The Model to this NextCommand Object.
	 */
	public NextCommand() {	
		
		lastEntities = new LinkedList<Entity>();
	}

	@Override
	public void execute() {
		Model model = Controller.getInstance().getModel();
		// Save state of Model.
		lastBalance = model.getBalance();
		for(Entity entity: model.getEntities()) {
			try {
				lastEntities.add(EntityFactory.clone(entity));
			} catch (UnimplementedEntity e) {
				e.printStackTrace();
			} 
		}
		// Update to next game iteration.
		model.clearBoard();		
		model.updateShooters();
		model.updateMoveables();
		model.checkForDead();
		model.spawnEntities();
		model.incrementGameCounter();
		model.updatePurchasablePlants();
		// Add automatic welfare if payment period has elapsed 
		if (model.getGameCounter() % Model.PAYMENT_PERIOD == 0) model.increaseBalance(Model.WELFARE);	
		// Check if game is still runnable
		model.checkGameOver();
		model.checkRoundOver();
	}

	@Override
	public void undo() {
		Model model = Controller.getInstance().getModel();
		// Set Model to last game state.
		model.setEntities(lastEntities);
		model.setBalance(lastBalance);
		model.decrementGameCounter();
		model.updatePurchasablePlants();
	}

	@Override
	public void redo() {
		lastEntities = new LinkedList<Entity>();
		execute();
	}

	@Override
	public String toXMLString() {
		String XMLEncoding = 
				"<NextCommand>"
						+ "<lastBalance>" + lastBalance + "</lastBalance>"
						+ "<lastEntities>";
		for(Entity entity: lastEntities) XMLEncoding += entity.toXMLString();
		return XMLEncoding += "</lastEntities></NextCommand>";
	}
	
	/**
	 * Set the last balance of this.
	 * 
	 * @param lastBalance The last balance of this.
	 */
	public void setLastBalance(int lastBalance) { this.lastBalance = lastBalance; } 
	
	/**
	 * Set the last Entities of this.
	 * 
	 * @param lastEntities The last Entities of this.
	 */
	public void setLastEntities(LinkedList<Entity> lastEntities) { this.lastEntities = lastEntities; }

}
