import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    static {
        System.loadLibrary("libCheckersUTP");
    }

    private int selectedX = -1;
    private int selectedY = -1;
    public static final int PLAYER_ONE_PIECE = 1;
    public static final int PLAYER_TWO_PIECE = 2;
    public static final int PLAYER_ONE_CHOSEN = 3; // Chosen piece for Player 1
    public static final int PLAYER_TWO_CHOSEN = 4; // Chosen piece for Player 2
    public static final int PLAYER_ONE_KING = 5;   // King for Player 1
    public static final int PLAYER_TWO_KING = 6;   // King for Player 2

    private int currentPlayer = PLAYER_ONE_PIECE;

    private CheckersJNI game;
    private CheckerboardPanel boardPanel;

    public static class CheckersJNI {
        public native void initializeBoard();
        public native int[][] getBoardState();
        public native void selectPiece(int x, int y, int player);
        public native void deselectPiece();
        public native boolean moveSelectedPiece(int endX, int endY, int player);
    }

    public Main() {
        game = new CheckersJNI();
        game.initializeBoard();

        // Fetch initial board state
        boardPanel = new CheckerboardPanel(game.getBoardState());
        add(boardPanel);

        initializeUI();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ESCAPE) {
                    game.deselectPiece();
                    selectedX = -1;
                    selectedY = -1;
                    updateBoardUI();
                }
            }
        });
    }

    private void initializeUI() {
        setTitle("Checkers Game");
        setSize(400, 400);
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();

        // Add MouseListener for selection and movement
        boardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                int x = evt.getY() / 50; // Convert pixel location to board coordinates
                int y = evt.getX() / 50;

                int[][] boardState = game.getBoardState();
                if (boardState[x][y] == currentPlayer || boardState[x][y] == currentPlayer + 4) { // Check for King too
                    // Select piece if it belongs to the current player
                    game.selectPiece(x, y, currentPlayer);
                    selectedX = x;
                    selectedY = y;
                    boardPanel.setSelectedPiece(x, y); // Highlight the selected piece
                    updateBoardUI();
                } else if (selectedX != -1 && selectedY != -1) {
                    // Try to move selected piece if a piece has already been selected
                    if (game.moveSelectedPiece(x, y, currentPlayer)) {
                        switchTurns();
                        selectedX = -1;
                        selectedY = -1;
                        boardPanel.setSelectedPiece(-1, -1); // Clear selection
                        updateBoardUI();
                    }
                }
            }
        });
    }

    private void switchTurns() {
        // Toggle current player
        currentPlayer = (currentPlayer == PLAYER_ONE_PIECE) ? PLAYER_TWO_PIECE : PLAYER_ONE_PIECE;
    }

    private void updateBoardUI() {
        int[][] boardState = game.getBoardState(); // Fetch updated board state from C++
        boardPanel.setBoardState(boardState); // Update board state in CheckerboardPanel
    }

    public static void main(String[] args) {
        new Main();
    }
}

class CheckerboardPanel extends JPanel {
    private int[][] boardState;
    private int selectedX = -1;
    private int selectedY = -1;

    public CheckerboardPanel(int[][] boardState) {
        this.boardState = boardState;
        setPreferredSize(new Dimension(400, 400));
    }

    public void setBoardState(int[][] newBoardState) {
        this.boardState = newBoardState;
        repaint(); // Repaint to reflect the updated board state
    }

    public void setSelectedPiece(int x, int y) {
        this.selectedX = x;
        this.selectedY = y;
        repaint(); // Repaint to show selection
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(j * 50, i * 50, 50, 50);
            }
        }
    }

    private void drawPieces(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j] == Main.PLAYER_ONE_PIECE) {
                    g.setColor(Color.RED);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 1
                } else if (boardState[i][j] == Main.PLAYER_TWO_PIECE) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 2
                } else if (boardState[i][j] == Main.PLAYER_ONE_KING) {
                    g.setColor(Color.RED);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 1 King
                    g.setColor(Color.BLUE);
                    g.drawOval(j * 50 + 10, i * 50 + 10, 30, 30); // King border
                } else if (boardState[i][j] == Main.PLAYER_TWO_KING) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 2 King
                    g.setColor(Color.BLUE);
                    g.drawOval(j * 50 + 10, i * 50 + 10, 30, 30); // King border
                } else if (boardState[i][j] == Main.PLAYER_ONE_CHOSEN) {
                    g.setColor(Color.RED);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 1 chosen
                    g.setColor(Color.YELLOW); // Highlight chosen with a yellow border
                    g.drawOval(j * 50 + 10, i * 50 + 10, 30, 30);
                } else if (boardState[i][j] == Main.PLAYER_TWO_CHOSEN) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * 50 + 10, i * 50 + 10, 30, 30); // Player 2 chosen
                    g.setColor(Color.YELLOW); // Highlight chosen with a yellow border
                    g.drawOval(j * 50 + 10, i * 50 + 10, 30, 30);
                }
            }
        }
    }
}