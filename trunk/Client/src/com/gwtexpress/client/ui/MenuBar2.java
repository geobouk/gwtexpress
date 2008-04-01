package com.gwtexpress.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event;

import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A standard menu bar widget. A menu bar can contain any number of menu items,
 * each of which can either fire a {@link com.google.gwt.user.client.Command} or
 * open a cascaded menu bar.
 * 
 * <p>
 * <img class='gallery' src='MenuBar.png'/>
 * </p>
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-MenuBar { the menu bar itself }</li>
 * <li>.gwt-MenuBar-horizontal { dependent style applied to horizontal menu
 * bars }</li>
 * <li>.gwt-MenuBar-vertical { dependent style applied to vertical menu bars }</li>
 * <li>.gwt-MenuBar .gwt-MenuItem { menu items }</li>
 * <li>.gwt-MenuBar .gwt-MenuItem-selected { selected menu items }</li>
 * <li>.gwt-MenuBar .gwt-MenuItemSeparator { section breaks between menu items }
 * </li>
 * <li>.gwt-MenuBar .gwt-MenuItemSeparator .content { inner component of
 * section separators } </li>
 * </ul>
 * 
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.MenuBarExample}
 * </p>
 */
public class MenuBar2 extends Widget implements PopupListener {

  /**
   * List of all {@link MenuItem}s and {@link MenuItemSeparator}s.
   */
  private ArrayList<UIObject> allItems = new ArrayList<UIObject>();

  /**
   * List of {@link MenuItem}s, not including {@link MenuItemSeparator}s.
   */
  private ArrayList<MenuItem2> items = new ArrayList<MenuItem2>();

  private Element body;
  private MenuBar2 parentMenu;
  private PopupPanel popup;
  private MenuItem2 selectedItem;
  private MenuBar2 shownChildMenu;
  private boolean vertical, autoOpen;

  /**
   * Creates an empty horizontal menu bar.
   */
  public MenuBar2() {
    this(false);
  }

  /**
   * Creates an empty menu bar.
   * 
   * @param vertical <code>true</code> to orient the menu bar vertically
   */
  public MenuBar2(boolean vertical) {
    super();

    Element table = DOM.createTable();
    body = DOM.createTBody();
    DOM.appendChild(table, body);

    if (!vertical) {
      Element tr = DOM.createTR();
      DOM.appendChild(body, tr);
    }

    this.vertical = vertical;

    Element outer = DOM.createDiv();
    DOM.appendChild(outer, table);
    setElement(outer);

    sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
    setStyleName("gwt-MenuBar");
    if (vertical) {
      addStyleDependentName("vertical");
    } else {
      addStyleDependentName("horizontal");
    }
  }

  /**
   * Adds a menu item to the bar.
   * 
   * @param item the item to be added
   * @return the {@link MenuItem} object
   */
  public MenuItem2 addItem(MenuItem2 item) {
    addItemElement(item.getElement());
    item.setParentMenu(this);
    item.setSelectionStyle(false);
    items.add(item);
    allItems.add(item);
    return item;
  }

  /**
   * Adds a menu item to the bar, that will fire the given command when it is
   * selected.
   * 
   * @param text the item's text
   * @param asHTML <code>true</code> to treat the specified text as html
   * @param cmd the command to be fired
   * @return the {@link MenuItem} object created
   */
  public MenuItem2 addItem(String text, boolean asHTML, Command cmd) {
    return addItem(new MenuItem2(text, asHTML, cmd));
  }

  /**
   * Adds a menu item to the bar, that will open the specified menu when it is
   * selected.
   * 
   * @param text the item's text
   * @param asHTML <code>true</code> to treat the specified text as html
   * @param popup the menu to be cascaded from it
   * @return the {@link MenuItem} object created
   */
  public MenuItem2 addItem(String text, boolean asHTML, MenuBar2 popup) {
    return addItem(new MenuItem2(text, asHTML, popup));
  }

  /**
   * Adds a menu item to the bar, that will fire the given command when it is
   * selected.
   * 
   * @param text the item's text
   * @param cmd the command to be fired
   * @return the {@link MenuItem} object created
   */
  public MenuItem2 addItem(String text, Command cmd) {
    return addItem(new MenuItem2(text, cmd));
  }

  /**
   * Adds a menu item to the bar, that will open the specified menu when it is
   * selected.
   * 
   * @param text the item's text
   * @param popup the menu to be cascaded from it
   * @return the {@link MenuItem} object created
   */
  public MenuItem2 addItem(String text, MenuBar2 popup) {
    return addItem(new MenuItem2(text, popup));
  }

  /**
   * Adds a thin line to the {@link MenuBar} to separate sections of
   * {@link MenuItem}s.
   * 
   * @return the {@link MenuItemSeparator} object created
   */
  public MenuItemSeparator2 addSeparator() {
    return addSeparator(new MenuItemSeparator2());
  }

  /**
   * Adds a thin line to the {@link MenuBar} to separate sections of
   * {@link MenuItem}s.
   * 
   * @param separator the {@link MenuItemSeparator} to be added
   * @return the {@link MenuItemSeparator} object
   */
  public MenuItemSeparator2 addSeparator(MenuItemSeparator2 separator) {
    addItemElement(separator.getElement());
    separator.setParentMenu(this);
    allItems.add(separator);
    return separator;
  }

  /**
   * Removes all menu items from this menu bar.
   */
  public void clearItems() {
    Element container = getItemContainerElement();
    while (DOM.getChildCount(container) > 0) {
      DOM.removeChild(container, DOM.getChild(container, 0));
    }

    // Set the parent of all items to null
    for (UIObject item : allItems) {
      if (item instanceof MenuItemSeparator2) {
        ((MenuItemSeparator2) item).setParentMenu(null);
      } else {
        ((MenuItem2) item).setParentMenu(null);
      }
    }

    // Clear out all of the items and separators
    items.clear();
    allItems.clear();
  }

  /**
   * Gets whether this menu bar's child menus will open when the mouse is moved
   * over it.
   * 
   * @return <code>true</code> if child menus will auto-open
   */
  public boolean getAutoOpen() {
    return autoOpen;
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);

    MenuItem2 item = findItem(DOM.eventGetTarget(event));
    switch (DOM.eventGetType(event)) {
      case Event.ONCLICK: {
        // Fire an item's command when the user clicks on it.
        if (item != null) {
          doItemAction(item, true);
        }
        break;
      }

      case Event.ONMOUSEOVER: {
        if (item != null) {
          itemOver(item);
        }
        break;
      }

      case Event.ONMOUSEOUT: {
        if (item != null) {
          itemOver(null);
        }
        break;
      }
    }
  }

  public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
    // If the menu popup was auto-closed, close all of its parents as well.
    if (autoClosed) {
      closeAllParents();
    }

    // When the menu popup closes, remember that no item is
    // currently showing a popup menu.
    onHide();
    shownChildMenu = null;
    popup = null;
  }

  /**
   * Removes the specified menu item from the bar.
   * 
   * @param item the item to be removed
   */
  public void removeItem(MenuItem2 item) {
    if (removeItemElement(item)) {
      items.remove(item);
      item.setParentMenu(null);
    }
  }

  /**
   * Removes the specified {@link MenuItemSeparator} from the bar.
   * 
   * @param separator the separator to be removed
   */
  public void removeSeparator(MenuItemSeparator2 separator) {
    if (removeItemElement(separator)) {
      separator.setParentMenu(null);
    }
  }

  /**
   * Sets whether this menu bar's child menus will open when the mouse is moved
   * over it.
   * 
   * @param autoOpen <code>true</code> to cause child menus to auto-open
   */
  public void setAutoOpen(boolean autoOpen) {
    this.autoOpen = autoOpen;
  }

  /**
   * Returns a list containing the <code>MenuItem</code> objects in the menu
   * bar. If there are no items in the menu bar, then an empty <code>List</code>
   * object will be returned.
   * 
   * @return a list containing the <code>MenuItem</code> objects in the menu
   *         bar
   */
  protected List<MenuItem2> getItems() {
    return this.items;
  }

  /**
   * Returns the <code>MenuItem</code> that is currently selected
   * (highlighted) by the user. If none of the items in the menu are currently
   * selected, then <code>null</code> will be returned.
   * 
   * @return the <code>MenuItem</code> that is currently selected, or
   *         <code>null</code> if no items are currently selected
   */
  protected MenuItem2 getSelectedItem() {
    return this.selectedItem;
  }

  @Override
  protected void onDetach() {
    // When the menu is detached, make sure to close all of its children.
    if (popup != null) {
      popup.hide();
    }

    super.onDetach();
  }

  /**
   * <b>Affected Elements:</b>
   * <ul>
   * <li>-item# = the {@link MenuItem} at the specified index.</li>
   * </ul>
   * 
   * @see UIObject#onEnsureDebugId(String)
   */
  @Override
  protected void onEnsureDebugId(String baseID) {
    super.onEnsureDebugId(baseID);
    setMenuItemDebugIds(baseID);
  }

  /*
   * Closes all parent menu popups.
   */
  void closeAllParents() {
    MenuBar2 curMenu = this;
    while (curMenu != null) {
      curMenu.close();

      if ((curMenu.parentMenu == null) && (curMenu.selectedItem != null)) {
        curMenu.selectedItem.setSelectionStyle(false);
        curMenu.selectedItem = null;
      }

      curMenu = curMenu.parentMenu;
    }
  }

  /*
   * Performs the action associated with the given menu item. If the item has a
   * popup associated with it, the popup will be shown. If it has a command
   * associated with it, and 'fireCommand' is true, then the command will be
   * fired. Popups associated with other items will be hidden.
   * 
   * @param item the item whose popup is to be shown. @param fireCommand <code>true</code>
   * if the item's command should be fired, <code>false</code> otherwise.
   */
  void doItemAction(final MenuItem2 item, boolean fireCommand) {
    // If the given item is already showing its menu, we're done.
    if ((shownChildMenu != null) && (item.getSubMenu() == shownChildMenu)) {
      return;
    }

    // If another item is showing its menu, then hide it.
    if (shownChildMenu != null) {
      shownChildMenu.onHide();
      popup.hide();
    }

    // If the item has no popup, optionally fire its command.
    if (item.getSubMenu() == null) {
      if (fireCommand) {
        // Close this menu and all of its parents.
        closeAllParents();

        // Fire the item's command.
        Command cmd = item.getCommand();
        if (cmd != null) {
          DeferredCommand.addCommand(cmd);
        }
      }
      return;
    }

    // Ensure that the item is selected.
    selectItem(item);

    // Create a new popup for this item, and position it next to
    // the item (below if this is a horizontal menu bar, to the
    // right if it's a vertical bar).
    popup = new PopupPanel(true) {
      {
        setWidget(item.getSubMenu());
        item.getSubMenu().onShow();
      }

      @Override
      public boolean onEventPreview(Event event) {
        // Hook the popup panel's event preview. We use this to keep it from
        // auto-hiding when the parent menu is clicked.
        switch (DOM.eventGetType(event)) {
          case Event.ONCLICK:
            // If the event target is part of the parent menu, suppress the
            // event altogether.
            Element target = DOM.eventGetTarget(event);
            Element parentMenuElement = item.getParentMenu().getElement();
            if (DOM.isOrHasChild(parentMenuElement, target)) {
              return false;
            }
            break;
        }

        return super.onEventPreview(event);
      }
    };
    popup.addPopupListener(this);

    if (vertical) {
      popup.setPopupPosition(
          this.getAbsoluteLeft() + this.getOffsetWidth() - 1,
          item.getAbsoluteTop());
    } else {
      popup.setPopupPosition(item.getAbsoluteLeft(), this.getAbsoluteTop()
          + this.getOffsetHeight() - 1);
    }

    shownChildMenu = item.getSubMenu();
    item.getSubMenu().parentMenu = this;

    // Show the popup, ensuring that the menubar's event preview remains on top
    // of the popup's.
    popup.show();
  }

  void itemOver(MenuItem2 item) {
    if (item == null) {
      // Don't clear selection if the currently selected item's menu is showing.
      if ((selectedItem != null)
          && (shownChildMenu == selectedItem.getSubMenu())) {
        return;
      }
    }

    // Style the item selected when the mouse enters.
    selectItem(item);

    // If child menus are being shown, or this menu is itself
    // a child menu, automatically show an item's child menu
    // when the mouse enters.
    if (item != null) {
      if ((shownChildMenu != null) || (parentMenu != null) || autoOpen) {
        doItemAction(item, false);
      }
    }
  }

  void selectItem(MenuItem2 item) {
    if (item == selectedItem) {
      return;
    }

    if (selectedItem != null) {
      selectedItem.setSelectionStyle(false);
    }

    if (item != null) {
      item.setSelectionStyle(true);
    }

    selectedItem = item;
  }

  /**
   * Set the IDs of the menu items.
   * 
   * @param baseID the base ID
   */
  void setMenuItemDebugIds(String baseID) {
    int itemCount = 0;
    for (MenuItem2 item : items) {
      item.ensureDebugId(baseID + "-item" + itemCount);
      itemCount++;
    }
  }

  /**
   * Physically add the td element of a {@link MenuItem} or
   * {@link MenuItemSeparator} to this {@link MenuBar}.
   * 
   * @param tdElem the td element to be added
   */
  private void addItemElement(Element tdElem) {
    Element tr;
    if (vertical) {
      tr = DOM.createTR();
      DOM.appendChild(body, tr);
    } else {
      tr = DOM.getChild(body, 0);
    }
    DOM.appendChild(tr, tdElem);
  }

  /**
   * Closes this menu (if it is a popup).
   */
  private void close() {
    if (parentMenu != null) {
      parentMenu.popup.hide();
    }
  }

  private MenuItem2 findItem(Element hItem) {
    for (int i = 0; i < items.size(); ++i) {
      MenuItem2 item = items.get(i);
      if (DOM.isOrHasChild(item.getElement(), hItem)) {
        return item;
      }
    }

    return null;
  }

  private Element getItemContainerElement() {
    if (vertical) {
      return body;
    } else {
      return DOM.getChild(body, 0);
    }
  }

  /*
   * This method is called when a menu bar is hidden, so that it can hide any
   * child popups that are currently being shown.
   */
  private void onHide() {
    if (shownChildMenu != null) {
      shownChildMenu.onHide();
      popup.hide();
    }
  }

  /*
   * This method is called when a menu bar is shown.
   */
  private void onShow() {
    // Select the first item when a menu is shown.
    if (items.size() > 0) {
      selectItem(items.get(0));
    }
  }

  /**
   * Removes the specified item from the {@link MenuBar} and the physical DOM
   * structure.
   * 
   * @param item the item to be removed
   * @return true if the item was removed
   */
  private boolean removeItemElement(UIObject item) {
    int idx = allItems.indexOf(item);
    if (idx == -1) {
      return false;
    }

    Element container = getItemContainerElement();
    DOM.removeChild(container, DOM.getChild(container, idx));
    allItems.remove(idx);
    return true;
  }
}
