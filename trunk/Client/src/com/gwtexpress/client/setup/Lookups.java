package com.gwtexpress.client.setup;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.LookupsMetaData;
import com.gwtexpress.client.ui.ex.ExpressPage;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;

public class Lookups extends ExpressPage{

   private GLogixMetaData metaData = new LookupsMetaData();
   private String title = "Lookups";

   public String getPageTitle(){
      return title;
   }

   public GLogixMetaData getMetaData(){
      return metaData;
   }

   public InputFormLayout getInputFormLayout(GLogixMetaData metaData, String[] row){
      InputFormLayout form;
      int type;
      if (row == null){
         row = metaData.createRow();
         type = FormLayout.CREATE_FORM;
      } else {
         type = FormLayout.EDIT_FORM;
      }
      form = new InputFormLayout(row, metaData, 1, type, false, new String[]{ "LOOKUP_TYPE", "LOOKUP_VALUE", "LOOKUP_MEANING", "DISPLAY_SEQ"}, "Lookups");
      InputFormLayout subForm = new InputFormLayout(row, metaData, 2, type, true, new String[]{ "START_DATE", "END_DATE" }, null);
      String[] labels = metaData.getColumnTitles();
      labels[5] = "Effective Dates";
      labels[6] = "-";
      subForm.setColumnTitles(labels);
      subForm.setCompact(true);
      form.addSubForm(4, 0, subForm);
      return form;
   }

}
