import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

public class XO extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton[][] buttons;

	private int player, resetCounter, pointsX, pointsO;
	private int winner = -1;
	private boolean running = true;
	private boolean resetMode = false;
	private Object[] olds = new Object[3];

	private int frameSize = 500;

	private int resetDelay = 2000;
	private int resetTimes = 3;
	private int endGameAt = 3;
	private boolean winnerContinuesPlaying = true;
	private boolean showShadow = true;
	private boolean showResetText = true;

	public XO() {
		super("XO [0:0] - Its X's turn");
		setLayout(new GridLayout(3, 3));

		String[] values = Settings.readSettings();
		endGameAt = Integer.parseInt(values[0]);
		resetTimes = Integer.parseInt(values[1]);
		resetDelay = Integer.parseInt(values[2]);
		winnerContinuesPlaying = Boolean.parseBoolean(values[3]);
		showShadow = Boolean.parseBoolean(values[4]);
		showResetText = Boolean.parseBoolean(values[5]);

		buttons = new JButton[3][3];
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons[x][y] = new JButton();
				buttons[x][y].setFont(new Font("Arial", Font.PLAIN, frameSize / 5));
				buttons[x][y].setBounds(x * frameSize / 3, y * frameSize / 3, frameSize / 3, frameSize / 3);
				buttons[x][y].setName(x + "" + y);
				buttons[x][y].addActionListener(this);
				add(buttons[x][y]);
			}
		}

		for (JButton[] allButtons : buttons) {
			for (JButton curButton : allButtons) {
				curButton.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent evt) {
						if (curButton.getText().equals("") && winner == -1 && showShadow) {
							curButton.setForeground(Color.gray);
							curButton.setText(player == 0 ? "X" : "O");
						}
					}

					public void mouseExited(MouseEvent evt) {
						if (curButton.getForeground() == Color.gray && winner == -1) {
							curButton.setText("");
						}
					}
				});
			}
		}

		buttons[1][1].addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				if (resetMode && showResetText) {
					olds[0] = buttons[1][1].getFont();
					olds[1] = buttons[1][1].getForeground();
					olds[2] = buttons[1][1].getText();
					buttons[1][1].setFont(new Font("Arial", 0, 20));
					buttons[1][1].setForeground(Color.gray);
					buttons[1][1].setText("<html>Click " + resetTimes +" times<br>to reset</html>");
				}
			}

			public void mouseExited(MouseEvent evt) {
				if (resetMode && olds[0] != null && olds[1] != null && olds[2] != null) {
					resetMiddleButton();
				}

			}
		});

		MenuBar bar = createMenuBar();
		setMenuBar(bar);

		resizeSetting();

		startResetThread();

		double displayWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double displayHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		setBounds((int) displayWidth / 2 - frameSize / 2, (int) displayHeight / 2 - frameSize / 2, frameSize,
				frameSize);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int x = Integer.parseInt(String.valueOf(((JButton) e.getSource()).getName().charAt(0)));
		int y = Integer.parseInt(String.valueOf(((JButton) e.getSource()).getName().charAt(1)));
		if ((e.getActionCommand().equals("") || ((JButton) e.getSource()).getForeground() == Color.gray)
				&& winner == -1) {
			String sign = player == 0 ? "X" : "O";
			buttons[x][y].setForeground(Color.black);
			buttons[x][y].setText(sign);
			player = player == 0 ? 1 : 0;
			winner = new Check(buttons).checkWinner();
			if (winner != -1) {
				String message;
				switch (winner) {
				case 0:
					message = "Player X won!";
					pointsX++;
					break;
				case 1:
					message = "Player O won!";
					pointsO++;
					break;
				case 2:
					message = "Its a draw!";
					break;
				default:
					message = "Error";
				}

				if (pointsX == endGameAt || pointsO == endGameAt) {
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							String winnerChar = winner == 0 ? "X" : "O";
							buttons[i][j].setForeground(Color.green);
							buttons[i][j].setText(winnerChar);
							setTitle("Game over. Player " + winnerChar + " won! [Reset: Click the middle tile "
									+ resetTimes + "x in a row]");
							running = false;
							checkSubMenu(1);
						}
					}
					return;
				}
				setTitle(message + " [Reset: Click the middle tile " + resetTimes + "x in a row]");
				resetMode = true;
				colorButtons(winner);
				checkSubMenu(0);
			} else {
				updateTitle();
			}
		} else if (winner != -1 && x == 1 && y == 1) {
			if (resetCounter == (resetTimes - 1)) {
				reset();
			}
			resetCounter++;
		}
	}

	private void colorButtons(int winner) {
		switch (winner) {
		case 0, 1 -> Arrays.stream(buttons).forEach(x -> Arrays.stream(x).forEach(y -> {
			if (y.getText().equals(winner == 0 ? "X" : winner == 1 ? "O" : "#")) {
				y.setForeground(Color.green);
			} else {
				y.setForeground(Color.red);
			}
		}));
		case 2 -> Arrays.stream(buttons).forEach(x -> Arrays.stream(x).forEach(y -> y.setForeground(Color.orange)));
		}
	}

	private void updateTitle() {
		String curTurn = player == 0 ? "X" : "O";
		setTitle("XO [" + pointsX + ":" + pointsO + "] - Its " + curTurn + "'s turn");
	}
	
	private void resetMiddleButton() {
		buttons[1][1].setFont((Font) olds[0]);
		buttons[1][1].setForeground((Color) olds[1]);
		buttons[1][1].setText((String) olds[2]);
	}

	private void reset() {
		if (showResetText)
			resetMiddleButton();
		
		if (!running) {
			completeReset();
		}
		if (!(winner == 0 ? "X" : winner == 1 ? "O" : "#").equals("#"))
			player = player == 0 ? winnerContinuesPlaying ? 1 : 0 : winnerContinuesPlaying ? 0 : 1;
		winner = -1;
		resetCounter = 0;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons[x][y].setText("");
				buttons[x][y].setForeground(Color.black);
			}
		}
		resetMode = false;
		updateTitle();
	}

	private void completeReset() {
		winner = -1;
		player = 0;
		running = true;
		resetCounter = 0;
		pointsX = 0;
		pointsO = 0;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons[x][y].setText("");
				buttons[x][y].setForeground(Color.black);
			}
		}

		String[] values = Settings.readSettings();
		endGameAt = Integer.parseInt(values[0]);
		resetTimes = Integer.parseInt(values[1]);
		resetDelay = Integer.parseInt(values[2]);
		winnerContinuesPlaying = Boolean.parseBoolean(values[3]);
		showShadow = Boolean.parseBoolean(values[4]);
		showResetText = Boolean.parseBoolean(values[5]);

		setTitle("XO [0:0] - Its X's turn");
		checkSubMenu(2);
	}

	private void startResetThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					resetCounter = 0;
					try {
						Thread.sleep(resetDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void resizeSetting() {
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				int curSize = (int) getSize().getWidth();
				Arrays.stream(buttons).forEach(
						x -> Arrays.stream(x).forEach(y -> y.setFont(new Font("Arial", Font.PLAIN, curSize / 5))));
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	public MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu settings = new Menu("Settings");

		Menu subMenuEndGameAt = new Menu("Ending Game At");
		String[] names = { "1", "2", "3", "5", "10", "Endless" };
		CheckboxMenuItem[] items = new CheckboxMenuItem[names.length];

		for (int i = 0; i < items.length; i++) {
			items[i] = new CheckboxMenuItem(names[i]);
			if (items[i].getLabel().equals(String.valueOf(endGameAt))
					|| (endGameAt == Integer.MAX_VALUE && items[i].getLabel().equals("Endless"))) {
				items[i].setState(true);
			}
			subMenuEndGameAt.add(items[i]);
		}

		for (int i = 0; i < subMenuEndGameAt.getItemCount(); i++) {
			CheckboxMenuItem curItem = (CheckboxMenuItem) subMenuEndGameAt.getItem(i);
			curItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					int newEndGameAt;
					try {
						newEndGameAt = Integer.parseInt(curItem.getLabel());
					} catch (NumberFormatException exception) {
						newEndGameAt = Integer.MAX_VALUE;
					}
					if (pointsX < newEndGameAt && pointsO < newEndGameAt) {
						endGameAt = newEndGameAt;
						Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
						for (int i = 0; i < subMenuEndGameAt.getItemCount(); i++) {
							CheckboxMenuItem curItem = (CheckboxMenuItem) subMenuEndGameAt.getItem(i);
							curItem.setState(false);
						}
						curItem.setState(true);
					}
				}
			});
		}

		Menu reset = new Menu("Reset");

		String[] resetNames = { "1", "2", "3", "4", "5" };

		Menu resetTimesMenu = new Menu("Reset Times");
		CheckboxMenuItem[] itemsTimesMenu = new CheckboxMenuItem[resetNames.length];
		for (int i = 0; i < itemsTimesMenu.length; i++) {
			itemsTimesMenu[i] = new CheckboxMenuItem(names[i]);
			if (itemsTimesMenu[i].getLabel().equals(String.valueOf(resetTimes))) {
				itemsTimesMenu[i].setState(true);
			}
			resetTimesMenu.add(itemsTimesMenu[i]);
		}
		for (int i = 0; i < resetTimesMenu.getItemCount(); i++) {
			CheckboxMenuItem curItem = (CheckboxMenuItem) resetTimesMenu.getItem(i);
			curItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					resetTimes = Integer.parseInt(curItem.getLabel());
					Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
					for (int i = 0; i < resetTimesMenu.getItemCount(); i++) {
						CheckboxMenuItem curItem = (CheckboxMenuItem) resetTimesMenu.getItem(i);
						curItem.setState(false);
					}
					curItem.setState(true);
				}
			});
		}

		Menu resetDelayMenu = new Menu("Reset Delay");
		CheckboxMenuItem[] itemsDelayMenu = new CheckboxMenuItem[resetNames.length];
		for (int i = 0; i < itemsTimesMenu.length; i++) {
			int times = Integer.parseInt(names[i]) * 1000;
			itemsDelayMenu[i] = new CheckboxMenuItem(names[i] + " Sekunden");
			if (times == resetDelay) {
				itemsDelayMenu[i].setState(true);
			}
			resetDelayMenu.add(itemsDelayMenu[i]);
		}
		for (int i = 0; i < resetDelayMenu.getItemCount(); i++) {
			CheckboxMenuItem curItem = (CheckboxMenuItem) resetDelayMenu.getItem(i);
			curItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					resetDelay = Integer.parseInt(curItem.getLabel().substring(0, curItem.getLabel().length() - 9))
							* 1000;
					Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
					for (int i = 0; i < resetDelayMenu.getItemCount(); i++) {
						CheckboxMenuItem curItem = (CheckboxMenuItem) resetDelayMenu.getItem(i);
						curItem.setState(false);
					}
					curItem.setState(true);
				}
			});
		}
		
		CheckboxMenuItem resetText = new CheckboxMenuItem("Show Reset Text");
		resetText.setState(showResetText);
		resetText.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showResetText = !showResetText;
				Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
			}
		});

		reset.add(resetTimesMenu);
		reset.add(resetDelayMenu);
		reset.add(resetText);

		CheckboxMenuItem winnerContinuesPlayingItem = new CheckboxMenuItem("Winner Continues Playing");
		winnerContinuesPlayingItem.setState(winnerContinuesPlaying);
		winnerContinuesPlayingItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				winnerContinuesPlaying = !winnerContinuesPlaying;
				Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
			}
		});

		CheckboxMenuItem signShadowItem = new CheckboxMenuItem("Show Sign Shadow");
		signShadowItem.setState(showShadow);
		signShadowItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showShadow = !showShadow;
				Settings.writeSettings(endGameAt, resetTimes, resetDelay, winnerContinuesPlaying, showShadow, showResetText);
			}
		});

		Menu resetButtonMenu = new Menu("Reset");
		MenuItem resetButton = new MenuItem("Reset Game");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				completeReset();
			}
		});
		resetButtonMenu.add(resetButton);

		menuBar.add(resetButtonMenu);
		settings.add(subMenuEndGameAt);
		settings.add(reset);
		settings.add(winnerContinuesPlayingItem);
		settings.add(signShadowItem);
		menuBar.add(settings);
		return menuBar;
	}

	public void checkSubMenu(int state) {
		Menu subMenuEndGameAt = (Menu) getMenuBar().getMenu(1).getItem(0);
		for (int i = 0; i < subMenuEndGameAt.getItemCount(); i++) {
			CheckboxMenuItem curItem = (CheckboxMenuItem) subMenuEndGameAt.getItem(i);
			try {
				if (((pointsX >= Integer.parseInt(curItem.getLabel()) || pointsO >= Integer.parseInt(curItem.getLabel())
						|| state == 1)) && state != 2) {
					curItem.setEnabled(false);
				}
			} catch (Exception e) {
			}
			if (state == 2) {
				curItem.setEnabled(true);
			}
		}
	}

	public static void main(String[] args) {
		new XO();
	}

}