package com.gwtexpress.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

/**
 * A separator that can be placed in a
 * {@link com.google.gwt.user.client.ui.MenuBar}.
 */
public class MenuItemSeparator2 extends UIObject {

  private static final String STYLENAME_DEFAULT = "gwt-MenuItemSeparator";

  private MenuBar2 parentMenu;

  /**
   * Constructs a new {@link MenuItemSeparator}.
   */
  public MenuItemSeparator2() {
    setElement(DOM.createTD());
    setStyleName(STYLENAME_DEFAULT);
    
    // Add an inner element for styling purposes
    Element div = DOM.createDiv();
    DOM.appendChild(getElement(), div);
    setStyleName(div, "content");
  }

  /**
   * Gets the menu that contains this item.
   * 
   * @return the parent menu, or <code>null</code> if none exists.
   */
  public MenuBar2 getParentMenu() {
    return parentMenu;
  }

  void setParentMenu(MenuBar2 parentMenu) {
    this.parentMenu = parentMenu;
  }
}
