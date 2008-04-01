package com.gwtexpress.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestionHandler;


public class SuggestBox extends Composite {
    SuggestBox2 sbox;
    com.google.gwt.user.client.ui.TextBox textBox = 
        new com.google.gwt.user.client.ui.TextBox();

    public SuggestBox() {
        sbox = new SuggestBox2();
        initWidget(sbox);
    }

    private SuggestBox(SuggestOracle oracle) {
        //sbox = new SuggestBox2(oracle, textBox);
        sbox.setLimit(25);
        initWidget(sbox);
    }

    public void setReadOnly(boolean b) {
        textBox.setReadOnly(b);
        textBox.isReadOnly();
    }

    public boolean isReadOnly() {
        return textBox.isReadOnly();
    }

    public final String getText() {
        return sbox.getText();
    }

    public final void setText(String text) {
        sbox.setText(text);
    }

    public final void addChangeListener(ChangeListener listener) {
        sbox.addChangeListener(listener);
    }

    /**
     * Adds a listener to recieve click events on the SuggestBox's text box.
     * The source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     */
    public final void addClickListener(ClickListener listener) {
        sbox.addClickListener(listener);
    }

    public final void addEventHandler(SuggestionHandler2 handler) {
        sbox.addEventHandler(handler);
    }

    /**
     * Adds a listener to recieve focus events on the SuggestBox's text box.
     * The source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     */
    public final void addFocusListener(FocusListener listener) {
        sbox.addFocusListener(listener);
    }

    /**
     * Adds a listener to recieve keyboard events on the SuggestBox's text box.
     * The source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     */
    public final void addKeyboardListener(KeyboardListener listener) {
        sbox.addKeyboardListener(listener);
    }
}
