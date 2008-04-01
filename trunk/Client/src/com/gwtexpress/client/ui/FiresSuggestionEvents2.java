package com.gwtexpress.client.ui;

import com.google.gwt.user.client.ui.SuggestionHandler;

public interface FiresSuggestionEvents2 {

  /**
   * Adds a handler interface to receive suggestion events.
   * 
   * @param handler the handler to add
   */
  void addEventHandler(SuggestionHandler2 handler);

  /**
   * Removes a previously added handler interface.
   * 
   * @param handler the handler to remove.
   */
  void removeEventHandler(SuggestionHandler2 handler);
}
