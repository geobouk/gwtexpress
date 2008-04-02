package com.gwtexpress.client.ui.ex;

import com.allen_sauer.gwt.log.client.Log;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.Button;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;
import com.gwtexpress.client.ui.table.SimpleTable;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.gwtexpress.client.util.QueryParameter;

import net.mygwt.ui.client.Events;
import net.mygwt.ui.client.Style;
import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;
import net.mygwt.ui.client.widget.ContentPanel;
import net.mygwt.ui.client.widget.TabFolder;
import net.mygwt.ui.client.widget.TabItem;
import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.FillLayout;
import net.mygwt.ui.client.widget.layout.RowData;
import net.mygwt.ui.client.widget.layout.RowLayout;
import net.mygwt.ui.client.widget.table.TableItem;


public abstract class ExpressPage extends Page {

    boolean searchAllowed = true, showResults = true, editAllowed = 
        true, insertAllowed = true;
    public SimpleTable table;
    TabItem updateTab;
    InputFormLayout updateForm;
    TableItem currentUpdatRow;
    String[] autoQueryParams;
    String searchTabText = null;
    String createTabText = null;
    boolean createOrCancelMode = true;
    private InputFormLayout searchForm;

    boolean showSearchFirst = true;

    public ExpressPage() {

    }

    public void onShow() {
        Window.setTitle(getPageTitle());
        QueryParameter qp = new QueryParameter();
        String action = qp.getValue("a");
        if ("as".equals(action)) {
            searchForm.showAdvancedSearchForm();
        } else if ("c".equals(action)) {
            tabFolder.setSelection(sourceItem);
        }
    }

    public abstract GLogixMetaData getMetaData();

    public TabFolder tabFolder;
    public TabItem contentItem, sourceItem;

    public InputFormLayout getSearchFormLayout(GLogixMetaData metaData) {
        int totalCols = 2;
        int[] visible = metaData.getVisibleColumnIndexes();
        if (visible != null && visible.length <= 5) {
            totalCols = 1;
        }
        InputFormLayout form = 
            new InputFormLayout(new String[metaData.getColumnCount()], 
                                metaData, totalCols, 
                                InputFormLayout.SEARCH_FORM, false);
        return form;
    }

    public InputFormLayout getInputFormLayout(GLogixMetaData metaData, 
                                              String[] row) {
        InputFormLayout form;
        int totalCols = 2;
        int[] visible = metaData.getVisibleColumnIndexes();
        if (visible != null && visible.length <= 5) {
            totalCols = 1;
        }
        if (row == null) {
            form = 
new InputFormLayout(metaData.createRow(), metaData, totalCols, 
                    FormLayout.CREATE_FORM, false, visible, getPageTitle());
        } else {
            form = 
new InputFormLayout(row, metaData, totalCols, FormLayout.EDIT_FORM, false, 
                    visible, getPageTitle());
        }
        return form;
    }

    public void createWidget(WidgetContainer c) {
        final GLogixMetaData metaData = getMetaData();
        c.setLayout(new RowLayout(Style.VERTICAL));
        c.setScrollEnabled(false);
        int queryFieldCount = metaData.getQueryableColumnIndexes().length;
        if (queryFieldCount == 0)
            setShowResults(false);
        if (showResults)
            table = GLogixUIBuilder.createTable(metaData);
        if (searchAllowed && queryFieldCount > 0) {
            int topHeight = (queryFieldCount * 20) + 60;
            searchForm = getSearchFormLayout(metaData);
            searchForm.setResultTable(table);
            searchForm.init();

            ContentPanel searchRegion = new ContentPanel(Style.HEADER);
            searchRegion.setFrame(true);
            searchRegion.setAnimateCollapse(false);
            searchRegion.setText("Search");
            searchRegion.setLayout(new FillLayout());
            searchRegion.setScrollEnabled(true);
            searchRegion.add(searchForm);
            searchRegion.setHeight(topHeight);
            c.add(searchRegion, new RowData(RowData.FILL_HORIZONTAL));
        }
        if (showResults && editAllowed) {
            table.addListener(Events.RowDoubleClick, new Listener() {
                        public void handleEvent(BaseEvent be) {
                            if (be.type == Events.RowDoubleClick) {
                                currentUpdatRow = (TableItem)be.item;
                                String[] row = table.getCurrentRow();
                                if (updateTab == null) {
                                    updateTab = new TabItem(Style.NONE);
                                    String title = getPageTitle();
                                    updateTab.setText("Update " + title + 
                                                      " : " + 
                                                      row[metaData.getVisibleColumnIndexes()[0]]);
                                    tabFolder.add(updateTab);
                                    updateForm = 
                                            getInputFormLayout(metaData, row);
                                    updateForm.init();
                                    updateForm.addListener(Events.Close, 
                                                           new Listener() {
                                                public void handleEvent(BaseEvent be) {
                                                    if (be.item instanceof 
                                                        Button && 
                                                        "SAVE".equals(((Button)be.item).getId())) {
                                                        int[] idxs = 
                                                            metaData.getResultColumnIndexes();
                                                        String[] data = 
                                                            updateForm.getData();
                                                        for (int i = 0; 
                                                             i < idxs.length; 
                                                             i++) {
                                                            currentUpdatRow.setValue(i, 
                                                                                     GLogixUIBuilder.decodeData(metaData.getColumnTypes()[idxs[i]], 
                                                                                                                data[idxs[i]]));
                                                        }
                                                    }
                                                    contentItem.enable();
                                                    sourceItem.enable();
                                                    tabFolder.setSelection(contentItem);
                                                    updateForm.clearForm(true);
                                                    updateTab.setVisible(false);
                                                }
                                            });


                                    ContentPanel updateRegion = 
                                        new ContentPanel(Style.NONE);
                                    updateRegion.setFrame(true);
                                    updateRegion.setText("Search");
                                    updateRegion.setLayout(new FillLayout());
                                    updateRegion.setScrollEnabled(true);
                                    updateRegion.add(updateForm, 
                                                     new RowData(RowData.FILL_HORIZONTAL));
                                    updateTab.getContainer().setLayout(new FillLayout());
                                    updateTab.getContainer().add(updateRegion, 
                                                                 new RowData(RowData.FILL_HORIZONTAL));


                                    //                                    updateTab.getContainer().setScrollEnabled(true);
                                    //                                    updateTab.getContainer().add(updateForm);
                                } else {
                                    updateTab.setVisible(true);
                                }
                                if (updateForm.metaData.getChildMetaDataNames() != 
                                    null) {
                                    tabFolder.setSelection(updateTab);
                                    updateForm.fetchMDData(row);
                                } else {
                                    updateForm.setData(row);
                                    updateForm.resetForm();
                                }
                                tabFolder.setSelection(updateTab);
                                contentItem.disable();
                                sourceItem.disable();
                            }
                        }
                    });
        }
        if (showResults) {
            table.disableTextSelection(false);
            table.setWidth("100%");
            table.setTitle("Results");

            ContentPanel panel = new ContentPanel(Style.HEADER);
            panel.setFrame(true);
            panel.setAnimateCollapse(false);
            panel.setText("Results");
            panel.setLayout(new FillLayout());
            panel.add(table);

            if (!searchAllowed) {
                HorizontalPanel refreshPanel = new HorizontalPanel();
                Label label = new Label("Refresh");
                label.addStyleName("pointer");
                label.setStylePrimaryName("my-cpanel-hdr-text");
                refreshPanel.add(label);
                refreshPanel.setCellVerticalAlignment(label, 
                                                      HasVerticalAlignment.ALIGN_MIDDLE);
                refreshPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
                Image refresh = new Image("images/icons/refresh.png");
                refresh.addStyleName("pointer");
                ClickListener cl = new ClickListener() {
                        public void onClick(Widget sender) {
                            table.refresh();
                        }
                    };
                label.addClickListener(cl);
                refresh.addClickListener(cl);
                refreshPanel.add(refresh);
                panel.getHeader().addWidget(refreshPanel);
            }
            c.add(panel, new RowData(RowData.FILL_BOTH));
        }
    }
    private long startTimeMillis;

    public void init() {
        initialized = true;
        if (Log.isDebugEnabled()) {
            startTimeMillis = System.currentTimeMillis();
        }
        final GLogixMetaData metaData = getMetaData();

        int queryFieldCount = metaData.getQueryableColumnIndexes().length;
        if (queryFieldCount == 0)
            setShowResults(false);


        String title = this.getPageTitle();
        FillLayout layout = new FillLayout();
        setLayout(layout);

        tabFolder = new TabFolder(Style.NONE);
        tabFolder.setBorders(false);
        final Page fPage = this;
        add(tabFolder);
        if (showResults) {
            contentItem = new TabItem(Style.NONE);
            if (searchTabText == null)
                contentItem.setText("Search " + title);
            else
                contentItem.setText(searchTabText);

            createWidget(contentItem.getContainer());
            tabFolder.add(contentItem);
        }
        if (editAllowed && insertAllowed) {
            sourceItem = new TabItem(Style.NONE);
            if (createTabText != null) {
                sourceItem.setText(createTabText);
            } else {
                if (showResults)
                    sourceItem.setText("Create New " + title);
                else {
                    sourceItem.setText(title);
                }
            }
            final InputFormLayout form = getInputFormLayout(metaData, null);
            Listener listener = new Listener() {
                    public void handleEvent(BaseEvent be) {
                        if (sourceItem.equals(be.item)) {
                            if (contentItem != null && createOrCancelMode) {
                                contentItem.disable();
                            }
                            form.init();
                            form.setFocus();
                        }
                    }
                };
            tabFolder.addListener(Events.SelectionChange, listener);
            if (showResults) {
                form.addListener(Events.Close, new Listener() {
                            public void handleEvent(BaseEvent be) {
                                form.setDirty(true);
                                form.setFormType(FormLayout.CREATE_FORM);
                                form.clearForm(true);
                                contentItem.enable();
                                tabFolder.setSelection(contentItem);
                            }
                        });
            }
            //            sourceItem.getContainer().setScrollEnabled(true);
            //            sourceItem.getContainer().add(form, 
            //                                          new RowData(RowData.FILL_HORIZONTAL));
            //

            ContentPanel createRegion = new ContentPanel(Style.NONE);
            createRegion.setFrame(true);
            createRegion.setLayout(new FillLayout());
            createRegion.setScrollEnabled(true);
            createRegion.add(form, new RowData(RowData.FILL_HORIZONTAL));
            sourceItem.getContainer().setLayout(new FillLayout());
            sourceItem.getContainer().add(createRegion, 
                                          new RowData(RowData.FILL_HORIZONTAL));


            tabFolder.add(sourceItem);
        }
        if (autoQueryParams != null && showResults) {
            table.fetchData(autoQueryParams);
        }
        if (showSearchFirst && showResults)
            tabFolder.setSelection(contentItem);
        else
            tabFolder.setSelection(sourceItem);

        if (Log.isDebugEnabled()) {
            long endTimeMillis = System.currentTimeMillis();
            float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
            Log.debug("Load Duration for " + getPageTitle() + ": " + 
                      durationSeconds + " seconds");
        }
    }

    public void setSearchAllowed(boolean searchAllowed) {
        this.searchAllowed = searchAllowed;
    }

    public boolean isSearchAllowed() {
        return searchAllowed;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
        if (!showResults)
            searchAllowed = false;
    }

    public boolean isShowResults() {
        return showResults;
    }

    public void setEditAllowed(boolean editAllowed) {
        this.editAllowed = editAllowed;
    }

    public boolean isEditAllowed() {
        return editAllowed;
    }

    public void setAutoQueryParams(String[] autoQueryParams) {
        this.autoQueryParams = autoQueryParams;
    }

    public String[] getAutoQueryParams() {
        return autoQueryParams;
    }

    public void setShowSearchFirst(boolean showSearchFirst) {
        this.showSearchFirst = showSearchFirst;
    }

    public boolean isShowSearchFirst() {
        return showSearchFirst;
    }

    public void setSearchTabText(String searchTabText) {
        this.searchTabText = searchTabText;
    }

    public String getSearchTabText() {
        return searchTabText;
    }

    public void setCreateTabText(String createTabText) {
        this.createTabText = createTabText;
    }

    public String getCreateTabText() {
        return createTabText;
    }

    public void setCreateOrCancelMode(boolean createOrCancelMode) {
        this.createOrCancelMode = createOrCancelMode;
    }

    public boolean isCreateOrCancelMode() {
        return createOrCancelMode;
    }

    public void setInsertAllowed(boolean insertAllowed) {
        this.insertAllowed = insertAllowed;
    }

    public boolean isInsertAllowed() {
        return insertAllowed;
    }

    public SimpleTable getTable() {
        return table;
    }
}
