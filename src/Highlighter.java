import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class Highlighter {

    private final Timer debounceTimer;
    private static final int delay = 100;
    private JTextPane textPane;
    private JLabel statusLabel;

    public Highlighter() {
        debounceTimer = new Timer(delay, e -> applyHighlighting());
        debounceTimer.setRepeats(false);
    }

    public void start() {
        JFrame frame = new JFrame("Real-Time Syntax Highlighter");
        textPane = new JTextPane();

        // Set font size here (change 16 to whatever you like)
        textPane.setFont(new Font("Consolas", Font.PLAIN, 20));

        JScrollPane scrollPane = new JScrollPane(textPane);

        // Status label for errors
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(255,70,50));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Layout
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        textPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                debounceTimer.restart();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                debounceTimer.restart();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                debounceTimer.restart();
            }
        });
    }

    private void applyHighlighting() {
        String text = textPane.getText();
        StyledDocument doc = textPane.getStyledDocument();

        // Reset all text to black
        doc.setCharacterAttributes(0, text.length(), getStyle(Color.BLACK), true);

        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            Color color = switch (token.getType()) {
                case "KEYWORD" -> new Color(64, 64, 231);
                case "IDENTIFIER" -> new Color(0, 128, 0);
                case "NUMBER" -> new Color(228, 71, 228);
                case "OPERATOR" -> new Color(200, 0, 0);
                case "SYMBOL" -> new Color(254, 133, 76);
                default -> Color.BLACK;
            };
            doc.setCharacterAttributes(token.startPos, token.len, getStyle(color), true);
        }

        // Run parser and show errors in status bar
        Parser parser = new Parser(tokens);
        String error = parser.parseFunc() ? "No syntax errors." : parser.getLastError();
        statusLabel.setText(error);
    }

    private SimpleAttributeSet getStyle(Color color) {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        return attr;
    }
}
