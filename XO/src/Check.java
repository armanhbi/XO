import javax.swing.JButton;

public class Check {

	JButton[][] buttons;

	public Check(JButton[][] buttons) {
		this.buttons = buttons;
	}

	public int checkWinner() {
		int row = checkRows();
		int colum = checkColum();
		int[] diagonals = { checkDiagonal1(), checkDiagonal2() };
		boolean draw = checkDraw();
		if (row != -1)
			return row;
		if (colum != -1)
			return colum;
		if (diagonals[0] != -1)
			return diagonals[0];
		if (diagonals[1] != -1)
			return diagonals[1];
		if (draw)
			return 2;
		return -1;
	}

	public int checkDiagonal2() {
		if (buttons[2][0].getText() != "") {
			if (buttons[2][0].getText().equals(buttons[1][1].getText())
					&& buttons[1][1].getText().equals(buttons[0][2].getText())) {
				return buttons[1][1].getText() == "X" ? 0 : 1;
			}
		}
		return -1;
	}

	public int checkDiagonal1() {
		if (buttons[0][0].getText() != "") {
			if (buttons[0][0].getText().equals(buttons[1][1].getText())
					&& buttons[1][1].getText().equals(buttons[2][2].getText())) {
				return buttons[0][0].getText() == "X" ? 0 : 1;
			}
		}
		return -1;
	}

	public int checkColum() {
		for (int i = 0; i < 3; i++) {
			if (buttons[i][0].getText() != "") {
				if (buttons[i][0].getText().equals(buttons[i][1].getText())
						&& buttons[i][1].getText().equals(buttons[i][2].getText())) {
					return buttons[i][0].getText() == "X" ? 0 : 1;
				}
			}
		}
		return -1;
	}

	public int checkRows() {
		for (int i = 0; i < 3; i++) {
			if (buttons[0][i].getText() != "") {
				if (buttons[0][i].getText().equals(buttons[1][i].getText())
						&& buttons[1][i].getText().equals(buttons[2][i].getText())) {
					return buttons[0][i].getText() == "X" ? 0 : 1;
				}
			}
		}
		return -1;
	}

	public boolean checkDraw() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (buttons[x][y].getText().equals("")) {
					return false;
				}
			}
		}
		return true;
	}

}
