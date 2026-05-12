package ui;

import core.Direction;
import core.Game;
import core.GameMap;
import core.Inventory;
import core.Item;
import core.Player;
import core.Position;
import core.Recipe;
import core.Resource;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class GameFrame extends JFrame {
    private static final Color BG = new Color(13, 17, 23);
    private static final Color BG2 = new Color(22, 27, 34);
    private static final Color BG3 = new Color(28, 33, 40);
    private static final Color BORDER = new Color(48, 54, 61);
    private static final Color GOLD = new Color(226, 185, 111);
    private static final Color GOLD2 = new Color(245, 215, 142);
    private static final Color DIM = new Color(110, 118, 129);
    private static final Color GREEN = new Color(63, 185, 80);
    private static final Color RED = new Color(248, 81, 73);
    private static final Color AMBER = new Color(210, 153, 34);

    private final Supplier<Game> gameFactory;
    private Game game;

    private final MapPanel mapPanel = new MapPanel();
    private final JLabel levelLabel = label("LEVEL 1 / 10", 12, GOLD2);
    private final JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final JLabel resourceLabel = label("none", 12, GOLD);
    private final JPanel itemPanel = new JPanel();
    private final JLabel equippedLabel = label("Equipped: nothing", 12, GOLD);
    private final JTextArea logArea = new JTextArea(6, 22);

    public GameFrame(Supplier<Game> gameFactory) {
        this.gameFactory = gameFactory;
        this.game = gameFactory.get();

        setTitle("Dungeon Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(BG);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createHint(), BorderLayout.SOUTH);

        setupKeyBindings();
        addLog("Welcome, adventurer. Find the key and reach the door.");
        refresh();

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(880, 620));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 4, 18));

        JLabel title = label("DUNGEON CRAWLER", 22, GOLD2);
        title.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        header.add(levelLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));

        JPanel mapWrap = new JPanel(new BorderLayout());
        mapWrap.setBackground(BG2);
        mapWrap.setBorder(BorderFactory.createLineBorder(BORDER));
        mapWrap.add(mapPanel, BorderLayout.CENTER);
        center.add(mapWrap, BorderLayout.CENTER);
        center.add(createSideBar(), BorderLayout.EAST);
        return center;
    }

    private JPanel createSideBar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(BG);
        side.setPreferredSize(new Dimension(250, 520));

        side.add(box("KEY STATUS", keyPanel));
        side.add(box("RESOURCES", resourceLabel));

        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(BG2);
        JPanel itemBox = new JPanel();
        itemBox.setLayout(new BoxLayout(itemBox, BoxLayout.Y_AXIS));
        itemBox.setBackground(BG2);
        itemBox.add(itemPanel);
        itemBox.add(equippedLabel);
        side.add(box("ITEMS", itemBox));

        side.add(box("CONTROLS", createControls()));

        logArea.setEditable(false);
        logArea.setFocusable(false);
        logArea.setBackground(BG);
        logArea.setForeground(DIM);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(BG);
        side.add(box("LOG", scroll));
        return side;
    }

    private JPanel createControls() {
        JPanel wrapper = new JPanel(new BorderLayout(4, 8));
        wrapper.setBackground(BG2);

        JPanel dPad = new JPanel(new GridLayout(3, 3, 4, 4));
        dPad.setBackground(BG2);
        dPad.add(new JLabel());
        dPad.add(button("▲", () -> move(Direction.UP)));
        dPad.add(new JLabel());
        dPad.add(button("◄", () -> move(Direction.LEFT)));
        dPad.add(button("▼", () -> move(Direction.DOWN)));
        dPad.add(button("►", () -> move(Direction.RIGHT)));
        dPad.add(new JLabel());
        dPad.add(button("USE", this::useEquippedItem));
        dPad.add(button("CRAFT", this::openCraftingDialog));

        JPanel row = new JPanel(new GridLayout(1, 2, 4, 0));
        row.setBackground(BG2);
        row.add(button("RESET", this::restartGame));
        row.add(button("QUIT", this::dispose));

        wrapper.add(dPad, BorderLayout.CENTER);
        wrapper.add(row, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel createHint() {
        JLabel hint = label("WASD / ARROWS = move   •   E = use   •   C = craft   •   1/2 = equip", 11, DIM);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return hint;
    }

    private JPanel box(String title, java.awt.Component content) {
        JPanel box = new JPanel(new BorderLayout(0, 8));
        box.setBackground(BG2);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel titleLabel = label(title, 10, DIM);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 11));
        box.add(titleLabel, BorderLayout.NORTH);
        box.add(content, BorderLayout.CENTER);

        JPanel gap = new JPanel(new BorderLayout());
        gap.setBackground(BG);
        gap.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        gap.add(box, BorderLayout.CENTER);
        return gap;
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(BG);
        button.setForeground(GOLD);
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        button.addActionListener(e -> {
            action.run();
            mapPanel.requestFocusInWindow();
        });
        return button;
    }

    private static JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
        return label;
    }

    private void setupKeyBindings() {
        bind("W", () -> move(Direction.UP));
        bind("UP", () -> move(Direction.UP));
        bind("S", () -> move(Direction.DOWN));
        bind("DOWN", () -> move(Direction.DOWN));
        bind("A", () -> move(Direction.LEFT));
        bind("LEFT", () -> move(Direction.LEFT));
        bind("D", () -> move(Direction.RIGHT));
        bind("RIGHT", () -> move(Direction.RIGHT));
        bind("E", this::useEquippedItem);
        bind("C", this::openCraftingDialog);
        bind("DIGIT1", () -> equipSlot(1));
        bind("DIGIT2", () -> equipSlot(2));
        bind("ESCAPE", this::dispose);
    }

    private void bind(String key, Runnable action) {
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(key), key);
        getRootPane().getActionMap().put(key, new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void move(Direction direction) {
        if (game.isGameComplete()) return;

        GameMap map = game.getCurrentMap();
        Position oldPosition = game.getPlayer().getPosition();
        Position nextPosition = oldPosition.move(direction.getDRow(), direction.getDCol());
        char target = map.isInside(nextPosition) ? map.getTile(nextPosition) : '#';
        String oldMapId = game.getCurrentMapId();

        game.movePlayer(direction);

        if (game.isGameComplete()) {
            addLog("Victory! You cleared all 10 levels.");
            showVictoryDialog();
        } else if (!oldMapId.equals(game.getCurrentMapId())) {
            addLog("Level complete. Entered " + game.getCurrentMapId() + ".");
        } else if (target == 'R') {
            addLog("Picked up Rock.");
        } else if (target == 'S') {
            addLog("Picked up Stick.");
        } else if (target == 'K') {
            addLog("You found the key.");
        } else if (target == '1' || target == '2' || target == '3') {
            addLog("Key part " + target + " collected.");
        } else if (target == 'D') {
            addLog(game.getInventory().hasKeyForMap(oldMapId) ? "Door unlocked." : "The door is locked.");
        } else if (target == 'W') {
            addLog("Breakable wall. Equip a Pickaxe or Bomb and press E.");
        }

        refresh();
    }

    private void useEquippedItem() {
        if (game.isGameComplete()) return;
        Item equipped = game.getInventory().getEquippedItem();
        if (equipped == null) {
            addLog("Nothing equipped. Craft something first.");
            refresh();
            return;
        }

        int before = game.getInventory().getAllItems().getOrDefault(equipped, 0);
        game.useItem();
        int after = game.getInventory().getAllItems().getOrDefault(equipped, 0);

        if (after < before) {
            addLog(equipped.getName() + " used.");
        } else {
            addLog("No breakable wall next to you.");
        }
        refresh();
    }

    private void openCraftingDialog() {
        JDialog dialog = new JDialog(this, "Crafting", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG2);

        JPanel list = new JPanel(new GridBagLayout());
        list.setBackground(BG2);
        list.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 0);

        List<Recipe> recipes = game.getCraftingRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            JButton recipeButton = button(recipe.describe(), () -> { });
            recipeButton.setEnabled(game.canCraft(recipe));
            recipeButton.addActionListener(e -> {
                int index = recipes.indexOf(recipe);
                if (game.craft(index)) {
                    addLog("Crafted: " + recipe.getResult().getName() + ".");
                    refresh();
                    dialog.dispose();
                }
            });
            gbc.gridy = i;
            list.add(recipeButton, gbc);
        }

        JLabel resources = label(resourceText(), 12, GOLD);
        resources.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        JButton close = button("CLOSE", dialog::dispose);
        close.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        dialog.add(resources, BorderLayout.NORTH);
        dialog.add(list, BorderLayout.CENTER);
        dialog.add(close, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(360, Math.max(220, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void equipSlot(int slot) {
        if (game.equipBySlot(slot)) {
            addLog("Slot " + slot + " equipped.");
            refresh();
        }
    }

    private void restartGame() {
        game = gameFactory.get();
        logArea.setText("");
        addLog("Game restarted.");
        refresh();
    }

    private void showVictoryDialog() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "You conquered all 10 levels. Play again?",
                "Victory",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) restartGame();
    }

    private void refresh() {
        GameMap map = game.getCurrentMap();
        int level = parseLevelNumber(map.getId());
        levelLabel.setText("LEVEL " + level + " / 10");
        updateKeyPanel();
        resourceLabel.setText(resourceText());
        updateItemPanel();
        mapPanel.revalidate();
        mapPanel.repaint();
    }

    private int parseLevelNumber(String mapId) {
        try {
            return Integer.parseInt(mapId.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void updateKeyPanel() {
        keyPanel.removeAll();
        keyPanel.setBackground(BG2);
        Inventory inventory = game.getInventory();
        String mapId = game.getCurrentMapId();

        if (inventory.hasKeyForMap(mapId)) {
            JLabel complete = label("KEY COMPLETE", 12, GREEN);
            complete.setBorder(BorderFactory.createLineBorder(GREEN));
            keyPanel.add(complete);
        } else {
            Set<Character> parts = inventory.getKeyPartsForMap(mapId);
            for (char part : new char[]{'1', '2', '3'}) {
                JLabel partLabel = label(String.valueOf(part), 12, parts.contains(part) ? AMBER : DIM);
                partLabel.setHorizontalAlignment(SwingConstants.CENTER);
                partLabel.setPreferredSize(new Dimension(28, 26));
                partLabel.setBorder(BorderFactory.createLineBorder(parts.contains(part) ? AMBER : BORDER));
                keyPanel.add(partLabel);
            }
        }
        keyPanel.revalidate();
        keyPanel.repaint();
    }

    private String resourceText() {
        Map<Resource, Integer> resources = game.getInventory().getAllResources();
        if (resources.isEmpty()) return "Resources: none";
        StringBuilder text = new StringBuilder("Resources: ");
        resources.forEach((resource, amount) -> text.append(resource.name()).append(" x").append(amount).append("  "));
        return text.toString();
    }

    private void updateItemPanel() {
        itemPanel.removeAll();
        itemPanel.setBackground(BG2);
        Map<Item, Integer> items = game.getInventory().getAllItems();
        Item equipped = game.getInventory().getEquippedItem();

        if (items.isEmpty()) {
            itemPanel.add(label("none", 12, DIM));
        } else {
            int slot = 1;
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                Item item = entry.getKey();
                String text = "[" + slot + "] " + item.getName() + " x" + entry.getValue();
                JButton itemButton = button(text, () -> {
                    game.getInventory().equipItem(item);
                    addLog(item.getName() + " equipped.");
                    refresh();
                });
                if (item == equipped) {
                    itemButton.setForeground(GOLD2);
                    itemButton.setBorder(BorderFactory.createLineBorder(GOLD));
                }
                itemPanel.add(itemButton);
                slot++;
            }
        }

        equippedLabel.setText("Equipped: " + (equipped == null ? "nothing" : equipped.getName()));
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private void addLog(String message) {
        logArea.insert(message + "\n", 0);
    }

    private class MapPanel extends JPanel {
        private final int cellSize = 22;
        private final Font mapFont = new Font(Font.MONOSPACED, Font.BOLD, 18);

        MapPanel() {
            setBackground(BG);
            setFocusable(true);
        }

        @Override
        public Dimension getPreferredSize() {
            GameMap map = game.getCurrentMap();
            return new Dimension(map.getCols() * cellSize, map.getRows() * cellSize);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(mapFont);
            FontMetrics metrics = g2.getFontMetrics();

            GameMap map = game.getCurrentMap();
            Player player = game.getPlayer();
            Position playerPos = player.getPosition();

            for (int row = 0; row < map.getRows(); row++) {
                for (int col = 0; col < map.getCols(); col++) {
                    Position pos = new Position(row, col);
                    char ch = map.getTile(pos);
                    boolean isPlayer = playerPos.row() == row && playerPos.col() == col;
                    char drawChar = isPlayer ? '@' : ch;

                    int x = col * cellSize;
                    int y = row * cellSize;
                    g2.setColor(backgroundFor(drawChar));
                    g2.fillRect(x, y, cellSize, cellSize);
                    g2.setColor(foregroundFor(drawChar));
                    String text = String.valueOf(drawChar);
                    int tx = x + (cellSize - metrics.stringWidth(text)) / 2;
                    int ty = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
                    g2.drawString(text, tx, ty);
                }
            }
            g2.dispose();
        }

        private Color backgroundFor(char ch) {
            return switch (ch) {
                case '#' -> new Color(10, 15, 23);
                case 'W' -> new Color(17, 24, 39);
                case 'D' -> new Color(26, 10, 46);
                case 'K', '1', '2', '3' -> new Color(28, 18, 0);
                case '@' -> new Color(28, 22, 0);
                default -> BG;
            };
        }

        private Color foregroundFor(char ch) {
            return switch (ch) {
                case '#' -> new Color(30, 42, 58);
                case 'W' -> new Color(71, 85, 105);
                case 'D' -> new Color(168, 85, 247);
                case 'K', '1', '2', '3' -> new Color(217, 119, 6);
                case 'R' -> new Color(148, 163, 184);
                case 'S' -> new Color(146, 64, 14);
                case '@' -> GOLD2;
                default -> BG3;
            };
        }
    }
}
