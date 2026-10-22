package com.calculator.maybe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView display, history, modeView;
    private String expression = "";
    private boolean degreeMode = true;
    private boolean justCalculated = false;
    private double lastAnswer = 0;

    private final int bg = Color.rgb(15, 17, 29);
    private final int panel = Color.rgb(25, 29, 45);
    private final int number = Color.rgb(39, 44, 64);
    private final int function = Color.rgb(48, 54, 79);
    private final int accent = Color.rgb(123, 95, 255);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(167, 173, 198);

    private final String[][] keys = {
            {"AC", "DEL", "(", ")", "÷"},
            {"sin", "cos", "tan", "√", "x²"},
            {"7", "8", "9", "×", "^"},
            {"4", "5", "6", "−", "log"},
            {"1", "2", "3", "+", "ln"},
            {"0", ".", "π", "e", "="},
            {"∛", "xʸ", "n!", "%", "DEG"},
            {"1/x", "±", "abs", "RAD", "Ans"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildUI();
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        root.setPadding(dp(12), dp(6), dp(12), dp(6));
        root.setBackgroundColor(bg);

        TextView title = new TextView(this);
        title.setText("SCIENTIFIC CALCULATOR");
        title.setTextColor(muted);
        title.setTextSize(15);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.04f);
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(34)));

        modeView = new TextView(this);
        modeView.setText("SCIENTIFIC  •  DEG");
        modeView.setTextColor(accent);
        modeView.setTextSize(13);
        modeView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        root.addView(modeView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(25)));

        LinearLayout displayBox = new LinearLayout(this);
        displayBox.setOrientation(LinearLayout.VERTICAL);
        displayBox.setGravity(Gravity.CENTER_VERTICAL);
        displayBox.setPadding(dp(16), dp(8), dp(16), dp(8));
        displayBox.setBackground(round(panel, 22));

        LinearLayout.LayoutParams displayParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 2.1f);
        displayParams.setMargins(0, dp(5), 0, dp(8));
        root.addView(displayBox, displayParams);

        history = new TextView(this);
        history.setText("جاهز للحساب");
        history.setTextColor(muted);
        history.setTextSize(14);
        history.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        history.setSingleLine(true);
        displayBox.addView(history, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(30)));

        ScrollView resultScroll = new ScrollView(this);
        resultScroll.setFillViewport(false);
        resultScroll.setVerticalScrollBarEnabled(false);

        display = new TextView(this);
        display.setText("0");
        display.setTextColor(white);
        display.setTextSize(34);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        display.setSingleLine(true);
        display.setHorizontallyScrolling(true);
        resultScroll.addView(display, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        displayBox.addView(resultScroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        ScrollView keypadScroll = new ScrollView(this);
        keypadScroll.setFillViewport(false);
        keypadScroll.setVerticalScrollBarEnabled(false);
        keypadScroll.setClipToPadding(false);

        LinearLayout keypad = new LinearLayout(this);
        keypad.setOrientation(LinearLayout.VERTICAL);
        keypad.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        for (String[] row : keys) {
            addButtonRow(keypad, row);
        }

        keypadScroll.addView(keypad, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(keypadScroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 5.2f));

        setContentView(root);
    }

    private void addButtonRow(LinearLayout parent, String[] row) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(58));
        rowParams.setMargins(0, dp(1), 0, dp(1));
        parent.addView(rowLayout, rowParams);

        for (String key : row) {
            Button button = new Button(this);
            button.setText(key);
            button.setAllCaps(false);
            button.setTextSize(key.length() > 3 ? 12 : 16);
            button.setTextColor(white);
            button.setTypeface(Typeface.DEFAULT_BOLD);
            button.setPadding(0, 0, 0, 0);
            button.setMinWidth(0);
            button.setMinHeight(0);
            button.setStateListAnimator(null);
            button.setIncludeFontPadding(false);
            button.setGravity(Gravity.CENTER);

            int color = isFunction(key) ? function : number;
            if (key.equals("=")) color = accent;
            if (key.equals("AC") || key.equals("DEL")) {
                color = Color.rgb(107, 56, 83);
            }
            button.setBackground(round(color, 16));

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(0,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            buttonParams.setMargins(dp(3), dp(3), dp(3), dp(3));
            rowLayout.addView(button, buttonParams);
            button.setOnClickListener(v -> press(key));
        }
    }

    private boolean isFunction(String key) {
        return key.equals("sin") || key.equals("cos")
                || key.equals("tan") || key.equals("√")
                || key.equals("∛") || key.equals("x²")
                || key.equals("xʸ") || key.equals("^")
                || key.equals("log") || key.equals("ln")
                || key.equals("n!") || key.equals("%")
                || key.equals("π") || key.equals("e")
                || key.equals("1/x") || key.equals("±")
                || key.equals("abs") || key.equals("DEG")
                || key.equals("RAD") || key.equals("Ans");
    }

    private void press(String key) {
        if (key.equals("AC")) {
            expression = "";
            justCalculated = false;
            display.setText("0");
            history.setText("جاهز للحساب");
            return;
        }

        if (key.equals("DEL")) {
            if (justCalculated) {
                expression = "";
                justCalculated = false;
            } else if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
            }
            updateDisplay();
            return;
        }

        if (key.equals("DEG") || key.equals("RAD")) {
            degreeMode = key.equals("DEG");
            modeView.setText("SCIENTIFIC  •  " + (degreeMode ? "DEG" : "RAD"));
            Toast.makeText(this,
                    degreeMode ? "وضع الدرجات DEG" : "وضع الراديان RAD",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (key.equals("=")) {
            calculate();
            return;
        }

        if (key.equals("±")) {
            toggleSign();
            return;
        }

        if (key.equals("1/x")) {
            wrapExpression("1/(", ")");
            return;
        }

        if (key.equals("x²")) {
            append("^2");
            return;
        }

        if (key.equals("xʸ") || key.equals("^")) {
            append("^");
            return;
        }

        if (key.equals("√")) {
            append("sqrt(");
            return;
        }

        if (key.equals("∛")) {
            append("cbrt(");
            return;
        }

        if (key.equals("sin") || key.equals("cos") || key.equals("tan")
                || key.equals("log") || key.equals("ln") || key.equals("abs")) {
            append(key + "(");
            return;
        }

        if (key.equals("π")) {
            append("pi");
            return;
        }

        if (key.equals("e")) {
            append("e");
            return;
        }

        if (key.equals("n!")) {
            append("!");
            return;
        }

        if (key.equals("%")) {
            append("%");
            return;
        }

        if (key.equals("Ans")) {
            append(format(lastAnswer));
            return;
        }

        append(key);
    }

    private void append(String value) {
        if (justCalculated) {
            if (isOperator(value)) {
                justCalculated = false;
            } else {
                expression = "";
                justCalculated = false;
            }
        }
        expression += value;
        updateDisplay();
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("−") || s.equals("×")
                || s.equals("÷") || s.equals("^");
    }

    private void updateDisplay() {
        display.setText(expression.isEmpty() ? "0" : expression);
    }

    private void wrapExpression(String prefix, String suffix) {
        if (expression.isEmpty()) {
            Toast.makeText(this, "أدخل رقمًا أولًا", Toast.LENGTH_SHORT).show();
            return;
        }
        expression = prefix + expression + suffix;
        justCalculated = false;
        updateDisplay();
    }

    private void toggleSign() {
        if (expression.isEmpty()) {
            expression = "-";
        } else if (expression.startsWith("-(") && expression.endsWith(")")) {
            expression = expression.substring(2, expression.length() - 1);
        } else {
            expression = "-(" + expression + ")";
        }
        justCalculated = false;
        updateDisplay();
    }

    private void calculate() {
        if (expression.trim().isEmpty()) return;

        try {
            String original = expression;
            Parser parser = new Parser(expression, degreeMode);
            double result = parser.parse();

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new ArithmeticException("نتيجة غير صالحة");
            }

            lastAnswer = result;
            history.setText(original + " =");
            expression = format(result);
            display.setText(expression);
            justCalculated = true;

        } catch (Exception e) {
            history.setText("خطأ في العملية");
            Toast.makeText(this,
                    "تأكد من الأرقام والعمليات والأقواس",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String format(double value) {
        if (value == 0) return "0";
        if (Math.abs(value) >= 1e12 || Math.abs(value) < 1e-8) {
            return String.format(Locale.US, "%.8g", value);
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class Parser {
        private final String s;
        private final boolean degrees;
        private int pos = 0;

        Parser(String expression, boolean degreeMode) {
            s = expression.replace("×", "*")
                    .replace("÷", "/")
                    .replace("−", "-")
                    .replace(" ", "");
            degrees = degreeMode;
        }

        double parse() {
            double result = expression();
            if (pos < s.length()) {
                throw new IllegalArgumentException("إدخال غير متوقع");
            }
            return result;
        }

        private double expression() {
            double value = term();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '+') {
                    pos++;
                    value += term();
                } else if (c == '-') {
                    pos++;
                    value -= term();
                } else {
                    break;
                }
            }
            return value;
        }

        private double term() {
            double value = unary();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '*') {
                    pos++;
                    value *= unary();
                } else if (c == '/') {
                    pos++;
                    double divisor = unary();
                    if (divisor == 0) throw new ArithmeticException("القسمة على صفر");
                    value /= divisor;
                } else {
                    break;
                }
            }
            return value;
        }

        private double unary() {
            if (eat('+')) return unary();
            if (eat('-')) return -unary();
            return power();
        }

        private double power() {
            double value = postfix();
            if (eat('^')) value = Math.pow(value, unary());
            return value;
        }

        private double postfix() {
            double value = primary();
            while (pos < s.length()) {
                if (eat('%')) {
                    value /= 100.0;
                } else if (eat('!')) {
                    value = factorial(value);
                } else {
                    break;
                }
            }
            return value;
        }

        private double primary() {
            if (eat('(')) {
                double value = expression();
                if (!eat(')')) throw new IllegalArgumentException("قوس إغلاق مفقود");
                return value;
            }

            if (pos < s.length()
                    && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) {
                return number();
            }

            String name = identifier();
            if (name.isEmpty()) throw new IllegalArgumentException("القيمة غير موجودة");
            if (name.equals("pi")) return Math.PI;
            if (name.equals("e")) return Math.E;

            if (!eat('(')) throw new IllegalArgumentException("قوس الدالة مفقود");
            double value = expression();
            if (!eat(')')) throw new IllegalArgumentException("قوس إغلاق مفقود");

            switch (name) {
                case "sin": return Math.sin(angle(value));
                case "cos": return Math.cos(angle(value));
                case "tan":
                    double radians = angle(value);
                    if (Math.abs(Math.cos(radians)) < 1e-12) {
                        throw new ArithmeticException("قيمة الظل غير معرّفة");
                    }
                    return Math.tan(radians);
                case "sqrt":
                    if (value < 0) throw new ArithmeticException("جذر سالب");
                    return Math.sqrt(value);
                case "cbrt": return Math.cbrt(value);
                case "log":
                    if (value <= 0) throw new ArithmeticException("قيمة اللوغاريتم غير صالحة");
                    return Math.log10(value);
                case "ln":
                    if (value <= 0) throw new ArithmeticException("قيمة اللوغاريتم غير صالحة");
                    return Math.log(value);
                case "abs": return Math.abs(value);
                default: throw new IllegalArgumentException("دالة غير معروفة");
            }
        }

        private double angle(double value) {
            return degrees ? Math.toRadians(value) : value;
        }

        private double number() {
            int start = pos;
            boolean hasDigit = false;
            while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                pos++;
                hasDigit = true;
            }
            if (pos < s.length() && s.charAt(pos) == '.') {
                pos++;
                while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                    pos++;
                    hasDigit = true;
                }
            }
            if (!hasDigit) throw new IllegalArgumentException("رقم غير صالح");

            if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                int exponentStart = pos;
                int next = pos + 1;
                if (next < s.length() && (s.charAt(next) == '+' || s.charAt(next) == '-')) next++;
                int digitStart = next;
                while (next < s.length() && Character.isDigit(s.charAt(next))) next++;
                pos = next > digitStart ? next : exponentStart;
            }
            return Double.parseDouble(s.substring(start, pos));
        }

        private String identifier() {
            int start = pos;
            while (pos < s.length() && Character.isLetter(s.charAt(pos))) pos++;
            return s.substring(start, pos);
        }

        private boolean eat(char c) {
            if (pos < s.length() && s.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        private double factorial(double value) {
            if (value < 0 || value != Math.floor(value) || value > 170) {
                throw new ArithmeticException("المضروب يحتاج عددًا صحيحًا من 0 إلى 170");
            }
            double result = 1;
            for (int i = 2; i <= (int) value; i++) result *= i;
            return result;
        }
    }
}