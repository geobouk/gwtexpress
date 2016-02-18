## Step 1 ##
Create a table
```
CREATE TABLE `customers` (
  `customerNumber` bigint(20) NOT NULL AUTO_INCREMENT,
  `customerName` varchar(50) NOT NULL,
  `contactLastName` varchar(50) DEFAULT NULL,
  `contactFirstName` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `b_addressLine1` varchar(50) NOT NULL,
  `b_addressLine2` varchar(50) DEFAULT NULL,
  `b_city` varchar(50) NOT NULL,
  `b_state` varchar(50) NOT NULL,
  `b_postalCode` varchar(15) NOT NULL,
  `b_country` varchar(50) NOT NULL,
  `s_addressLine1` varchar(50) DEFAULT NULL,
  `s_addressLine2` varchar(50) DEFAULT NULL,
  `s_city` varchar(50) DEFAULT NULL,
  `s_state` varchar(50) DEFAULT NULL,
  `s_postalCode` varchar(15) DEFAULT NULL,
  `s_country` varchar(50) DEFAULT NULL,
  `salesRepEmployeeNumber` int(11) DEFAULT NULL,
  `creditLimit` double DEFAULT NULL,
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_date` date DEFAULT NULL,
  `email_address` varchar(80) DEFAULT NULL,
  PRIMARY KEY  (`customerNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
```

## Step 2 ##
Create metadata object for the above table & register on the server's Worker class

[CustomersMetaData.java](http://code.google.com/p/gwtexpress/source/browse/trunk/RPC/src/com/gwtexpress/client/rpc/model/CustomersMetaData.java)
```
public class CustomersMetaData extends AbstractGLogixMetaData {

    String[] columnTitles = 
    { "Customer Number", "Customer Name", "Contact Last Name", 
      "Contact First Name", "Phone", "Bill To Address 1", "Bill To Address 2", 
      "City", "Bill To State", "Zip", "Bill To Country", 
      "Ship To Addressline1", "Ship To Addressline2", "Ship To City", 
      "Ship To State", "Ship To Postalcode", "Ship To Country", 
      "Salesrep Employee Number", "Credit Limit", "Start Date", "-", 
      "Email Address" };

    String[] columnNames = 
    { "customerNumber", "customerName", "contactLastName", "contactFirstName", 
      "phone", "b_addressLine1", "b_addressLine2", "b_city", "b_state", 
      "b_postalCode", "b_country", "s_addressLine1", "s_addressLine2", 
      "s_city", "s_state", "s_postalCode", "s_country", 
      "salesRepEmployeeNumber", "creditLimit", "start_date", "end_date", 
      "email_address" };

    boolean[] editableCols = 
    { false, true, true, true, true, true, true, true, true, true, true, true, 
      true, true, true, true, true, true, true, true, true, true };

    boolean[] queryCols = 
    { false, true, true, true, true, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, true, true, true, 
      true };

    boolean[] allTrues = 
    { true, true, true, true, true, true, true, true, true, true, true, true, 
      true, true, true, true, true, true, true, true, true, true };

    String[] columnSuggestNames = 
    { null, null, null, null, null, null, null, "CITIES", "STATES", null, null, 
      null, null, null, null, null, null, null, null, null, null, null };

    String[] columnLookupNames = 
    { null, null, null, null, null, null, null, null, null, null, null, null, 
      null, "CITIES", "STATES", null, null, null, null, null, null, null };

    boolean[] required = 
    { true, true, false, false, false, true, false, true, true, true, true, 
      false, false, false, false, false, false, false, false, true, false, 
      false };
    int[] columnSizes = 
    { 20, 50, 50, 50, 50, 50, 50, 50, 50, 15, 50, 50, 50, 50, 50, 15, 50, 11, 
      22, 19, 10, 80 };
    char[] columnTypes = 
    { N, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, N, T, D, V };

    public CustomersMetaData() {
    }

    public String[] getColumnSuggestNames() {
        return columnSuggestNames;
    }

    public String[] getColumnLookupNames() {
        return columnLookupNames;
    }

    public int[] getColumnWidth() {
        return columnSizes;
    }

    public char[] getColumnTypes() {
        return columnTypes;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public String getTableName() {
        return "customers";
    }

    public String getServiceName() {
        return "CustomersMetaData";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getEditable() {
        return editableCols;
    }

    public boolean[] getVisible() {
        return allTrues;
    }

    public boolean[] getQueryable() {
        return queryCols;
    }

    public int[] getPkColumnIndexs() {
        return new int[] { 0 };
    }

    public boolean[] getRequired() {
        return required;
    }

    public int getRowIdIndex() {
        return 0;
    }
}
```
The below mentioned "CITIES" & "STATES" are the lookup types that are keyed into the system through the "[Setup Lookups](http://gwtexpress.com/#Lookups)" page
```
    String[] columnSuggestNames = 
    { null, null, null, null, null, null, null, "CITIES", "STATES", null, null, 
      null, null, null, null, null, null, null, null, null, null, null };

    String[] columnLookupNames = 
    { null, null, null, null, null, null, null, null, null, null, null, null, 
      null, "CITIES", "STATES", null, null, null, null, null, null, null };
```
The above codeline is all you need to add Suggest Box & Dropdown List Box to your express page. See the above metadata in action on the [Customer Creation Page](http://gwtexpress.com/#Customers?a=c)

**Worker.java**
```
  metaDataMap.put("CustomersMetaData", new CustomersMetaData());
```

## Step 3 ##
Create Express Page

[Customers.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Client/src/com/gwtexpress/client/setup/Customers.java)
```
public class Customers extends ExpressPage {

    private GLogixMetaData metaData = new CustomersMetaData();
    private String title = "Customers";

    public String getPageTitle() {
        return title;
    }

    public GLogixMetaData getMetaData() {
        return metaData;
    }

    public InputFormLayout getInputFormLayout(GLogixMetaData metaData, 
                                              String[] row) {
        int type;
        if (row == null) {
            row = metaData.createRow();
            type = FormLayout.CREATE_FORM;
        } else {
            type = FormLayout.EDIT_FORM;
        }
        final InputFormLayout form = 
            new InputFormLayout(row, metaData, 2, type, false, 
                                new String[] { "Customer Number", 
                                               "Customer Name", 
                                               "Contact Last Name", 
                                               "Contact First Name", "Phone", 
                                               "salesRepEmployeeNumber", 
                                               "creditLimit", 
                                               "email_address" }, 
                                "Customer Details");
        InputFormLayout aDates = 
            new InputFormLayout(row, metaData, 2, type, true, 
                                new String[] { "start_date", "end_date" }, 
                                "Active Dates");
        aDates.setCompact(true);
        form.addSubForm(5, 0, 1, 4, aDates);
        InputFormLayout billToAddr = 
            new InputFormLayout(row, metaData, 1, type, true, 
                                new String[] { "b_addressLine1", 
                                               "b_addressLine2", "b_city", 
                                               "b_state", "b_postalCode", 
                                               "b_country" }, 
                                "Bill To Address");

        form.addSubForm(6, 0, billToAddr);
        InputFormLayout shipToAddr = 
            new InputFormLayout(row, metaData, 1, type, true, 
                                new String[] { "s_addressLine1", 
                                               "s_addressLine2", "s_city", 
                                               "s_state", "s_postalCode", 
                                               "s_country" }, 
                                "Ship To Address");
        form.addSubForm(6, 1, shipToAddr);
        return form;
    }
}
```
**End Result** http://gwtexpress.com/#Customers

Login details for the above demo: demo/demo