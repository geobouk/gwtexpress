package com.gwtexpress.client.ui;

import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.EventObject;

/**
 * Event object containing information about the selection of a
 * {@link SuggestOracle.Suggestion} displayed by a {@link SuggestBox}.
 *
 * @see SuggestBox#addEventHandler(SuggestionHandler)
 */
public class SuggestionEvent2 extends EventObject {

  private SuggestOracle.Suggestion selectedSuggestion;

  public SuggestionEvent2(SuggestBox2 sender,
                         SuggestOracle.Suggestion selectedSuggestion) {
    super(sender);
    this.selectedSuggestion = selectedSuggestion;
  }

  /**
   * Gets the <code>Suggestion</code> object for the suggestion chosen by the
   * user.
   *
   * @return the <code>Suggestion</code> object for the selected suggestion
   */
  public SuggestOracle.Suggestion getSelectedSuggestion() {
    return selectedSuggestion;
  }

  /**
   * Returns the string representation of this event object. The string contains
   * the string representation of the SuggestBox from which the event originated
   * (the source), and the string representation of the Suggestion that was
   * selected.
   *
   * @return the string representation of this event object containing the
   *         source SuggestBox and the selected Suggestion
   */
  @Override
  public String toString() {
    return "[source=" + getSource() +
        ", selectedSuggestion=" + getSelectedSuggestion() + "]";
  }
}
