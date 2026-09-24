import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Таблица значений функции ===");
        System.out.print("Введите функцию f(x): ");
        String input = scanner.nextLine().trim();

        double startX = -3.0; // start
        double endX = 3.0;    // end
        double step = 0.5;    // step

        System.out.println("\n-----------------------------");
        System.out.printf("| %10s | %10s |\n", "x", "y = f(x)");
        System.out.println("-----------------------------");

        // Цикл от -3 до 3
        for (double x = startX; x <= endX + 1e-9; x += step) {
            try {
                MathFunctionParser parser = new MathFunctionParser(input, x);
                double y = parser.parse();

                System.out.printf("| %10.2f | %10.4f |\n", x, y);
            } catch (ArithmeticException e) {
                System.out.printf("| %10.2f | %10s |\n", x, "Не сущ.");
            } catch (RuntimeException e) {
                System.out.println("Ошибка в формуле: " + e.getMessage());
                break;
            }
        }

        System.out.println("-----------------------------");
        scanner.close();
    }
}

// Класс парсера находится в том же файле под классом Main
class MathFunctionParser {
    private final String str;
    private int pos = -1;
    private int ch;
    private final double xValue;

    public MathFunctionParser(String str, double xValue) {
        this.str = str;
        this.xValue = xValue;
    }

    private void nextChar() {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public double parse() {
        nextChar();
        double result = parseExpression();
        if (pos < str.length()) {
            throw new RuntimeException("Неожиданный символ: " + (char) ch);
        }
        return result;
    }

    private double parseExpression() {
        double value = parseTerm();
        for (;;) {
            if      (eat('+')) value += parseTerm();
            else if (eat('-')) value -= parseTerm();
            else return value;
        }
    }

    private double parseTerm() {
        double value = parseFactor();
        for (;;) {
            if (eat('*')) {
                value *= parseFactor();
            } else if (eat('/')) {
                double divisor = parseFactor();
                if (divisor == 0) throw new ArithmeticException("Деление на ноль!");
                value /= divisor;
            } else if (ch == '(' || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                value *= parseFactor();
            } else {
                return value;
            }
        }
    }

    private double parseFactor() {
        if (eat('+')) return +parseFactor();
        if (eat('-')) return -parseFactor();

        double value;
        int startPos = this.pos;

        if (eat('(')) {
            value = parseExpression();
            if (!eat(')')) throw new RuntimeException("Пропущена закрывающая скобка ')'");
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            value = Double.parseDouble(str.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String name = str.substring(startPos, this.pos);

            if (name.equals("x")) {
                value = xValue;
            } else if (name.equals("pi")) {
                value = Math.PI;
            } else if (name.equals("e")) {
                value = Math.E;
            } else {
                if (!eat('(')) throw new RuntimeException("Ожидалась '(' после функции " + name);
                value = parseExpression();
                if (!eat(')')) throw new RuntimeException("Ожидалась ')' после " + name);

                value = switch (name) {
                    case "sin"  -> Math.sin(value);
                    case "cos"  -> Math.cos(value);
                    case "tan"  -> Math.tan(value);
                    case "sqrt" -> Math.sqrt(value);
                    case "abs"  -> Math.abs(value);
                    case "ln"   -> Math.log(value);
                    default     -> throw new RuntimeException("Неизвестная функция: " + name);
                };
            }
        } else {
            throw new RuntimeException("Неожиданный символ: " + (char) ch);
        }

        if (eat('^')) {
            value = Math.pow(value, parseFactor());
        }

        return value;
    }
}