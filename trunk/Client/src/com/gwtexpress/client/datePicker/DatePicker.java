package com.gwtexpress.client.datePicker;

import com.gwtexpress.client.ui.GLogixField;
import com.gwtexpress.client.ui.TextBox;
import com.gwtexpress.client.util.DateUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;


/**
 * Main class of the DatePicker. It extends the TextBox widget and manages a Date object.
 * When it is clicked, it opens a PopupCalendar on which we can select a new date. <br>
 * Example of use :  <br>
 * <code>
 * DatePicker datePicker = new DatePicker();<br>
 * RootPanel.get().add(datePicker);<br>
 * </code>
 * You can specify a theme (see the CSS file DatePickerStyle.css) and
 * the date to initialize the date picker.
 * Enjoy xD
 * @author Nicolas Wetzel (nicolas.wetzel@zenika.com)
 * @author Jean-Philippe Dournel
 */
public class DatePicker extends Composite implements GLogixField {
    private PopupCalendar popup;
    private Date date;
    private DateTimeFormat dateFormatter;
    private TextBox textBox;
    int dateLength = 10;
    private ChangeListenerCollection changeListeners;
    Image icon = new Image("images/icons/date-add.png");
    private HorizontalPanel panel = new HorizontalPanel();

    {
        dateFormatter = DateUtil.getDateFormat();
        dateLength = dateFormatter.getPattern().length();
        popup = new PopupCalendar(this);
    }

    /**
     * Default constructor. It creates a DatePicker which shows the current
     * month.
     */
    public DatePicker(int rowIndex, int columnIndex) {
        textBox = new TextBox(rowIndex, columnIndex);
        //super(rowIndex, columnIndex);
        textBox.setText("");
        //sinkEvents(Event.ONCHANGE | Event.ONKEYPRESS);
        textBox.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        showPopup();
                    }
                });
        textBox.addFocusListener(new FocusListener() {
                    public void onFocus(Widget sender) {
                    }

                    public void onLostFocus(Widget sender) {
                        popup.hidePopupCalendar();
                        parseDate();
                    }
                });
        textBox.addChangeListener(new ChangeListener() {
                    public void onChange(Widget sender) {
                        parseDate();
                    }
                });
        KeyboardListenerAdapter keyboardListenerAdapter = 
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
                        (keyCode != (char)KEY_LEFT) && 
                        (keyCode != (char)KEY_UP) && 
                        (keyCode != (char)KEY_RIGHT) && 
                        (keyCode != (char)KEY_DOWN)) {
                        // TextBox.cancelKey() suppresses the current keyboard event.
                        ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                    } else {
                        String dateVal = ((TextBox)sender).getText();
                        int len = dateVal.length();
                        int slen = ((TextBox)sender).getSelectionLength();
                        if (len == slen) {
                            len = 0;
                            ((TextBox)sender).setSelectionRange(0, 0);
                            ((TextBox)sender).setText("");
                        }

                        if (len == 2 || len == 5) {
                            dateVal = dateVal + "/";
                            len++;
                            ((TextBox)sender).setText(dateVal);
                        }

                        if (len == 13) {
                            dateVal = dateVal + ":";
                            len++;
                            ((TextBox)sender).setText(dateVal);
                        }

                        if (len == 10 && dateLength > 10) {
                            dateVal = dateVal + " ";
                            len++;
                            ((TextBox)sender).setText(dateVal);
                        }

                        if (len == 0 && !(keyCode == '0' || keyCode == '1')) {
                            dateVal = "0" + keyCode + "/";
                            ((TextBox)sender).setText(dateVal);
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                            return;
                        }

                        if (len == 1 && 
                            !(keyCode == '0' || keyCode == '1' || keyCode == 
                              '2')) {
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                            return;
                        }


                        if (len == 3 && 
                            !(keyCode == '0' || keyCode == '1' || keyCode == 
                              '2' || keyCode == '3')) {
                            dateVal = dateVal + "0" + keyCode + "/";
                            ((TextBox)sender).setText(dateVal);
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                            return;
                        }

                        if (len == 9 && dateLength > 10) {
                            dateVal = dateVal + keyCode + " ";
                            ((TextBox)sender).setText(dateVal);
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                            return;
                        }

                        if (len == 12) {
                            dateVal = dateVal + keyCode + ":";
                            ((TextBox)sender).setText(dateVal);
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                            return;
                        }

                        if (len == 1 || len == 4) {
                            dateVal = dateVal + keyCode + "/";
                            ((TextBox)sender).setText(dateVal);
                            ((com.google.gwt.user.client.ui.TextBox)sender).cancelKey();
                        }

                    }
                }
            };
        textBox.addKeyboardListener(keyboardListenerAdapter);

        icon.addStyleName("pointer");
        icon.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        showPopup();
                    }
                });
        panel.add(textBox);
        panel.add(icon);
        panel.setCellVerticalAlignment(textBox, 
                                       HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellVerticalAlignment(icon, 
                                       HasVerticalAlignment.ALIGN_MIDDLE);
        initWidget(panel);

    }

    public void addChangeListener(final ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
        final DatePicker me = this;
        textBox.addChangeListener(new ChangeListener() {
                    public void onChange(Widget sender) {
                        listener.onChange(me);
                    }
                });
    }

    public void addStyleName(String style) {
        textBox.addStyleName(style);
    }

    public void removeStyleName(String style) {
        textBox.removeStyleName(style);
    }

    public void setWidth(String width) {
        textBox.setWidth(width);
    }

    public void setReadOnly(boolean readOnly) {
        textBox.setReadOnly(readOnly);
        if (readOnly)
            icon.setVisible(false);
        else
            icon.setVisible(true);
    }

    public void setMaxLength(int length) {
        textBox.setMaxLength(length);
    }

    public void setFocus(boolean f) {
        textBox.setFocus(f);
    }

    public void addFocusListener(FocusListener listener) {
        textBox.addFocusListener(listener);
    }

    public void setText(String value) {
        textBox.setText(value);
        parseDate();
    }

    public String getText() {
        return textBox.getText();
    }
    //    /**
    //     * Create a DatePicker which show a specific Date.
    //     * @param date Date to show
    //     */
    //    public DatePicker(Date date) {
    //        this();
    //        this.date = date;
    //        synchronizeFromDate();
    //    }
    //
    //    /**
    //     * Create a DatePicker which uses a specific theme.
    //     * @param theme Theme name
    //     */
    //    public DatePicker(String theme) {
    //        this();
    //        setTheme(theme);
    //    }
    //
    //    /**
    //     * Create a DatePicker which specifics date and theme.
    //     * @param date Date to show
    //     * @param theme Theme name
    //     */
    //    public DatePicker(Date date, String theme) {
    //        this();
    //        this.date = date;
    //        synchronizeFromDate();
    //        setTheme(theme);
    //    }

    /**
     * Return the Date contained in the DatePicker.
     * @return The Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the Date of the datePicker and synchronize it with the display.
     * @param value
     */
    public void setDate(Date value) {
        this.date = value;
        synchronizeFromDate();
    }

    /**
     * Return the theme name.
     * @return Theme name
     */
    public String getTheme() {
        return popup.getTheme();
    }

    /**
     * Set the theme name.
     * @param theme Theme name
     */
    public void setTheme(String theme) {
        popup.setTheme(theme);
    }
    /*
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONCLICK:
            showPopup();
            break;
        case Event.ONBLUR:
            popup.hidePopupCalendar();
            break;
        case Event.ONCHANGE:
            parseDate();
            break;
        case Event.ONKEYPRESS:
            if (DOM.eventGetKeyCode(event) == 13) {
                parseDate();
                showPopup();
                break;
            }
        }
    }
*/

    /**
     * Display the date in the DatePicker.
     */
    public void synchronizeFromDate() {
        textBox.setText(dateFormatter.format(date));
        if (changeListeners != null)
            changeListeners.fireChange(this);
    }

    /**
     * Display the PopupCalendar.
     */
    private void showPopup() {
        if (textBox.isReadOnly()) return;
        if (this.date != null) {
            popup.setDisplayedMonth(this.date);
        } else {
            popup.setDisplayedMonth(new Date(System.currentTimeMillis()));
        }
        popup.setPopupPosition(this.getAbsoluteLeft(), 
                               this.getAbsoluteTop() + 16);
        popup.displayMonth();
    }

    /**
     * Parse the date entered in the DatePicker.
     */
    private void parseDate() {
        try {
            Date d = dateFormatter.parse(textBox.getText());
            if (d != null && (date == null || d.getTime() != date.getTime())){
                date = d;
                synchronizeFromDate();
            }
        } catch (Exception e) {
        }
    }

    public void setDateFormatter(DateTimeFormat dateFormatter) {
        this.dateFormatter = dateFormatter;
        dateLength = dateFormatter.getPattern().length();
    }

    public DateTimeFormat getDateFormatter() {
        return dateFormatter;
    }

    public int getColumnIndex() {
        return textBox.getColumnIndex();
    }

    public void setColumnIndex(int index) {
        textBox.setColumnIndex(index);
    }

    public int getRowIndex() {
        return textBox.getRowIndex();
    }

    public void setRowIndex(int index) {
        textBox.setRowIndex(index);
    }
}
