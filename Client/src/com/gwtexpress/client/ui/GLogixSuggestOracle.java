package com.gwtexpress.client.ui;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GLogixSuggestOracle extends SuggestOracle {
    ArrayList<String[]> items = new ArrayList<String[]>();

    public GLogixSuggestOracle() {
    }

    public void setItems(ArrayList<String[]> items) {
        this.items = items;
    }

    public ArrayList<String[]> getItems() {
        return items;
    }

    public static class MySuggestion implements Suggestion, IsSerializable {
        private String _value;
        private String _displayString;

        public MySuggestion() {
        }

        public MySuggestion(String value, String displayString) {
            _value = value;
            _displayString = displayString;
        }

        public String getDisplayString() {
            return _displayString;
        }

        public String getReplacementString() {
            return _value;
        }
    }
    String lastQuery = "";
    int lastSuggestionsCount = -1;

    public void requestSuggestions(Request request, Callback callback) {
        List<MySuggestion> suggestions = new ArrayList<MySuggestion>();
        if (request.getQuery().length() >= 1) {
            Log.info("requestSuggestions with " + request.getQuery());
            if (lastSuggestionsCount == 0 && 
                request.getQuery().toLowerCase().startsWith(lastQuery)) {
                Response response = new Response(suggestions);
                callback.onSuggestionsReady(request, response);
                return;
            }
            suggestions = 
                    computeItemsFor(request.getQuery().toLowerCase(), request.getLimit());
            lastSuggestionsCount = suggestions.size();
            Log.info("Found " + lastSuggestionsCount + " suggestions");
            lastQuery = request.getQuery().toLowerCase();
            Response response = new Response(suggestions);
            callback.onSuggestionsReady(request, response);
        } else {
            Response response = new Response(suggestions);
            callback.onSuggestionsReady(request, response);
        }
    }

    private List<MySuggestion> computeItemsFor(String query, int limit) {
        ArrayList<MySuggestion> matches = new ArrayList<MySuggestion>();
        String value = null;
        String meaning = null;
        limit = 100;

        for (int i = 0; i < items.size() && matches.size() < limit; i++) {
            meaning = null;
            value = null;
            String[] data = items.get(i);
            if (data == null || data.length == 0)
                continue;
            value = data[0];
            if (value == null)
                continue;
            if (items.get(i).length > 1)
                meaning = items.get(i)[1];
            if (meaning != null && !value.equals(meaning)) {
                meaning = meaning + "[" + value + "]";
            } else {
                meaning = value;
            }
            if (meaning.toLowerCase().indexOf(query) >= 0)
                matches.add(getFormattedSuggestion(query, meaning, value));
        }
        return matches;
    }

    private MySuggestion getFormattedSuggestion(String query, 
                                                String suggestion, 
                                                String value) {
        StringBuffer sb = new StringBuffer();
        int start = suggestion.toLowerCase().indexOf(query);
        while (start >= 0) {
            sb.append(suggestion.substring(0, start));
            sb.append("<b>").append(suggestion.substring(start, 
                                                         start + query.length()));
            sb.append("</b>");
            suggestion = suggestion.substring(start + query.length());
            start = suggestion.toLowerCase().indexOf(query);
        }
        sb.append(suggestion);
        return new MySuggestion(value, sb.toString());
    }

    public boolean isDisplayStringHTML() {
        return true;
    }
}
