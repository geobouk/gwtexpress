package com.gwtexpress.client.ui;


import java.util.EventListener;

/**
 * Event handler interface for {@link SuggestionEvent}.
 *
 * @see SuggestBox
 */
public interface SuggestionHandler2 extends EventListener {

  /**
   * Fired when a suggestion is selected. Users can select a suggestion from
   * the SuggestBox by clicking on one of the suggestions, or by pressing
   * the ENTER key to select the suggestion that is currently highlighted.
   *
   * @param event the object containing information about this event
   */
  void onSuggestionSelected(SuggestionEvent2 event);
}
