package com.gwtexpress.client.setup;

import com.gwtexpress.client.rpc.model.CustomersMetaData;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.ex.ExpressPage;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;

public class Customers extends ExpressPage{

   private GLogixMetaData metaData = new CustomersMetaData();
   private String title = "Customers";

   public String getPageTitle(){
      return title;
   }

   public GLogixMetaData getMetaData(){
      return metaData;
   }

   public InputFormLayout getInputFormLayout(GLogixMetaData metaData, String[] row){
      int type;
      if (row == null){
         row = metaData.createRow();
         type = FormLayout.CREATE_FORM;
      } else {
         type = FormLayout.EDIT_FORM;
      }
      String[] labels = metaData.getColumnTitles();
      final InputFormLayout form = new InputFormLayout(row, metaData, 2, type, false, 
                                                       new int[]{ 0, 1, 3, 2, 4, 21, 17, 18 }, "Customer Details");
      InputFormLayout aDates = new InputFormLayout(row, metaData, 2, type, true, new int[]{ 19, 20 }, "Active Dates");
      labels[19] = "";
      labels[20] = "-";
      aDates.setColumnTitles(labels);
      aDates.setCompact(true);
      form.addSubForm(5, 0, 1, 4, aDates);
      InputFormLayout billToAddr = new InputFormLayout(row, metaData, 1, type, true, new int[]{ 5, 6, 7, 8, 9, 10 }, 
                                                       "Bill To Address");
      labels[5] = "Street";
      labels[6] = "Apt#";
      labels[7] = "City";
      labels[8] = "State";
      labels[9] = "Zip";
      labels[10] = "Country";
      billToAddr.setColumnTitles(labels);
      form.addSubForm(6, 0, billToAddr);
      InputFormLayout shipToAddr = new InputFormLayout(row, metaData, 1, type, true, 
                                                       new int[]{ 11, 12, 13, 14, 15, 16 }, "Ship To Address");
      labels[11] = "Street";
      labels[12] = "Apt#";
      labels[13] = "City";
      labels[14] = "State";
      labels[15] = "Zip";
      labels[16] = "Country";
      shipToAddr.setColumnTitles(labels);
      form.addSubForm(6, 1, shipToAddr);
      return form;
   }

}
