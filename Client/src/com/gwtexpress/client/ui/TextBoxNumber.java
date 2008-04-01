package com.gwtexpress.client.ui;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxNumber extends TextBox implements GLogixField {

    public TextBoxNumber(int rowIndex, int columnIndex) {
        this(rowIndex, columnIndex, false);
    }
    
    public TextBoxNumber(int rowIndex, int columnIndex, boolean format) {
        super(rowIndex, columnIndex);
        addKeyboardListener(keyboardListenerAdapter);
        setTextAlignment(TextBoxBase.ALIGN_RIGHT);    
        if (format) {
            this.addChangeListener(new ChangeListener() {
                        public void onChange(Widget widget) {
                            ((com.google.gwt.user.client.ui.TextBox)widget).setText(format(getNumberValue()));
                        }
                    });
        }        
    }

    static final NumberFormat numberFormat = 
        NumberFormat.getFormat("####0.00");
    static final KeyboardListenerAdapter keyboardListenerAdapter = 
        new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, 
                                   int modifiers) {
                if ((!Character.isDigit(keyCode)) && 
                    (keyCode != (char)KEY_TAB) && 
                    (keyCode != (char)KEY_BACKSPACE) && 
                    (keyCode != (char)KEY_DELETE) && 
                    (keyCode != (char)KEY_ENTER) && 
                    (keyCode != (char)KEY_HOME) && 
                    (keyCode != (char)KEY_END) && 
                    (keyCode != (char)KEY_LEFT) && (keyCode != (char)KEY_UP) && 
                    (keyCode != (char)KEY_RIGHT) && 
                    (keyCode != (char)KEY_DOWN)) {
                    // TextBox.cancelKey() suppresses the current keyboard event.
                    ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                }
            }
        };

    public static String format(double d){
        return numberFormat.format(d);
    }
    public double getNumberValue() {
        String value = getText();
        if (value == null || value.trim().length() == 0)
            return 0;
        return Double.parseDouble(value);
    }
        
}
