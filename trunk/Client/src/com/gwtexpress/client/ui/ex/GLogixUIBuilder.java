package com.gwtexpress.client.ui.ex;

import com.allen_sauer.gwt.log.client.Log;

import com.gwtexpress.client.rpc.ClientService;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.LookupValues;
import com.gwtexpress.client.ui.GLogixSuggestOracle;
import com.gwtexpress.client.ui.ListBox;
import com.gwtexpress.client.ui.SuggestBox2;
import com.gwtexpress.client.ui.form.AbstractFormLayout;
import com.gwtexpress.client.ui.form.AdvancedSearchForm;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;
import com.gwtexpress.client.ui.table.SimpleTable;
import com.gwtexpress.client.util.DateUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.mygwt.ui.client.Events;
import net.mygwt.ui.client.Style;
import net.mygwt.ui.client.widget.Dialog;
import net.mygwt.ui.client.widget.Info;
import net.mygwt.ui.client.widget.LoadingPanel;
import net.mygwt.ui.client.widget.table.TableColumn;
import net.mygwt.ui.client.widget.table.TableColumnModel;


public class GLogixUIBuilder {
    static final HashMap<String, ArrayList<String[]>> listCache = 
        new HashMap<String, ArrayList<String[]>>();

    static final ArrayList<String> listCacheInProgress = 
        new ArrayList<String>();

    static final HashMap<String, MultiWordSuggestOracle> oracleCache = 
        new HashMap<String, MultiWordSuggestOracle>();


    private GLogixUIBuilder() {
    }
    static final Dialog dialog = 
        new Dialog(Style.OK_CANCEL | Style.CLOSE | Style.RESIZE | Style.MODAL);

    public static void showError(String title, Throwable caught) {
        try {
            throw caught;
        } catch (InvocationException e) {
            Log.debug("InvocationException", e);
            showError(title, 
                      "Unable to connect to the server. Could be one of the following reasons.<li>The network connection to the server is unavailable</li>" + 
                      "<li>The host web server is not available</li>" + 
                      "<li>The server is not available</li>" + "<li>" + 
                      e.getMessage() + 
                      ((e.getCause() == null) ? "" : ("Cause:" + 
                                                      e.getCause().getMessage())) + 
                      "</li>");
        } catch (SerializableException ex) {
            showError(title, ex.getMessage());
        } catch (Throwable ex) {
            //showError(title, e.getMessage());

            StringBuffer sb = new StringBuffer(title);
            sb.append("<p/><span style='color:#FF0000;'>").append(ex.getClass().getName()).append("</span><p/><pre>").append(ex.getMessage());
            StackTraceElement[] stacks = ex.getStackTrace();
            for (int i = 0; i < stacks.length; i++) {
                sb.append("<br/>").append(stacks[i].toString());
            }
            sb.append("</pre>");
            showError(title, sb.toString());
        }
    }

    public static void showError(String title, String message) {
        int w = Window.getClientWidth();
        if (w > 400)
            w = 400;
        dialog.setWidth(w);
        dialog.setCloseOnButtonClick(true);
        dialog.setText(title);
        dialog.getContent().setHeight(100);
        dialog.getContent().removeAll();
        dialog.getContent().setScrollEnabled(true);
        dialog.getContent().addText(message);
        dialog.open();
        dialog.center();
    }

    private static AsyncCallback<HashMap<String, ArrayList<String[]>>> createFetchMDCallback(final InputFormLayout form) {
        return new AsyncCallback<HashMap<String, ArrayList<String[]>>>() {
                public void onFailure(Throwable caught) {
                    LoadingPanel.get().hide();
                    GLogixUIBuilder.showError("Error fetching data for " + 
                                              form.metaData.getServiceName(), 
                                              caught);
                }

                public void onSuccess(HashMap<String, ArrayList<String[]>> data) {
                    if (data != null) {
                        if (data.size() == 0) {
                            LoadingPanel.get().hide();
                            GLogixUIBuilder.showError("No data found", 
                                                      "No data found");
                            form.fireEvent(Events.Close, form, form.cancelBtn);
                        } else {
                            form.setData(data, true);
                            form.setFormType(FormLayout.EDIT_FORM);
                        }
                    }
                    LoadingPanel.get().hide();
                }
            };
    }

    private static AsyncCallback<HashMap<String, ArrayList<String[]>>> createMDFormCallback(final InputFormLayout form) {
        return new AsyncCallback<HashMap<String, ArrayList<String[]>>>() {
                public void onFailure(Throwable caught) {
                    LoadingPanel.get().hide();
                    GLogixUIBuilder.showError("Error posting service " + 
                                              form.metaData.getServiceName(), 
                                              caught);
                }

                public void onSuccess(HashMap<String, ArrayList<String[]>> data) {
                    if (data != null) {
                        if (form.isUpdateOnPost()) {
                            form.setData(data, false);
                            form.setFormType(FormLayout.EDIT_FORM);
                            form.resetForm();
                        }
                    }
                    LoadingPanel.get().hide();
                    form.fireEvent(Events.Close, form, form.saveBtn);
                    Info.show("Success", "Changes posted...", "");
                }
            };
    }

    private static AsyncCallback<ArrayList<String[]>> createFormCallback(final InputFormLayout form) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    LoadingPanel.get().hide();
                    GLogixUIBuilder.showError("Error posting service " + 
                                              form.metaData.getServiceName(), 
                                              caught);
                }

                public void onSuccess(ArrayList<String[]> data) {
                    if (data != null && data.size() == 1 && 
                        data.get(0) != null) {
                        if (form.isUpdateOnPost()) {
                            form.setFormType(FormLayout.EDIT_FORM);
                            form.setData(data.get(0));
                            form.resetForm();
                        }
                    }
                    LoadingPanel.get().hide();
                    form.fireEvent(Events.Close, form, form.saveBtn);
                    Info.show("Success", "Changes posted...", "");
                }
            };
    }

    public static void populateList(String lookupName, SuggestBox2 sb, 
                                    GLogixSuggestOracle oracle, 
                                    ArrayList<String[]> data, 
                                    String defaultValue) {

        //String[] row;
        oracle.setItems(data);
        if (!listCache.containsKey(lookupName)) {
            listCache.put(lookupName, data);
        }
        int x = data.size();
        //        Log.debug("Populating SuggestBox for lookup " + lookupName + " with " + 
        //                  x + " items");
        //        for (int i = 0; i < x; i++) {
        //            row = data.get(i);
        //            if (defaultValue != null && defaultValue.equals(row[0])) {
        //                sb.setText(row[1]);
        //            }
        //        }
    }

    public static void populateList(String lookupName, AbstractFormLayout form, 
                                    ListBox lb, ArrayList<String[]> data, 
                                    String defaultValue) {
        String[] row;
        if ("".equals(defaultValue))
            defaultValue = null;
        listCache.put(lookupName, data);
        if (lb == null)
            return;
        lb.clear();
        if (data == null || data.size() == 0) {
            lb.addItem("No values available", "");
            Log.info("No data found to populate list for lookupName:" + 
                     lookupName);
        } else {
            int x = data.size();

            if (!lb.isRequired())
                lb.addItem("", "");
            else
                lb.addItem("Select a value", "");
            for (int i = 0; i < x; i++) {
                row = data.get(i);
                if (row.length > 1)
                    lb.addItem(row[1], row[0]);
                else
                    lb.addItem(row[0], row[0]);

                if (defaultValue != null && defaultValue.equals(row[0])) {
                    //                    if (!lb.isRequired())
                    lb.setSelectedIndex(i + 1);
                    //                    else
                    //                        lb.setSelectedIndex(i);
                }
            }
            if (defaultValue == null || lb.getSelectedIndex() < 0) {
                lb.setSelectedIndex(0);
                if (form != null && 
                    (form.getFormType() == FormLayout.CREATE_FORM || 
                     form.getFormType() ==FormLayout.EDIT_FORM))
                    form.setDataValue(lb.getRowIndex(), lb.getColumnIndex(), 
                                      lb.getValue(0));
            }
        }
    }

    private static AsyncCallback<ArrayList<String[]>> createGLogixServiceCallback(final String lookupName, 
                                                                                  final SuggestBox2 lb, 
                                                                                  final GLogixSuggestOracle oracle, 
                                                                                  final GLogixMetaData metaData, 
                                                                                  final String defaultValue) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    GLogixUIBuilder.showError("Error populating suggest box for " + 
                                              metaData.getServiceName(), 
                                              caught);
                }
                StringBuffer sb = new StringBuffer();

                public void onSuccess(ArrayList<String[]> data) {
                    populateList(lookupName, lb, oracle, data, defaultValue);
                    listCacheInProgress.remove(lookupName);
                }
            };
    }

    private static AsyncCallback<ArrayList<String[]>> createGLogixServiceCallback(final String lookupName, 
                                                                                  final AbstractFormLayout form, 
                                                                                  final ListBox lb, 
                                                                                  final GLogixMetaData metaData, 
                                                                                  final String defaultValue) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    GLogixUIBuilder.showError("Error populating listbox for " + 
                                              metaData.getServiceName(), 
                                              caught);
                }
                StringBuffer sb = new StringBuffer();

                public void onSuccess(ArrayList<String[]> data) {
                    populateList(lookupName, form, lb, data, defaultValue);
                    listCacheInProgress.remove(lookupName);
                }
            };
    }

    private static AsyncCallback<ArrayList<String[]>> createGLogixServiceCallback(final SimpleTable table, 
                                                                                  final GLogixMetaData metaData) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    LoadingPanel.get().hide();
                    GLogixUIBuilder.showError("Error invoking service " + 
                                              metaData.getServiceName(), 
                                              caught);
                }
                StringBuffer sb = new StringBuffer();

                public void onSuccess(ArrayList<String[]> data) {
                    int total = data.size();
                    if (total > 0) {
                        Info.show("Match found...", total + " rows fetched...", 
                                  "");
                    } else {
                        Info.show("No data found...", null, "");
                    }
                    table.setData(data);
                    LoadingPanel.get().hide();
                }
            };
    }
    //    private AsyncCallback<List> createGLogixServiceCallback(final TableViewer tableViewer) {
    //        return new AsyncCallback<List>() {
    //                public void onFailure(Throwable caught) {
    //                    Window.alert("error: " + caught);
    //                }
    //
    //                public void onSuccess(List data) {
    //                    tableViewer.setInput(new GModel(data));
    //                }
    //            };
    //    }
    /*
    public void createTable2(WidgetContainer container,
                             GLogixMetaData airportsMetaData) {
        //        final DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/d/y");
        //        final NumberFormat currency = NumberFormat.getCurrencyFormat();

        TextBox box;

        TableColumn[] columns =
            new TableColumn[airportsMetaData.getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = airportsMetaData.getTableColumn(i);
        }

        TableColumnModel cm = new TableColumnModel(columns);

        Table table = new Table(Style.SINGLE | Style.HORIZONTAL, cm);

        TableViewer viewer = new TableViewer(table);


        ClientService service = new ClientService();
        try {
            service.findService("Airports",
                                createGLogixServiceCallback(viewer));
        } catch (GLogixException e) {
            Window.alert("error: " + e);
            //label.setText(e.getMessage());
        }

        viewer.setContentProvider(new ModelContentProvider());

        //        for (int i = 0; i < columns.length; i++) {
        //            TableViewerColumn col = viewer.getViewerColumn(i);
        //            col.setLabelProvider(new CellLabelProvider() {
        //                        public void update(ViewerCell cell) {
        //                            cell.setText(((GModel)cell.getElement()).get(cell.getColumnIndex()));
        //                        }
        //                    });
        //        }

        viewer.setLabelProvider(new ITableLabelProvider() {

                    public String getColumnStyle(Object element,
                                                 int columnIndex) {
                        return null;
                    }

                    public String getColumnText(Object element, int colIndex) {
                        GModel gModel = (GModel)element;
                        return gModel.get(colIndex);

                    }

                });
        container.setLayout(new FillLayout(8));
        container.add(table);
    }
*/

    public static void fetchMDData(InputFormLayout form, 
                                   GLogixMetaData metaData, String[] params) {
        LoadingPanel.get().show("Please wait while data is being fetched...");
        ClientService service = new ClientService();

        service.findServiceMD(metaData.getServiceName(), params, 
                              createFetchMDCallback(form));

    }

    public static void postData(InputFormLayout form, GLogixMetaData metaData, 
                                HashMap<String, ArrayList<String[]>> childFormsData) {
        LoadingPanel.get().show(form, 
                                "Please wait while changes are being posted...");
        ClientService service = new ClientService();

        service.postService(metaData.getServiceName(), childFormsData, 
                            createMDFormCallback(form));

    }

    public static void postData(InputFormLayout form, GLogixMetaData metaData, 
                                String[] data) {
        LoadingPanel.get().show(form, 
                                "Please wait while changes are being posted...");
        ArrayList<String[]> list = new ArrayList<String[]>(1);
        list.add(data);
        ClientService service = new ClientService();

        service.postService(metaData.getServiceName(), list, 
                            createFormCallback(form));

    }

    public static void fetchData(SimpleTable table, GLogixMetaData metaData, 
                                 String[] params) {
        LoadingPanel.get().show(table, "Loading...");
        table.removeAll();
        ClientService service = new ClientService();
        service.findService(metaData.getServiceName(), params, 
                            createGLogixServiceCallback(table, metaData));

    }
    static LookupValues lookupValuesMetaData = new LookupValues();

    public static void populateSuggest(final SuggestBox2 lb, 
                                       final GLogixSuggestOracle oracle, 
                                       final String lookupName, 
                                       final String defaultValue) {
        if (listCache.containsKey(lookupName)) {
            populateList(lookupName, lb, oracle, listCache.get(lookupName), 
                         defaultValue);
        } else if (listCacheInProgress.contains(lookupName)) {
            Timer t = new Timer() {
                    int attempts = 0;

                    public void run() {
                        attempts++;
                        if (!listCacheInProgress.contains(lookupName)) {
                            Log.info("Done waiting for " + lookupName + 
                                     ",  Attempt:" + attempts);
                            populateList(lookupName, lb, oracle, 
                                         listCache.get(lookupName), 
                                         defaultValue);
                        } else {
                            if (attempts < 20) {
                                Log.info("Waiting for " + lookupName + 
                                         ",  Attempt:" + attempts);
                                schedule(1000);
                            }
                        }
                    }
                };
            t.schedule(1000);
        } else {
            listCacheInProgress.add(lookupName);
            Log.debug("Cache not found for lookup:" + lookupName);
            ClientService service = new ClientService();
            String[] params = lookupValuesMetaData.createRow();
            params[0] = lookupName;

            service.findService(lookupValuesMetaData.getServiceName(), params, 
                                createGLogixServiceCallback(lookupName, lb, 
                                                            oracle, 
                                                            lookupValuesMetaData, 
                                                            defaultValue));

        }
    }

    public static void preFetchLookup(String lookupName) {
        populateLookup(null, null, lookupName, null);
    }

    public static void populateLookup(final AbstractFormLayout form, 
                                      final ListBox lb, 
                                      final String lookupName, 
                                      final String defaultValue) {
        if (listCache.containsKey(lookupName)) {
            populateList(lookupName, form, lb, listCache.get(lookupName), 
                         defaultValue);
        } else if (listCacheInProgress.contains(lookupName)) {
            Timer t = new Timer() {
                    int attempts = 0;

                    public void run() {
                        attempts++;
                        if (!listCacheInProgress.contains(lookupName)) {
                            Log.info("Done waiting for " + lookupName + 
                                     ",  Attempt:" + attempts);
                            populateList(lookupName, form, lb, 
                                         listCache.get(lookupName), 
                                         defaultValue);
                        } else {
                            if (attempts < 20) {
                                Log.info("Waiting for " + lookupName + 
                                         ",  Attempt:" + attempts);
                                schedule(1000);
                            }
                        }
                    }
                };
            t.schedule(1000);
        } else {
            listCacheInProgress.add(lookupName);
            Log.debug("Cache not found for lookup:" + lookupName);
            if (lb != null) {
                lb.clear();
                lb.addItem("Please wait...");
            }
            ClientService service = new ClientService();
            String[] params = lookupValuesMetaData.createRow();
            params[0] = lookupName;

            service.findService(lookupValuesMetaData.getServiceName(), params, 
                                createGLogixServiceCallback(lookupName, form, 
                                                            lb, 
                                                            lookupValuesMetaData, 
                                                            defaultValue));

        }
    }

    public static void fetchData(SimpleTable table, GLogixMetaData metaData, 
                                 ArrayList<String[]> paramList, String andOr) {
        LoadingPanel.get().show(table, "Loading...");
        table.removeAll();
        ClientService service = new ClientService();

        service.findService(metaData.getServiceName(), paramList, andOr, 
                            createGLogixServiceCallback(table, metaData));

    }

    public static TableColumn getTableColumn(GLogixMetaData metaData, 
                                             int index) {
        int[] columnWidth = metaData.getColumnWidth();
        String[] columnTitles = metaData.getColumnTitles();
        boolean[] visible = metaData.getVisible();
        char[] types = metaData.getColumnTypes();

        int width = columnWidth[index];
        if (width > 20)
            width = 20;
        if (width < 5)
            width = 5;
        //        if (!visible[index])
        //            width = 0;
        TableColumn tableColumn = 
            new TableColumn(columnTitles[index], width * 8);
        tableColumn.setSortable(true);
        tableColumn.setResizable(true);
        //        tableColumn.setMinWidth(20);
        //        tableColumn.setMaxWidth(columnWidth[index] * 10);
        if (types[index] == 'N') {
            tableColumn.setAlignment(Style.RIGHT);
        }
        //tableColumn.hidden = !visible[index];
        return tableColumn;
    }

    public static SimpleTable createTable(GLogixMetaData metaData) {
        int[] visibleColumnIdxs = metaData.getResultColumnIndexes();
        TableColumn[] columns = new TableColumn[visibleColumnIdxs.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = getTableColumn(metaData, visibleColumnIdxs[i]);
        }
        TableColumnModel cm = new TableColumnModel(columns);
        SimpleTable table = 
            new SimpleTable(Style.SINGLE | Style.HORIZONTAL, cm, metaData);
        return table;
    }

    public static InputFormLayout createSearchForm(GLogixMetaData metaData) {
        InputFormLayout form = 
            new InputFormLayout(new String[metaData.getColumnCount()], 
                                metaData, 2, InputFormLayout.SEARCH_FORM);
        return form;
    }

    public static AdvancedSearchForm createAdvancedSearchForm(GLogixMetaData metaData) {
        AdvancedSearchForm form = new AdvancedSearchForm(metaData);
        return form;
    }
    private static final DateTimeFormat dtf = DateUtil.getDateTimeFormat();
    private static final DateTimeFormat df = DateUtil.getDateFormat();

    public static String decodeData(char columnType, String value) {
        if (value == null || value.length() == 0)
            return "";
        if (columnType == 'D') {
            value = df.format(new Date(Long.parseLong(value)));
        } else if (columnType == 'T') {
            value = dtf.format(new Date(Long.parseLong(value)));
        }
        return value;
    }

    public static String encodeData(char columnType, String value) {
        if (value == null || value.length() == 0)
            return value;
        if (columnType == 'D') {
            try {
                Date d = df.parse(value);
                value = d.getTime() + "";
            } catch (Throwable e) {
                value = "";
            }
        } else if (columnType == 'T') {
            try {
                Date d = dtf.parse(value);
                value = d.getTime() + "";
            } catch (Throwable e) {
                value = "";
            }
        }
        return value;
    }

    static void log(String msg) {
        Log.debug(msg);
    }
}
