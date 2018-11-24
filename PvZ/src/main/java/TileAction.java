import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TileAction extends Command implements ActionListener {
	
	private Point tileClicked;
	
	public TileAction(Model model, UndoManager undoManager, Point tileClicked) {
		super(model, undoManager);
		this.tileClicked = tileClicked;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getUndoManager().execute(new TileCommand(getModel(), tileClicked));
	}

}