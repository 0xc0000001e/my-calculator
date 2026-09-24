import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WindowApp extends JFrame {

    private JTextField formulaInput;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public WindowApp() {
        setTitle("Калькулятор функций");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // По центру экрана
        setLayout(new BorderLayout(10, 10));

        // Верхняя панель ввода
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("f(x) ="));

        formulaInput = new JTextField("(1/4)x^3", 15);
        topPanel.add(formulaInput);

        JButton calcButton = new JButton("Рассчитать");
        topPanel.add(calcButton);

        add(topPanel, BorderLayout.NORTH);

        // Таблица результатов
        String[] columnNames = {"x", "y = f(x)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Логика кнопки
        calcButton.addActionListener(e -> calculate());

        setVisible(true);
    }

    private void calculate() {
        String formula = formulaInput.getText().trim();
        tableModel.setRowCount(0); // Очистить таблицу

        double startX = -3.0;
        double endX = 3.0;
        double step = 0.5;

        for (double x = startX; x <= endX + 1e-9; x += step) {
            try {
                MathFunctionParser parser = new MathFunctionParser(formula, x);
                double y = parser.parse();
                tableModel.addRow(new Object[]{String.format("%.2f", x), String.format("%.4f", y)});
            } catch (ArithmeticException ex) {
                tableModel.addRow(new Object[]{String.format("%.2f", x), "Не сущ."});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка в формуле: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WindowApp::new);
    }
}