import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Platform extends JPanel {

    public static int NUM_ROWS = 10;
    public static int NUM_COLS = 10;

    public static int percent = 25;
    private static Color[][] terrainGrid;

    public static int PREFERRED_GRID_SIZE_PIXELS = 600 / NUM_ROWS;

    private static int[][] maps;

    public Platform() {
        maps = new int[NUM_ROWS][NUM_COLS];
        terrainGrid = new Color[NUM_ROWS][NUM_COLS];
        setForm(NUM_ROWS, NUM_COLS, percent);
    }

    public void setForm(int num_rows, int num_cols, int per) {
        NUM_ROWS = num_rows;
        NUM_COLS = num_cols;
        percent = per;
        PREFERRED_GRID_SIZE_PIXELS = 600/ num_rows;
        maps = new int[NUM_ROWS][NUM_COLS];
        terrainGrid = new Color[NUM_ROWS][NUM_COLS];
        for (int i = 0; i < NUM_ROWS ; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                Random r = new Random();
                int randomInt = r.nextInt(100) + 1;
                if (randomInt <= percent) {
                    maps[i][j] = 0;
                } else {
                    maps[i][j] = 1;
                }
            }
        }
        createGraph();
        setTerrainGrid();
        int preferredWidth = NUM_COLS * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = NUM_ROWS * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        repaint();
    }
    public static void setTerrainGrid() {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (maps[i][j] == 1) {
                    terrainGrid[i][j] = Color.WHITE;
                } else {
                    terrainGrid[i][j] = Color.BLACK;
                }
            }
        }
    }
    static Graph graph = new Graph();
    public static void createGraph() {
        graph = new Graph();
        //Add nodes
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (maps[i][j] == 1) {
                    Node node = new Node(i, j);
                    node.setId(i * NUM_ROWS + j);
                    graph.addNode(node);
                }
            }
        }

        //Add edges
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (i > 0 && maps[i - 1][j] == 1) {
                    graph.addEdge(i * NUM_ROWS + j, (i - 1) * NUM_ROWS + (j), 1);
                }
                ;
                if (j > 0 && maps[i][j - 1] == 1) {
                    graph.addEdge(i * NUM_ROWS + j, (i) * NUM_ROWS + (j - 1), 1);
                }
                ;
                if (j < NUM_COLS - 1 && maps[i][j + 1] == 1) {
                    graph.addEdge(i * NUM_ROWS + j, (i) * NUM_ROWS + (j + 1), 1);
                }
                ;
                if (i < NUM_ROWS - 1 && maps[i + 1][j] == 1) {
                    graph.addEdge(i * NUM_ROWS + j, (i + 1) * NUM_ROWS + (j), 1);
                }
                ;
            }
        }
    }
    public static int findPath(int startId, int targetId) {
        graph.aStar(startId, targetId);
        LinkedList<Node> nodes = graph.getPath(targetId);

        if ((nodes.size() == 0 || nodes.size() == 1) && startId != targetId) {
            return -1;
        } else {
            for (int i = 0; i < nodes.size(); i++) {
                Node n = nodes.get(i);
                int x = (int) n.getX();
                int y = (int) n.getY();
                if (i == 0 || i == nodes.size() - 1) {
                    terrainGrid[x][y] = Color.RED;
                } else {
                    terrainGrid[x][y] = Color.YELLOW;
                }
            }
            return 0;
        }
    }

    public static int getID(int x, int y) {
        int i = (x / PREFERRED_GRID_SIZE_PIXELS);
        int j = (y / PREFERRED_GRID_SIZE_PIXELS);
        return (j*NUM_ROWS) + i;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Clear the board
        g.clearRect(0, 0, getWidth(), getHeight());

        // Draw the grid
        int rectWidth = getWidth() / NUM_COLS;
        int rectHeight = getHeight() / NUM_ROWS;

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                int x = i * rectWidth;
                int y = j * rectHeight;
                Color terrainColor = terrainGrid[i][j];
                g.setColor(terrainColor);
                g.fillRect(y, x, rectWidth, rectHeight);
            }
        }
    }

    public static void paintID(Graphics g, int id) {
        g.setColor(Color.RED);
        Node node = graph.getNode(id);
        if (node != null) {
            g.fillRect((int) node.getY() * PREFERRED_GRID_SIZE_PIXELS, (int) node.getX() * PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS);
        }
    }

    public static void reMovePaintID(Graphics g, int id) {
        g.setColor(Color.WHITE);
        Node node = graph.getNode(id);
        if (node != null) {
            g.fillRect((int) node.getY() * PREFERRED_GRID_SIZE_PIXELS, (int) node.getX() * PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS, PREFERRED_GRID_SIZE_PIXELS);
        }
    }

    static class CustomMouseListener implements MouseListener {
        private JTextField textField;
        private JPanel map;
        public CustomMouseListener(JPanel map, JTextField textField) {
            this.textField = textField;
            this.map = map;
        }
        public void mouseClicked(MouseEvent e) {
            if (map.getMouseListeners().length != 0) {
                map.removeMouseListener(map.getMouseListeners()[0]);
            }
            map.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    textField.setText(String.valueOf(getID(e.getX(), e.getY())));
                    paintID(map.getGraphics(), getID(e.getX(), e.getY()));
                }
                @Override
                public void mousePressed(MouseEvent e) {
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
            });
        }

        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Platform");
                frame.setSize(500, 500);
                frame.setLayout(new GridLayout(1, 2));

                Platform map = new Platform();
                map.setForeground(Color.red);
                frame.add(map);

                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                panel.setSize(1600, 1600);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 25;
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 4;
                JLabel create = new JLabel("CREATE MAPS", JLabel.CENTER);
                panel.add(create, gbc);

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 15;
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.gridwidth = 1;
                JLabel row = new JLabel("Enter Rows: ", JLabel.RIGHT);
                panel.add(row, gbc);

                gbc.gridx = 1;
                gbc.gridy = 1;
                final JTextField num_rows = new JTextField(10);
                panel.add(num_rows, gbc);

                gbc.gridx = 2;
                gbc.gridy = 1;
                JLabel col = new JLabel("Enter Cols: ", JLabel.CENTER);
                panel.add(col, gbc);

                gbc.gridx = 3;
                gbc.gridy = 1;
                final JTextField num_cols = new JTextField(10);
                panel.add(num_cols, gbc);

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 15;
                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel per = new JLabel("Enter Per:", JLabel.LEFT);
                panel.add(per, gbc);

                gbc.gridx = 1;
                gbc.gridy = 2;
                final JTextField num_per = new JTextField(10);
                panel.add(num_per, gbc);

                gbc.gridx = 3;
                gbc.gridy = 2;
                gbc.gridwidth = 3;
                final JButton create_btn = new JButton("Create");
                panel.add(create_btn, gbc);
                create_btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        map.setForm(Integer.parseInt(num_rows.getText()), Integer.parseInt(num_cols.getText()), Integer.parseInt(num_per.getText()));
                        frame.repaint();
                    }
                });

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 15;
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 4;
                JLabel test = new JLabel("A* ALORITHMS", JLabel.CENTER);
                panel.add(test, gbc);

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridwidth = 1;
                gbc.gridx = 0;
                gbc.gridy = 4;
                JLabel start = new JLabel("Start: ", JLabel.RIGHT);

                panel.add(start, gbc);
                gbc.gridx = 1;
                gbc.gridy = 4;
                final JTextField startID = new JTextField(10);
                panel.add(startID, gbc);

                final JTextField targetID = new JTextField(10);
                CustomMouseListener m1 = new CustomMouseListener(map,startID);
                CustomMouseListener m2 = new CustomMouseListener(map,targetID);

                startID.addMouseListener(m1);
                targetID.addMouseListener(m2);
                gbc.gridx = 2;
                gbc.gridy = 4;
                JLabel target = new JLabel("Target: ", JLabel.CENTER);
                panel.add(target, gbc);
                gbc.gridx = 3;
                gbc.gridy = 4;
                panel.add(targetID, gbc);

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 5;
                gbc.gridwidth = 1;
                JButton btstart = new JButton("Run");
                panel.add(btstart, gbc);

                JTextArea status = new JTextArea("Result: ", 3, 3);
                status.setBackground(Color.gray);
                status.setForeground(Color.WHITE);
                gbc.gridwidth = 3;
                gbc.gridx = 1;
                gbc.gridy = 5;
                panel.add(status, gbc);

                btstart.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setTerrainGrid();
                        boolean check1 = false;
                        boolean check2 = false;
                        int check = 0;
                        if (startID.getText().equals("")) {
                            check1 = true;
                        }
                        if (targetID.getText().equals("")) {
                            check2 = true;
                        }
                        String message = "";
                        if (check1) {
                            message += "Start ID is missing! Please choose ID!\n";
                            startID.setText("");
                            targetID.setText("");
                        }
                        if (check2) {
                            message += "Target ID is missing! Please choose ID!";
                            startID.setText("");
                            targetID.setText("");
                        }
                        status.setText(message);

                        if (!check1 && !check2) {
                            check = findPath(Integer.parseInt(startID.getText()), Integer.parseInt(targetID.getText()));
                            if (check == -1) {
                                status.setText("Cannot find the path!");
                                startID.setText("");
                                targetID.setText("");
                            } else {
                                status.setText("Result: The path was found!");
                            }
                            map.repaint();
                        }
                    }
                });

                frame.add(panel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
