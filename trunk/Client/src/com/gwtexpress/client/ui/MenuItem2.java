package com.gwtexpress.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * A widget that can be placed in a
 * {@link com.google.gwt.user.client.ui.MenuBar}. Menu items can either fire a
 * {@link com.google.gwt.user.client.Command} when they are clicked, or open a
 * cascading sub-menu.
 */
public class MenuItem2 extends UIObject implements HasHTML {

  private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";

  private Command command;
  private MenuBar2 parentMenu, subMenu;

  /**
   * Constructs a new menu item that fires a command when it is selected.
   * 
   * @param text the item's text
   * @param cmd the command to be fired when it is selected
   */
  public MenuItem2(String text, Command cmd) {
    this(text, false);
    setCommand(cmd);
  }

  /**
   * Constructs a new menu item that fires a command when it is selected.
   * 
   * @param text the item's text
   * @param asHTML <code>true</code> to treat the specified text as html
   * @param cmd the command to be fired when it is selected
   */
  public MenuItem2(String text, boolean asHTML, Command cmd) {
    this(text, asHTML);
    setCommand(cmd);
  }

  /**
   * Constructs a new menu item that cascades to a sub-menu when it is selected.
   * 
   * @param text the item's text
   * @param subMenu the sub-menu to be displayed when it is selected
   */
  public MenuItem2(String text, MenuBar2 subMenu) {
    this(text, false);
    setSubMenu(subMenu);
  }

  /**
   * Constructs a new menu item that cascades to a sub-menu when it is selected.
   * 
   * @param text the item's text
   * @param asHTML <code>true</code> to treat the specified text as html
   * @param subMenu the sub-menu to be displayed when it is selected
   */
  public MenuItem2(String text, boolean asHTML, MenuBar2 subMenu) {
    this(text, asHTML);
    setSubMenu(subMenu);
  }

  MenuItem2(String text, boolean asHTML) {
    setElement(DOM.createTD());
    setSelectionStyle(false);

    if (asHTML) {
      setHTML(text);
    } else {
      setText(text);
    }
    setStyleName("gwt-MenuItem");
  }

  /**
   * Gets the command associated with this item.
   * 
   * @return this item's command, or <code>null</code> if none exists
   */
  public Command getCommand() {
    return command;
  }

  public String getHTML() {
    return DOM.getInnerHTML(getElement());
  }

  /**
   * Gets the menu that contains this item.
   * 
   * @return the parent menu, or <code>null</code> if none exists.
   */
  public MenuBar2 getParentMenu() {
    return parentMenu;
  }

  /**
   * Gets the sub-menu associated with this item.
   * 
   * @return this item's sub-menu, or <code>null</code> if none exists
   */
  public MenuBar2 getSubMenu() {
    return subMenu;
  }

  public String getText() {
    return DOM.getInnerText(getElement());
  }

  /**
   * Sets the command associated with this item.
   * 
   * @param cmd the command to be associated with this item
   */
  public void setCommand(Command cmd) {
    command = cmd;
  }

  public void setHTML(String html) {
    DOM.setInnerHTML(getElement(), html);
  }

  /**
   * Sets the sub-menu associated with this item.
   * 
   * @param subMenu this item's new sub-menu
   */
  public void setSubMenu(MenuBar2 subMenu) {
    this.subMenu = subMenu;
  }

  public void setText(String text) {
    DOM.setInnerText(getElement(), text);
  }

  /**
   * Also sets the Debug IDs of MenuItems in the submenu of this
   * {@link MenuItem} if a submenu exists.
   * 
   * @see UIObject#onEnsureDebugId(String)
   */
  @Override
  protected void onEnsureDebugId(String baseID) {
    super.onEnsureDebugId(baseID);
    if (subMenu != null) {
      subMenu.setMenuItemDebugIds(baseID);
    }
  }

  void setParentMenu(MenuBar2 parentMenu) {
    this.parentMenu = parentMenu;
  }

  void setSelectionStyle(boolean selected) {
    if (selected) {
      addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
    } else {
      removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
    }
  }
}
